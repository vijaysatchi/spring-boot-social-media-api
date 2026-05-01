export function formatRelativeTime(dateString, options = {}) {
    if (!dateString) return '';

    const {
        maxUnit = 'minute',   // 'minute' | 'hour' | 'day' | 'week' | 'month' | 'year'
    } = options;

    const date = new Date(dateString);
    const now = new Date();

    let diffMs = now - date;

    if (diffMs < 0) diffMs = 0;

    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (maxUnit === 'minute') {
        if (diffMins < 1) return 'Just now';
        if (diffMins < 60) return `${diffMins}m`;
    }

    if (['minute', 'hour'].includes(maxUnit)) {
        if (diffHours < 24) return `${diffHours}h`;
    }

    if (['minute', 'hour', 'day'].includes(maxUnit)) {
        if (diffDays < 7) return `${diffDays}d`;
    }

    const weeks = Math.floor(diffDays / 7);
    if (['minute', 'hour', 'day', 'week'].includes(maxUnit)) {
        if (weeks < 5) return `${weeks}w`;
    }

    const months = Math.floor(diffDays / 30);
    if (['minute', 'hour', 'day', 'week', 'month'].includes(maxUnit)) {
        if (months < 12) return `${months}mo`;
    }

    const years = Math.floor(diffDays / 365);
    return `${years}y`;
}