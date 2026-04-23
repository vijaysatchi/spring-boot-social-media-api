import { getCurrentUser, addAuthListener } from "../auth/state.js";
import {fetchSimple, fetchWithError} from '../utils/api.js';
import { createPostElement } from '../components/Post.js';
import { showToast } from '../utils/toast.js';
import { InfiniteScroll } from '../utils/scroll.js';
import { getAvatarUrl } from "../config/constants.js";

export function initPostComposer() {
    const composer = document.getElementById('postComposer');
    const avatar = document.getElementById('composerAvatar');
    const textarea = document.getElementById('postCaption');
    const submitBtn = document.getElementById('submitPostBtn');
    const charCount = document.getElementById('charCount');
    if (!composer || !textarea) return;

    const updateUI = () => {
        const user = getCurrentUser();
        if (user) {
            composer.style.display = 'block';
            avatar.src = getAvatarUrl(user.profilePictureUrl);
            textarea.disabled = false;
            updateCharCount();
        } else {
            composer.style.display = 'none';
        }
    };

    const updateCharCount = () => {
        const len = textarea.value.length;
        charCount.textContent = len;
        submitBtn.disabled = len === 0 || len > 255;
        charCount.style.color = len > 230 ? 'var(--primary)' : 'var(--gray)';
    };

    textarea.addEventListener('input', updateCharCount);

    const handleSubmit = async () => {
        const caption = textarea.value.trim();
        if (!caption) return;
        submitBtn.disabled = true;
        submitBtn.textContent = 'Posting...';
        try {
            const newPost = await fetchWithError('/api/post', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ caption })
            });
            textarea.value = '';
            updateCharCount();
            const postEl = createPostElement(newPost, {
                onCommentClick: (id) => window.location.href = `/post/${id}`
            });
            const feed = document.getElementById('feed') || document.getElementById("userPostsFeed");
            feed.insertBefore(postEl, feed.firstChild);
            postEl.scrollIntoView({ behavior: 'smooth', block: 'start' });
            showToast('Post created');
        } catch {
            showToast('Failed to create post', 'error');
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Post';
        }
    };

    submitBtn.addEventListener('click', handleSubmit);
    textarea.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey && !submitBtn.disabled) {
            e.preventDefault();
            handleSubmit();
        }
    });

    addAuthListener(updateUI);
    updateUI();
}

export function initFeed(feedType) {
    const feedContainer = document.getElementById('feed');
    if (!feedContainer) return;

    let page = 0;
    let finished = false;

    const loadMore = async () => {
        if (finished) return { finished: true };
        try {

            const posts = (feedType === 'global')
                ? await fetchSimple(`/api/post/feed/${feedType}?page=${page}`)
                : await fetchWithError(`/api/post/feed/${feedType}?page=${page}`);
            if (!posts.length) {
                finished = true;
                return { finished: true };
            }
            const fragment = document.createDocumentFragment();
            posts.forEach(post => {
                fragment.appendChild(createPostElement(post, {
                    onCommentClick: (id) => window.location.href = `/post/${id}`
                }));
            });
            feedContainer.appendChild(fragment);
            page++;
            return { finished: false };
        } catch (err) {
            console.error(err);
            showToast('Failed to load posts', 'error');
            finished = true;
            return { finished: true };
        }
    };

    initPostComposer();

    const sentinel = document.getElementById('sentinel');
    if (sentinel) {
        const scroller = new InfiniteScroll(loadMore, { rootMargin: '200px' });
        scroller.observe(sentinel);
        scroller.load();
    } else {
        loadMore();
    }
}