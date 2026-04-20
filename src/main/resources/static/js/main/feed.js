import { initFeed } from '../features/feed.js';
import { initMobileMenu } from '../ui/mobileMenu.js';
import { initSidebar } from '../ui/sidebar.js';
import { updateScrollProgress } from '../ui/scrollProgress.js';

export function start() {
    initFeed(window.__PAGE_DATA__.feedType);
    initMobileMenu();
    initSidebar();
    window.addEventListener('scroll', updateScrollProgress);
    if (!document.querySelector('.toast-container')) {
        const container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
}