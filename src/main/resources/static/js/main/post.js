import { initPost } from '../features/post.js';
import { initMobileMenu } from '../ui/mobileMenu.js';
import { initSidebar } from '../ui/sidebar.js';
import { initCommentInput } from '../ui/commentUI.js';
import { showToast } from '../utils/toast.js';

export function start() {
    const postId = window.__PAGE_DATA__.postId;
    if (!postId) {
        showToast('Invalid post', 'error');
        setTimeout(() => window.location.href = '/feed/global', 1500);
        return;
    }
    initPost(postId);
    initMobileMenu();
    initSidebar();
    initCommentInput('commentInput', 'postCommentBtn', async (text) => {
        const { postComment } = await import('../features/post.js');
        await postComment(text);
    });

    // Back button
    const backBtn = document.getElementById('backBtn');
    backBtn?.addEventListener('click', () => window.history.back());
}