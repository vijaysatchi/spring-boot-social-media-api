import { escapeHtml } from '../utils/dom.js';
import { formatRelativeTime } from '../utils/date.js';
import { getAvatarUrl } from '../config/constants.js';
import { showToast } from '../utils/toast.js';
import { showFollowersModal } from './FollowersModal.js';
import { showEditProfileModal } from './EditProfileModal.js';
import { fetchRawWithError } from "../utils/api.js";

export function renderProfileHeader(user, currentViewerId) {
    const container = document.getElementById('profileHeaderContainer');
    if (!container) return;

    const isOwnProfile = currentViewerId === user.id;
    const isLoggedIn = !!currentViewerId;
    const joinDate = formatRelativeTime(user.dateCreated);
    const mutualCount = user.mutualFollowersCount || 0;

    let actionButtonHtml = '';
    if (isLoggedIn && !isOwnProfile) {
        const isFollowing = user.currentlyFollowing === true;
        actionButtonHtml = `
            <button id="followActionBtn" class="${isFollowing ? 'unfollow-btn' : 'follow-btn'}">
                <i class="fas ${isFollowing ? 'fa-user-minus' : 'fa-user-plus'}"></i>
                ${isFollowing ? 'Unfollow' : 'Follow'}
            </button>
        `;
    } else if (isOwnProfile && isLoggedIn) {
        actionButtonHtml = `<button id="editProfileBtn" class="edit-profile-btn"><i class="fas fa-edit"></i> Edit Profile</button>`;
    }

    const headerHtml = `
        <div class="profile-header" id="profileHeader">
            <div class="profile-cover" style="background: ${user.bannerColour || 'linear-gradient(135deg, #e28b8b, #c06e6e)'};"></div>
            <div class="profile-header-row">
                <img src="${getAvatarUrl(user.profilePictureUrl)}" class="profile-avatar" alt="${escapeHtml(user.name)}">
                <div class="profile-header-buttons">${actionButtonHtml}</div>
            </div>
            <div class="profile-info">
                <h1 class="profile-name">${escapeHtml(user.name)}</h1>
                ${user.bio ? `<div class="profile-bio">${escapeHtml(user.bio)}</div>` : ''}
                <div class="profile-stats">
                    <div class="stat-item" data-type="followers">
                        <span class="stat-number">${user.followersCount || 0}</span>
                        <span class="stat-label">Followers</span>
                    </div>
                    <div class="stat-item" data-type="following">
                        <span class="stat-number">${user.followingCount || 0}</span>
                        <span class="stat-label">Following</span>
                    </div>
                    ${!isOwnProfile && isLoggedIn ? `
                    <div class="stat-item" data-type="mutuals">
                        <span class="stat-number">${user.mutualFollowersCount}</span>
                        <span class="stat-label">Mutual</span>
                    </div>
                    ` : ''}
                </div>
                <div class="profile-meta">
                    <i class="fas fa-calendar-alt"></i> Joined ${joinDate} ago
                </div>
            </div>
        </div>
    `;
    container.innerHTML = headerHtml;

    // Stats click
    document.querySelectorAll('.stat-item').forEach(stat => {
        stat.addEventListener('click', () => showFollowersModal(user.id, stat.dataset.type));
    });

    // Follow/Unfollow button
    const followBtn = document.getElementById('followActionBtn');
    if (followBtn && !isOwnProfile) {
        followBtn.addEventListener('click', async () => {
            if (!isLoggedIn)
                return window.location.href = '/login';
            const isFollowing = user.currentlyFollowing === true;
            followBtn.disabled = true;
            const originalHtml = followBtn.innerHTML;
            followBtn.innerHTML = '<i class="fas fa-spinner fa-pulse"></i>';
            try {
                if (isFollowing) {
                    await fetchRawWithError(`/api/user/${user.id}/unfollow`, { method: 'DELETE', credentials: 'include' });
                    user.currentlyFollowing = false;
                    user.followersCount = Math.max(0, (user.followersCount || 0) - 1);
                    showToast(`Unfollowed ${user.name}`);
                } else {
                    await fetchRawWithError(`/api/user/${user.id}/follow`, { method: 'POST', credentials: 'include' });
                    user.currentlyFollowing = true;
                    user.followersCount = (user.followersCount || 0) + 1;
                    showToast(`Following ${user.name}`);
                }
                // Update button appearance
                const newFollowing = !isFollowing;
                followBtn.innerHTML = `<i class="fas ${newFollowing ? 'fa-user-minus' : 'fa-user-plus'}"></i> ${newFollowing ? 'Unfollow' : 'Follow'}`;
                followBtn.className = newFollowing ? 'unfollow-btn' : 'follow-btn';
                // Update follower count display
                const countSpan = document.querySelector('.stat-item[data-type="followers"] .stat-number');
                if (countSpan) countSpan.textContent = user.followersCount;
            } catch (err) {
                console.error(err);
                showToast('Action failed', 'error');
                followBtn.innerHTML = originalHtml;
            } finally {
                followBtn.disabled = false;
            }
        });
    }

    // Edit profile button
    const editBtn = document.getElementById('editProfileBtn');
    if (editBtn && isOwnProfile) {
        editBtn.addEventListener('click', () => showEditProfileModal(user));
    }
}

export function renderCompactSidebar(user) {
    const container = document.getElementById('compactProfileSidebar');
    if (!container) return;
    container.innerHTML = `
        <div class="compact-avatar-wrapper">
            <img src="${getAvatarUrl(user.profilePictureUrl)}" class="compact-avatar" alt="${escapeHtml(user.name)}">
        </div>
        <div class="compact-name">${escapeHtml(user.name)}</div>
        ${user.bio ? `<div class="compact-bio">${escapeHtml(user.bio.substring(0, 60))}${user.bio.length > 60 ? '...' : ''}</div>` : '<div class="compact-bio no-bio">No bio yet</div>'}
    `;
}

export function initStickyHeader() {
    const header = document.getElementById('profileHeader');
    const compactSidebar = document.getElementById('compactProfileSidebar');
    if (!header || !compactSidebar) return;
    const observer = new IntersectionObserver(
        ([entry]) => {
            compactSidebar.classList.toggle('show', !entry.isIntersecting);
        },
        { threshold: 0, rootMargin: '-1px 0px 0px 0px' }
    );
    observer.observe(header);
}