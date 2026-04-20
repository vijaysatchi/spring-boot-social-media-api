import { fetchSimple } from '../utils/api.js';
import { getAvatarUrl } from '../config/constants.js';
import { showToast } from '../utils/toast.js';
import { isLoggedIn } from '../auth/state.js';
import { escapeHtml } from "../utils/dom.js";

let currentUserId = null;
let activeTab = 'followers';
let pagination = {
    followers: { page: 0, finished: false, loading: false },
    following: { page: 0, finished: false, loading: false },
    mutuals: { page: 0, finished: false, loading: false }
};
let modalScrollHandler = null;

export function showFollowersModal(userId, initialTab = 'followers') {
    if (!isLoggedIn()) {
        showToast('You must be logged in to view followers', 'error');
        return;
    }
    currentUserId = userId;
    activeTab = initialTab;
    // Reset pagination for this user
    pagination = {
        followers: { page: 0, finished: false, loading: false },
        following: { page: 0, finished: false, loading: false },
        mutuals: { page: 0, finished: false, loading: false }
    };

    const modal = document.getElementById('followersModal');
    const modalTitle = document.getElementById('modalTitle');
    modalTitle.textContent = initialTab === 'followers' ? 'Followers' : initialTab === 'following' ? 'Following' : 'Mutual Followers';

    // Clear lists
    document.getElementById('followersList').innerHTML = '';
    document.getElementById('followingList').innerHTML = '';
    document.getElementById('mutualList').innerHTML = '';

    // Show active tab
    document.querySelectorAll('.modal-tabs .tab-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.dataset.tab === activeTab) btn.classList.add('active');
    });
    document.getElementById('followersList').style.display = activeTab === 'followers' ? 'flex' : 'none';
    document.getElementById('followingList').style.display = activeTab === 'following' ? 'flex' : 'none';
    document.getElementById('mutualList').style.display = activeTab === 'mutuals' ? 'flex' : 'none';

    if (window.innerWidth <= 768) {
        modal.style.padding = '0 10px';
        const modalContent = modal.querySelector('.modal-content');
        if (modalContent) {
            modalContent.style.width = '100%';
            modalContent.style.maxWidth = '100%';
            modalContent.style.margin = '0 auto';
            modalContent.style.borderRadius = '28px';
        }
    } else {
        modal.style.padding = '';
        const modalContent = modal.querySelector('.modal-content');
        if (modalContent) {
            modalContent.style.width = '';
            modalContent.style.maxWidth = '';
            modalContent.style.margin = '';
        }
    }

    modal.style.display = 'flex';
    setupTabSwitching();
    loadMore(activeTab);

    // Scroll listener inside modal
    const modalBody = document.querySelector('.modal-body');
    if (modalScrollHandler) modalBody?.removeEventListener('scroll', modalScrollHandler);
    modalScrollHandler = throttle(() => {
        if (!modalBody) return;
        const { scrollTop, scrollHeight, clientHeight } = modalBody;
        if (scrollTop + clientHeight >= scrollHeight - 200) loadMore(activeTab);
    }, 200);
    modalBody?.addEventListener('scroll', modalScrollHandler);

    const closeModal = () => {
        modal.style.display = 'none';
        if (modalScrollHandler) modalBody?.removeEventListener('scroll', modalScrollHandler);
    };
    modal.querySelector('.modal-close').onclick = closeModal;
    modal.onclick = (e) => { if (e.target === modal) closeModal(); };
}

function setupTabSwitching() {
    const tabs = document.querySelectorAll('.modal-tabs .tab-btn');
    tabs.forEach(tab => {
        tab.removeEventListener('click', tab._listener);
        const handler = () => {
            const newTab = tab.dataset.tab;
            if (newTab === activeTab) return;
            activeTab = newTab;
            document.querySelectorAll('.modal-tabs .tab-btn').forEach(btn => btn.classList.remove('active'));
            tab.classList.add('active');
            const modalTitle = document.getElementById('modalTitle');
            modalTitle.textContent = newTab === 'followers' ? 'Followers' : newTab === 'following' ? 'Following' : 'Mutual Followers';
            document.getElementById('followersList').style.display = newTab === 'followers' ? 'flex' : 'none';
            document.getElementById('followingList').style.display = newTab === 'following' ? 'flex' : 'none';
            document.getElementById('mutualList').style.display = newTab === 'mutuals' ? 'flex' : 'none';
            if (pagination[newTab].page === 0 && !pagination[newTab].loading) loadMore(newTab);
        };
        tab.addEventListener('click', handler);
        tab._listener = handler;
    });
}

async function loadMore(tab) {
    const p = pagination[tab];
    if (p.loading || p.finished) return;
    p.loading = true;
    const container = document.getElementById(`${tab}List`);
    const loader = document.querySelector('.modal-loader');
    if (loader) loader.style.display = 'block';

    let endpoint = '';
    if (tab === 'followers') endpoint = `/api/user/${currentUserId}/followers/${p.page}`;
    else if (tab === 'following') endpoint = `/api/user/${currentUserId}/following/${p.page}`;
    else if (tab === 'mutuals') endpoint = `/api/user/${currentUserId}/mutuals/${p.page}`;

    try {
        const users = await fetchSimple(endpoint);
        if (!users.length) {
            p.finished = true;
            if (p.page === 0) container.innerHTML = '<div class="text-center" style="padding: 20px;">No users found.</div>';
        } else {
            const fragment = document.createDocumentFragment();
            users.forEach(user => fragment.appendChild(createUserItem(user)));
            container.appendChild(fragment);
            p.page++;
        }
    } catch (err) {
        console.error(err);
        if (p.page === 0) container.innerHTML = '<div class="text-center" style="padding: 20px;">Failed to load.</div>';
    } finally {
        p.loading = false;
        if (loader) loader.style.display = 'none';
    }
}

function createUserItem(user) {
    const div = document.createElement('div');
    div.className = 'user-item';
    div.dataset.userId = user.id;
    div.innerHTML = `
        <img src="${getAvatarUrl(user.profilePictureUrl)}" class="user-avatar" alt="${escapeHtml(user.name)}">
        <div class="user-info">
            <div class="user-name">${escapeHtml(user.name)}</div>
            ${user.bio ? `<div class="user-bio">${escapeHtml(user.bio.substring(0, 60))}${user.bio.length > 60 ? '...' : ''}</div>` : ''}
        </div>
    `;
    div.addEventListener('click', () => window.location.href = `/profile/${user.id}`);
    return div;
}

function throttle(func, limit) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}