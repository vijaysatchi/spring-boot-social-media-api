import { renderProfileHeader, renderCompactSidebar, initStickyHeader } from '../components/ProfileHeader.js';
import { ProfileFeedLoader } from './profileFeedLoader.js';
import { showToast } from '../utils/toast.js';
import { initPostComposer } from "./feed.js";
import { fetchSimple } from "../utils/api.js";

let profileFeedLoader = null;
let currentProfileUserId = null;

export async function initProfile(userId) {
    currentProfileUserId = userId;
    try {
        const user = await fetchSimple(`/api/user/${userId}`);
        const { getCurrentUser } = await import('../auth/state.js');
        const currentUser = getCurrentUser();
        const viewerId = currentUser?.id || null;
        const isOwnProfile = currentUser?.id === user?.id;

        renderProfileHeader(user, viewerId);
        renderCompactSidebar(user);
        initStickyHeader();
        if (isOwnProfile) {
            const composerContainer = document.getElementById('profilePostComposer');
            if (composerContainer) {
                composerContainer.style.display = 'block';
                initPostComposer(); // similar to feed's composer but targeting profile feed
            }
        }

        profileFeedLoader = new ProfileFeedLoader('userPostsFeed', userId, {
            showCommentsButton: true,
            onError: (err) => {
                console.error(err);
                showToast('Failed to load user posts', 'error');
            }
        });
        profileFeedLoader.initInfiniteScroll('profileSentinel');
        profileFeedLoader.loadMore();
    } catch (err) {
        console.error(err);
        const container = document.getElementById('profileHeaderContainer');
        if (container) {
            container.innerHTML = '<div class="error-card">Failed to load profile. Please refresh.</div>';
        }
    }
}