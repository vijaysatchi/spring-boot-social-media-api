export function formatRelativeTime(dateString) {
    if (!dateString) return '';
    const isDateOnly = /^\d{4}-\d{2}-\d{2}$/.test(dateString);
    let date;
    if (isDateOnly) {
        const [y, m, d] = dateString.split('-').map(Number);
        date = new Date(y, m - 1, d); // local midnight
    } else {
        date = new Date(dateString);
    }

    const now = new Date();
    const diffMs = now - date;

    const diffMins = Math.floor(diffMs / 60000);
    const diffHours = Math.floor(diffMins / 60);
    const diffDays = Math.floor(diffHours / 24);

    if (!isDateOnly) {
        if (diffMins < 1) return 'Just now';
        if (diffMins < 60) return `${diffMins}m`;
        if (diffHours < 24) return `${diffHours}h`;
        if (diffDays < 7) return `${diffDays}d`;

        const weeks = Math.floor(diffDays / 7);
        if (weeks < 5) return `${weeks}w`;

        const months = Math.floor(diffDays / 30);
        if (months < 12) return `${months}mo`;

        const years = Math.floor(diffDays / 365);
        return `${years}y`;
    }

    if (diffDays < 7) return `${diffDays}d`;

    const weeks = Math.floor(diffDays / 7);
    if (weeks < 5) return `${weeks}w`;

    const months = Math.floor(diffDays / 30);
    if (months < 12) return `${months}mo`;

    const years = Math.floor(diffDays / 365);
    return `${years}y`;
}