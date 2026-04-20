import { escapeHtml } from '../utils/dom.js';
import { formatRelativeTime } from '../utils/date.js';
import { getAvatarUrl } from '../config/constants.js';
import { getCurrentUser } from '../auth/state.js';

export function createCommentElement(comment, { onLikeToggle, onEdit, onDelete }) {
    const currentUser = getCurrentUser();
    const isOwner = currentUser?.id === comment.userId;
    const commentDiv = document.createElement('div');
    commentDiv.className = 'comment';
    commentDiv.dataset.commentId = comment.id;

    const avatar = getAvatarUrl(comment.profilePictureUrl);
    const editInfo = comment.updatedAt ? `<span class="edit-info-comment"><i class="fas fa-pen"></i> Edited ${formatRelativeTime(comment.updatedAt)}</span>` : '';

    const menuHtml = isOwner ? `
        <div class="comment-menu">
            <button class="comment-menu-btn"><i class="fas fa-ellipsis-v"></i></button>
            <div class="comment-menu-dropdown">
                <button class="edit-comment">Edit</button>
                <button class="delete-comment">Delete</button>
            </div>
        </div>
    ` : '';

    commentDiv.innerHTML = `
        <img src="${avatar}" class="comment-avatar-small" data-user-id="${comment.userId}">
        <div class="comment-content">
            <div class="comment-header">
                <div class="comment-user-info">
                    <span class="comment-user-name" data-user-id="${comment.userId}">${escapeHtml(comment.userName)}</span>
                    <span class="comment-time"><i class="far fa-clock"></i> ${formatRelativeTime(comment.dateCreated)}</span>
                    ${editInfo}
                </div>
                ${menuHtml}
            </div>
            <div class="comment-text">${escapeHtml(comment.content)}</div>
            <div class="comment-actions">
                <button class="comment-like-btn ${comment.liked ? 'liked' : ''}" data-comment-id="${comment.id}">
                    <i class="${comment.liked ? 'fas' : 'far'} fa-heart"></i> <span>${comment.likeCount}</span>
                </button>
            </div>
        </div>
    `;

    // Like button
    const likeBtn = commentDiv.querySelector('.comment-like-btn');
    likeBtn?.addEventListener('click', async (e) => {
        e.stopPropagation();
        if (!currentUser) return window.location.href = '/login';
        await onLikeToggle(comment.id, likeBtn);
    });

    // Profile navigation
    commentDiv.querySelectorAll('[data-user-id]').forEach(el => {
        el.addEventListener('click', () => window.location.href = `/profile/${el.dataset.userId}`);
    });

    // Edit/Delete menu
    if (isOwner) {
        const menuBtn = commentDiv.querySelector('.comment-menu-btn');
        const dropdown = commentDiv.querySelector('.comment-menu-dropdown');
        menuBtn?.addEventListener('click', (e) => {
            e.stopPropagation();
            const visible = dropdown.style.display === 'block';
            document.querySelectorAll('.comment-menu-dropdown').forEach(d => d.style.display = 'none');
            dropdown.style.display = visible ? 'none' : 'block';
        });
        commentDiv.querySelector('.edit-comment')?.addEventListener('click', () => onEdit(comment.id, commentDiv));
        commentDiv.querySelector('.delete-comment')?.addEventListener('click', () => onDelete(comment.id, commentDiv));
        document.addEventListener('click', () => dropdown && (dropdown.style.display = 'none'));
    }

    return commentDiv;
}