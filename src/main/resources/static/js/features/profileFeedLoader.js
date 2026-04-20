import { createPostElement } from '../components/Post.js';
import { fetchSimple, toggleLike } from '../utils/api.js';
import { showToast } from '../utils/toast.js';
import { InfiniteScroll } from '../utils/scroll.js';
import { getCurrentUser } from '../auth/state.js';

export class ProfileFeedLoader {
    constructor(feedElementId, userId, options = {}) {
        this.feedElement = document.getElementById(feedElementId);
        this.userId = userId;
        this.page = 0;
        this.loading = false;
        this.finished = false;
        this.showCommentsButton = options.showCommentsButton !== false;
        this.onError = options.onError || (() => {});
        this.scroller = null;
    }

    initInfiniteScroll(sentinelId) {
        const sentinel = document.getElementById(sentinelId);
        if (!sentinel) return;
        this.scroller = new InfiniteScroll(async () => {
            if (this.finished) return { finished: true };
            await this.loadMore();
            return { finished: this.finished };
        }, { rootMargin: '200px' });
        this.scroller.observe(sentinel);
    }

    async loadMore() {
        if (this.loading || this.finished) return;
        this.loading = true;
        try {
            const posts = await fetchSimple(`/api/user/${this.userId}/post/${this.page}`);
            if (!posts.length) {
                this.finished = true;
                if (this.page === 0) {
                    this.feedElement.innerHTML = '<div class="empty-feed"><i class="far fa-newspaper"></i> No posts yet.</div>';
                }
                return;
            }
            const fragment = document.createDocumentFragment();
            posts.forEach(post => {
                const postEl = createPostElement(post, {
                    onCommentClick: (id) => window.location.href = `/post/${id}`,
                    onLikeToggle: async (postId, btn) => {
                        if (!getCurrentUser()) return window.location.href = '/login';
                        await toggleLike(`/api/post/${postId}/like`, btn, showToast);
                    },
                    enableEdit: false
                });
                fragment.appendChild(postEl);
            });
            this.feedElement.appendChild(fragment);
            this.page++;
        } catch (err) {
            this.onError(err);
            this.finished = true;
        } finally {
            this.loading = false;
        }
    }

    reset() {
        this.page = 0;
        this.finished = false;
        this.loading = false;
        this.feedElement.innerHTML = '';
        if (this.scroller) this.scroller.reset();
        this.loadMore();
    }
}