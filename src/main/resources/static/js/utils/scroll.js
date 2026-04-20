export class InfiniteScroll {
    constructor(loadMoreFn, options = { threshold: 0.5, rootMargin: '200px' }) {
        this.loadMoreFn = loadMoreFn;
        this.loading = false;
        this.finished = false;
        this.observer = new IntersectionObserver(async (entries) => {
            if (entries[0].isIntersecting && !this.loading && !this.finished) {
                await this.load();
            }
        }, { threshold: options.threshold, rootMargin: options.rootMargin });
    }

    async load() {
        if (this.loading || this.finished) return;
        this.loading = true;
        const result = await this.loadMoreFn();
        this.finished = result.finished;
        this.loading = false;
    }

    observe(element) {
        if (element) this.observer.observe(element);
    }

    disconnect() {
        this.observer.disconnect();
    }

    reset() {
        this.finished = false;
        this.loading = false;
    }
}