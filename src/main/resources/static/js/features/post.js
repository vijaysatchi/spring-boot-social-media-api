import {fetchRawWithError, fetchSimple, fetchWithError, toggleLike} from '../utils/api.js';
import { createPostElement } from '../components/Post.js';
import { createCommentElement } from '../components/Comment.js';
import { showToast } from '../utils/toast.js';
import { InfiniteScroll } from '../utils/scroll.js';
import { getCurrentUser } from '../auth/state.js';

let currentPost = null;
let postId = null;
let commentsFeed = null;
let commentSentinel = null;
let commentScroller = null;

export async function initPost(id) {
    postId = id;
    await loadPost();
    setupCommentInfiniteScroll();
}

async function loadPost() {
    const container = document.getElementById('postContainer');
    try {
        const post = await fetchSimple(`/api/post/${postId}`);
        currentPost = post;
        const postEl = createPostElement(post, {
            onCommentClick: null, // no need on post page
            enableEdit: true,
            onDelete: async (id, el) => {
                if (confirm('Delete this post?')) {
                    await fetchRawWithError(`/api/post/${id}`, { method: 'DELETE' });
                    showToast('Post deleted');
                    window.location.href = '/feed/global';
                }
            }
        });
        container.innerHTML = '';
        container.appendChild(postEl);

        document.getElementById('commentsSection').style.display = 'block';
        await loadComments(true);
    } catch (err) {
        console.error(err);
        container.innerHTML = `<div class="error-card">Failed to load post. <button onclick="location.reload()">Retry</button></div>`;
    }
}

let commentPage = 0;
let commentFinished = false;
let commentLoading = false;

async function loadComments(reset = false) {
    if (reset) {
        commentPage = 0;
        commentFinished = false;
        commentLoading = false;
        const feed = document.getElementById('commentsFeed');
        if (feed) feed.innerHTML = '';
        if (commentScroller) commentScroller.reset();
    }
    if (commentLoading || commentFinished) return;
    commentLoading = true;

    try {
        const comments = await fetchSimple(`/api/post/${postId}/comments?page=${commentPage}`);
        const feed = document.getElementById('commentsFeed');
        if (!feed) return;

        if (comments.length === 0) {
            commentFinished = true;
            if (commentPage === 0) {
                feed.innerHTML = `<div class="empty-comments"><i class="far fa-comment-dots"></i> No comments yet. Be the first!</div>`;
            }
            return;
        }

        const fragment = document.createDocumentFragment();
        comments.forEach(comment => {
            const commentEl = createCommentElement(comment, {
                onLikeToggle: async (commentId, btn) => {
                    if (!getCurrentUser()) return window.location.href = '/login';
                    await toggleLike(`/api/comment/${commentId}/like`, btn, showToast);
                },
                onEdit: (commentId, commentDiv) => editComment(commentId, commentDiv),
                onDelete: async (commentId, commentDiv) => {
                    if (confirm('Delete comment?')) {
                        await fetchRawWithError(`/api/comment/${commentId}`, { method: 'DELETE' });
                        commentDiv.remove();
                        updateCommentCount(-1);
                        showToast('Comment deleted');
                    }
                }
            });
            fragment.appendChild(commentEl);
        });
        feed.appendChild(fragment);
        commentPage++;
        updateCommentCount(comments.length, true);
    } catch (err) {
        console.error(err);
        showToast('Failed to load comments', 'error');
    } finally {
        commentLoading = false;
    }
}

function updateCommentCount(deltaOrTotal, isSet = false) {
    const countSpan = document.getElementById('commentCount');
    if (!countSpan) return;
    let current = parseInt(countSpan.textContent, 10) || 0;
    if (isSet && deltaOrTotal !== undefined) current = deltaOrTotal;
    else current += deltaOrTotal;
    countSpan.textContent = `${current} comment${current !== 1 ? 's' : ''}`;
}

export async function postComment(text) {
    if (!text.trim()) return;
    try {
        const newComment = await fetchWithError(`/api/post/${postId}/comment`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content: text.trim() })
        });
        const feed = document.getElementById('commentsFeed');
        // Remove empty placeholder if exists
        if (feed.children.length === 1 && feed.children[0].classList?.contains('empty-comments')) {
            feed.innerHTML = '';
        }
        const commentEl = createCommentElement(newComment, {
            onLikeToggle: async (commentId, btn) => {
                if (!getCurrentUser()) return window.location.href = '/login';
                await toggleLike(`/api/comment/${commentId}/like`, btn, showToast);
            },
            onEdit: (commentId, commentDiv) => editComment(commentId, commentDiv),
            onDelete: async (commentId, commentDiv) => {
                if (confirm('Delete comment?')) {
                    await fetchRawWithError(`/api/comment/${commentId}`, { method: 'DELETE' });
                    commentDiv.remove();
                    updateCommentCount(-1);
                    showToast('Comment deleted');
                }
            }
        });
        feed.prepend(commentEl);
        updateCommentCount(1);
        commentEl.scrollIntoView({ behavior: 'smooth', block: 'center' });
    } catch (err) {
        console.error(err);
        showToast('Failed to post comment', 'error');
        throw err;
    }
}

async function editComment(commentId, commentDiv) {
    const textDiv = commentDiv.querySelector('.comment-text');
    const originalText = textDiv.textContent;
    const container = document.createElement('div');
    container.className = 'edit-comment-container';
    const textarea = document.createElement('textarea');
    textarea.value = originalText;
    textarea.maxLength = 255;
    const saveBtn = document.createElement('button');
    saveBtn.textContent = 'Save';
    const cancelBtn = document.createElement('button');
    cancelBtn.textContent = 'Cancel';
    container.append(textarea, saveBtn, cancelBtn);
    textDiv.style.display = 'none';
    textDiv.after(container);
    textarea.focus();

    const save = async () => {
        const newText = textarea.value.trim();
        if (!newText || newText === originalText) return cancel();
        saveBtn.disabled = true;
        try {
            const updated = await fetchWithError(`/api/comment/${commentId}`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ content: newText })
            });
            textDiv.textContent = updated.content;
            // Update edit info if exists
            let editSpan = commentDiv.querySelector('.edit-info-comment');
            if (!editSpan && updated.updatedAt) {
                editSpan = document.createElement('span');
                editSpan.className = 'edit-info-comment';
                commentDiv.querySelector('.comment-header .comment-user-info').appendChild(editSpan);
            }
            if (editSpan && updated.updatedAt) {
                const { formatRelativeTime } = await import('../utils/date.js');
                editSpan.innerHTML = `<i class="fas fa-pen"></i> Edited ${formatRelativeTime(updated.updatedAt)}`;
            }
            showToast('Comment updated');
            cancel();
        } catch {
            showToast('Update failed', 'error');
            cancel();
        }
    };
    const cancel = () => { container.remove(); textDiv.style.display = 'block'; };
    saveBtn.onclick = save;
    cancelBtn.onclick = cancel;
    textarea.addEventListener('keydown', e => e.key === 'Enter' && !e.shiftKey && save());
}

function setupCommentInfiniteScroll() {
    commentsFeed = document.getElementById('commentsFeed');
    commentSentinel = document.getElementById('commentSentinel');
    if (!commentsFeed || !commentSentinel) return;
    const loadMoreComments = async () => {
        if (commentFinished) return { finished: true };
        await loadComments();
        return { finished: commentFinished };
    };
    commentScroller = new InfiniteScroll(loadMoreComments, { rootMargin: '200px' });
    commentScroller.observe(commentSentinel);
    commentScroller.load();
}