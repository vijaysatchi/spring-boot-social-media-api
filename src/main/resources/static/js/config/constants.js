export const DEFAULT_AVATAR = '/images/default-avatar.png';
export const AVATAR_BASE_PATH = '/images/avatars/';
export const AVATAR_EXT = '.jpg';
export const AVATAR_KEYS = ['avatar1', 'avatar2', 'avatar3', 'avatar4', 'avatar5'];

// Create link from key or return default
export function getAvatarUrl(key) {
    if (!key) return DEFAULT_AVATAR;
    return `${AVATAR_BASE_PATH}${key}${AVATAR_EXT}`;
}