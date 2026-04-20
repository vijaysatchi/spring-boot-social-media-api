import { fetchWithError, fetchRawWithError } from '../utils/api.js';
import { showToast } from '../utils/toast.js';
import { escapeHtml } from '../utils/dom.js';
import { AVATAR_KEYS, getAvatarUrl, DEFAULT_AVATAR } from '../config/constants.js';
import { logout } from "../auth/state.js";

function disableBodyScroll() {
    document.body.style.overflow = 'hidden';
    document.body.style.paddingRight = '0px';
}

function enableBodyScroll() {
    document.body.style.overflow = '';
    document.body.style.paddingRight = '';
}

export function showEditProfileModal(user) {
    const existing = document.getElementById('editProfileModal');
    if (existing) existing.remove();

    let selectedAvatar = null;
    if (user.profilePictureUrl) {
        const matchedKey = AVATAR_KEYS.find(key =>
            user.profilePictureUrl === key ||
            user.profilePictureUrl === getAvatarUrl(key)
        );
        if (matchedKey) selectedAvatar = matchedKey;
    }

    const modal = document.createElement('div');
    modal.id = 'editProfileModal';
    modal.className = 'modal edit-profile-modal';
    modal.innerHTML = `
        <div class="modal-content">
            <div class="modal-header">
                <h3>Edit Profile</h3>
                <button class="modal-close">&times;</button>
            </div>
            <div class="modal-body">
                <form id="editProfileForm">
                    <div class="form-group">
                        <label>Name</label>
                        <input type="text" id="editName" value="${escapeHtml(user.name || '')}" maxlength="100">
                    </div>
                    <div class="form-group">
                        <label>Bio</label>
                        <textarea id="editBio" maxlength="255" rows="3">${escapeHtml(user.bio || '')}</textarea>
                    </div>
                    <div class="form-group">
                        <label>Banner Colour</label>
                        <div class="color-preview-wrapper">
                            <input type="color" id="editBannerColour" value="${user.bannerColour || '#e28b8b'}">
                            <div class="color-preview" style="background: ${user.bannerColour || '#e28b8b'};"></div>
                            <input type="text" id="editBannerColourHex" value="${user.bannerColour || '#e28b8b'}" maxlength="7" placeholder="#e28b8b">
                        </div>
                    </div>
                    <div class="form-group">
                        <label>Avatar</label>
                        <div class="avatar-picker">
                            <div class="avatar-option ${selectedAvatar === null ? 'selected' : ''}" data-avatar="null">
                                <img src="${DEFAULT_AVATAR}" alt="Default">
                                <span>None</span>
                            </div>
                            ${AVATAR_KEYS.map(key => `
                                <div class="avatar-option ${selectedAvatar === key ? 'selected' : ''}" data-avatar="${key}">
                                    <img src="${getAvatarUrl(key)}" alt="Avatar ${key}">
                                </div>
                            `).join('')}
                        </div>
                    </div>
                    <hr>
                    <h4>Change Password</h4>
                    <div class="form-group">
                        <label>Current Password</label>
                        <input type="password" id="currentPassword" autocomplete="off">
                    </div>
                    <div class="form-group">
                        <label>New Password</label>
                        <input type="password" id="newPassword" minlength="6">
                    </div>
                    <div class="form-group">
                        <label>Confirm Password</label>
                        <input type="password" id="confirmPassword">
                    </div>
                    <hr>
                    <div class="danger-zone">
                        <button type="button" id="deleteAccountBtn" class="delete-account-btn">Delete Account</button>
                    </div>
                    <div class="form-actions">
                        <button type="button" class="cancel-btn">Cancel</button>
                        <button type="submit" class="save-btn">Save Changes</button>
                    </div>
                </form>
            </div>
        </div>
    `;

    document.body.appendChild(modal);
    disableBodyScroll();
    modal.style.display = 'flex';

    // Color picker sync
    const colourPicker = document.getElementById('editBannerColour');
    const colourHex = document.getElementById('editBannerColourHex');
    const colourPreview = document.querySelector('.color-preview');
    if (colourPicker && colourHex && colourPreview) {
        colourPicker.addEventListener('input', () => {
            colourHex.value = colourPicker.value;
            colourPreview.style.background = colourPicker.value;
        });
        colourHex.addEventListener('input', () => {
            if (/^#[0-9A-Fa-f]{6}$/.test(colourHex.value)) {
                colourPicker.value = colourHex.value;
                colourPreview.style.background = colourHex.value;
            }
        });
    }

    // Avatar picker
    const avatarOptions = modal.querySelectorAll('.avatar-option');
    avatarOptions.forEach(opt => {
        opt.addEventListener('click', () => {
            avatarOptions.forEach(o => o.classList.remove('selected'));
            opt.classList.add('selected');
            const avatarValue = opt.dataset.avatar;
            selectedAvatar = avatarValue === 'null' ? null : avatarValue;
        });
    });

    const closeModal = () => {
        enableBodyScroll();
        modal.remove();
    };

    // Close buttons
    modal.querySelector('.modal-close').onclick = closeModal;
    modal.querySelector('.cancel-btn').onclick = closeModal;
    modal.onclick = (e) => { if (e.target === modal) closeModal(); };

    // Form submit
    const form = document.getElementById('editProfileForm');
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const saveBtn = form.querySelector('.save-btn');
        saveBtn.disabled = true;
        saveBtn.textContent = 'Saving...';

        let profileUpdateSuccess = false;
        let passwordUpdateSuccess = false;

        try {
            // Get current form values
            const name = document.getElementById('editName').value.trim();
            const bio = document.getElementById('editBio').value.trim();
            const bannerColour = document.getElementById('editBannerColourHex').value;
            const avatarValue = selectedAvatar === null ? null : selectedAvatar;

            const profileChanged =
                name !== (user.name || '') ||
                bio !== (user.bio || '') ||
                bannerColour !== (user.bannerColour || '#e28b8b') ||
                avatarValue !== user.profilePictureUrl;

            if (profileChanged) {
                const inputName = name === '' ? user.name : name;
                const profileData = {
                    name: inputName,
                    bio,
                    bannerColour,
                    profilePicture: avatarValue,
                    profilePictureUrl: avatarValue
                };

                await fetchWithError('/api/user/profile', {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(profileData)
                });
                profileUpdateSuccess = true;
            }

            // Update password
            const currentPassword = document.getElementById('currentPassword').value;
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            if (currentPassword || newPassword || confirmPassword) {
                if (!currentPassword || !newPassword || !confirmPassword) {
                    throw new Error('Please fill in all password fields to change password');
                }

                if (newPassword !== confirmPassword) {
                    throw new Error('New passwords do not match');
                }
                if (newPassword.length < 6) {
                    throw new Error('Password must be at least 6 characters');
                }

                await fetchRawWithError('/api/user/password', {
                    method: 'PATCH',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ currentPassword, newPassword, confirmPassword })
                });
                passwordUpdateSuccess = true;

                document.getElementById('currentPassword').value = '';
                document.getElementById('newPassword').value = '';
                document.getElementById('confirmPassword').value = '';
            }

            if (profileUpdateSuccess && passwordUpdateSuccess) showToast('Profile & Password updated successfully!', 'success');
            else if (profileUpdateSuccess) showToast('Profile updated successfully!', 'success');
            else if (passwordUpdateSuccess) showToast('Password changed successfully!', 'success');

            if (!profileUpdateSuccess && !passwordUpdateSuccess) {
                showToast('No changes were made', 'info');
                saveBtn.disabled = false;
                saveBtn.textContent = 'Save Changes';
                return;
            }

            closeModal();
            setTimeout(() => window.location.reload(), 1500);

        } catch (err) {
            console.error('Update error:', err);
            showToast(err.message || 'Update failed', 'error');
            saveBtn.disabled = false;
            saveBtn.textContent = 'Save Changes';
        }
    });

    // Delete account
    document.getElementById('deleteAccountBtn').addEventListener('click', async () => {
        const dialog = document.createElement('div');
        dialog.className = 'modal';
        dialog.style.display = 'flex';
        dialog.innerHTML = `
            <div class="modal-content" style="max-width: 400px;">
                <div class="modal-header">
                    <h3>Delete Account</h3>
                    <button class="modal-close">&times;</button>
                </div>
                <div class="modal-body">
                    <p style="color: #e28b8b; margin-bottom: 1rem;">
                        <strong>⚠️ WARNING:</strong> This action is irreversible.
                    </p>
                    <div class="form-group">
                        <label>Enter your password:</label>
                        <input type="password" id="deletePasswordInput" autocomplete="off">
                    </div>
                    <div class="form-actions" style="margin-top: 1rem;">
                        <button type="button" class="cancel-btn">Cancel</button>
                        <button type="button" id="confirmDeleteBtn" class="delete-account-confirm-btn">Delete Permanently</button>
                    </div>
                </div>
            </div>
        `;
        document.body.appendChild(dialog);
        disableBodyScroll();

        const closeDialog = () => {
            enableBodyScroll();
            dialog.remove();
        };
        dialog.querySelector('.modal-close').onclick = closeDialog;
        dialog.querySelector('.cancel-btn').onclick = closeDialog;
        dialog.onclick = (e) => { if (e.target === dialog) closeDialog(); };

        const confirmBtn = dialog.querySelector('#confirmDeleteBtn');
        const passwordInput = dialog.querySelector('#deletePasswordInput');

        confirmBtn.onclick = async () => {
            const password = passwordInput.value;
            if (!password) {
                showToast('Password required', 'error');
                return;
            }
            confirmBtn.disabled = true;
            confirmBtn.textContent = 'Deleting...';

            try {
                await fetchRawWithError('/api/user/delete', {
                    method: 'DELETE',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ password })
                });

                await logout();
                closeDialog();
                window.location.href = '/';
                showToast('Account deleted', 'success');
            } catch (err) {
                showToast('Delete failed: ' + err.message, 'error');
                confirmBtn.disabled = false;
                confirmBtn.textContent = 'Delete Permanently';
            }
        };

        // Enable button when password is entered
        passwordInput.addEventListener('input', () => {
            confirmBtn.disabled = !passwordInput.value.trim();
        });
        confirmBtn.disabled = true;
    });
}