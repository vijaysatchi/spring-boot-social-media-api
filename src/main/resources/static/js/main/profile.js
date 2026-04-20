import { initProfile } from '../features/profile.js';
import { initMobileMenu } from '../ui/mobileMenu.js';
import { initSidebar } from '../ui/sidebar.js';
import { showToast } from '../utils/toast.js';

export function start() {
    const userId = window.__PAGE_DATA__.profileUserId;
    if (!userId) {
        showToast('Invalid profile', 'error');
        setTimeout(() => window.location.href = '/feed/global', 1500);
        return;
    }
    initProfile(userId);
    initMobileMenu();
    initSidebar();
}