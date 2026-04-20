import { getCurrentUser, logout, addAuthListener } from '../auth/state.js';

function renderSidebar() {
    const authContainer = document.getElementById('authButtons');
    const profileLink = document.getElementById('profileLink');
    const logoutContainer = document.getElementById('logoutContainer');
    const user = getCurrentUser();

    if (user) {
        authContainer.innerHTML = ''; // Remove login/register if signed in
        if (logoutContainer) {
            logoutContainer.innerHTML = `<button id="logoutBtn" class="nav-item"><i class="fas fa-sign-out-alt"></i> Logout</button>`;
            document.getElementById('logoutBtn')?.addEventListener('click', () => logout());
        }
        if (profileLink) {
            profileLink.href = `/profile/${user.id}`;
        }
    } else {
        authContainer.innerHTML = `<a href="/login" class="nav-item"><i class="fas fa-sign-in-alt"></i> Sign In</a>
                                    <a href="/register" class="nav-item"><i class="fas fa-user-plus"></i> Sign Up</a>`;
        if (logoutContainer) logoutContainer.innerHTML = '';
    }
}

export function initSidebar() {
    renderSidebar();
    addAuthListener(() => renderSidebar());
}