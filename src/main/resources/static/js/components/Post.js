import { escapeHtml } from '../utils/dom.js';
import { formatRelativeTime } from '../utils/date.js';
import { getAvatarUrl } from '../config/constants.js';
import { getCurrentUser } from '../auth/state.js';
import { fetchWithError, toggleLike } from '../utils/api.js';
import { showToast } from '../utils/toast.js';

export function createPostElement(post, { onCommentClick, enableEdit = false, onDelete } = {}) {
    const currentUser = getCurrentUser();
    const isOwner = currentUser?.id === post.userId;
    const postDiv = document.createElement('div');
    postDiv.className = 'post';
    postDiv.dataset.postId = post.id;

    const editInfo = post.updatedAt ? `<span class="edit-info"><i class="fas fa-pen"></i> Edited ${formatRelativeTime(post.updatedAt)}</span>` : '';

    const menuHtml = (isOwner && enableEdit) ? `
        <div class="post-menu">
            <button class="menu-btn"><i class="fas fa-ellipsis-h"></i></button>
            <div class="menu-dropdown">
                <button class="edit-post">Edit</button>
                <button class="delete-post">Delete</button>
            </div>
        </div>
    ` : '';

    postDiv.innerHTML = `
        <div class="post-header">
            <img src="${getAvatarUrl(post.profilePictureUrl)}" class="profile-pic" data-user-id="${post.userId}">
            <div class="post-user-info">
                <span class="post-user-name" data-user-id="${post.userId}">${escapeHtml(post.userName)}</span>
                <div class="post-meta">
                    <i class="far fa-clock"></i> ${formatRelativeTime(post.dateCreated)}
                    ${editInfo}
                </div>
            </div>
            ${menuHtml}
        </div>
        <div class="post-content">
            <div class="post-caption">${escapeHtml(post.caption)}</div>
        </div>
        <div class="post-actions">
            <button class="action-btn like-btn ${post.liked ? 'liked' : ''}" data-post-id="${post.id}">
                <i class="${post.liked ? 'fas' : 'far'} fa-heart"></i> <span class="like-count">${post.likeCount ? post.likeCount : ''}</span>
            </button>
            <button class="action-btn comment-btn" data-post-id="${post.id}">
                <i class="far fa-comment"></i> Comment
            </button>
        </div>
    `;

    // Like button
    const likeBtn = postDiv.querySelector('.like-btn');
    likeBtn?.addEventListener('click', async (e) => {
        e.stopPropagation();
        if (!currentUser) return window.location.href = '/login';
        await toggleLike(`/api/post/${post.id}/like`, likeBtn, showToast);
    });

    // Comment button
    const commentBtn = postDiv.querySelector('.comment-btn');
    commentBtn?.addEventListener('click', () => onCommentClick?.(post.id));

    // Profile navigation
    postDiv.querySelectorAll('[data-user-id]').forEach(el => {
        el.addEventListener('click', () => window.location.href = `/profile/${el.dataset.userId}`);
    });

    // Edit/Delete menu
    if (isOwner && enableEdit) {
        const menuBtn = postDiv.querySelector('.menu-btn');
        const dropdown = postDiv.querySelector('.menu-dropdown');
        menuBtn?.addEventListener('click', (e) => {
            e.stopPropagation();
            const isVisible = dropdown.style.display === 'block';
            document.querySelectorAll('.menu-dropdown').forEach(d => d.style.display = 'none');
            dropdown.style.display = isVisible ? 'none' : 'block';
        });
        postDiv.querySelector('.edit-post')?.addEventListener('click', () => enterEditMode(post, postDiv));
        postDiv.querySelector('.delete-post')?.addEventListener('click', () => onDelete?.(post.id, postDiv));
        document.addEventListener('click', () => dropdown && (dropdown.style.display = 'none'));
    }

    return postDiv;
}

async function enterEditMode(post, postDiv) {
    const captionDiv = postDiv.querySelector('.post-caption');
    const original = captionDiv.textContent;
    const container = document.createElement('div');
    container.className = 'edit-post-container';
    const textarea = document.createElement('textarea');
    textarea.value = original;
    textarea.maxLength = 255;
    const saveBtn = document.createElement('button');
    saveBtn.textContent = 'Save';
    const cancelBtn = document.createElement('button');
    cancelBtn.textContent = 'Cancel';
    container.append(textarea, saveBtn, cancelBtn);
    captionDiv.style.display = 'none';
    captionDiv.after(container);
    textarea.focus();

    const save = async () => {
        const newCaption = textarea.value.trim();
        if (!newCaption || newCaption === original) return cancel();
        saveBtn.disabled = true;
        try {
            const updated = await fetchWithError(`/api/post/${post.id}`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ caption: newCaption })
            });
            captionDiv.textContent = escapeHtml(updated.caption);
            // update edit info
            const metaDiv = postDiv.querySelector('.post-meta');
            let editSpan = metaDiv.querySelector('.edit-info');
            if (!editSpan && updated.updatedAt) {
                editSpan = document.createElement('span');
                editSpan.className = 'edit-info';
                metaDiv.appendChild(editSpan);
            }
            if (editSpan && updated.updatedAt) {
                editSpan.innerHTML = `<i class="fas fa-pen"></i> Edited ${formatRelativeTime(updated.updatedAt)}`;
            }
            showToast('Post updated');
            cancel();
        } catch (err) {
            showToast('Failed to update', 'error');
            cancel();
        }
    };
    const cancel = () => { container.remove(); captionDiv.style.display = 'block'; };
    saveBtn.onclick = save;
    cancelBtn.onclick = cancel;
    textarea.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            save();
        }
    });
}