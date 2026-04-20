let isRefreshing = false;
let refreshSubscribers = [];

async function parseErrorResponse(response) {
    try {
        const clonedResponse = response.clone();
        const errorData = await clonedResponse.json();
        console.log('Error response parsed:', errorData);
        return errorData.message || errorData.error || errorData.msg || `HTTP ${response.status}`;
    } catch (e) {
        try {
            const text = await response.text();
            return text || `HTTP ${response.status}`;
        } catch {
            return `HTTP ${response.status} - ${response.statusText}`;
        }
    }
}

function onRefreshed() {
    refreshSubscribers.forEach(callback => callback());
    refreshSubscribers = [];
}

async function fetchWithRetry(url, options, retried = false) {
    const response = await fetch(url, {
        ...options,
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        }
    });

    if (response.status === 401 && !retried && !url.includes('/auth/refresh')) {
        if (isRefreshing) {
            return new Promise((resolve) => {
                refreshSubscribers.push(() => resolve(fetchWithRetry(url, options, true)));
            });
        }

        isRefreshing = true;

        try {
            const refreshResponse = await fetch('/api/auth/refresh', {
                method: 'POST',
                credentials: 'include',
                headers: { 'Content-Type': 'application/json' }
            });

            if (refreshResponse.ok) {
                isRefreshing = false;
                onRefreshed();
                return fetchWithRetry(url, options, true);
            } else {
                isRefreshing = false;
                await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
                if (!window.location.pathname.includes('/login')) {
                    window.location.href = '/login';
                }
                throw new Error('Session expired. Please log in again.');
            }
        } catch (err) {
            isRefreshing = false;
            console.error('Refresh failed:', err);
            await fetch('/api/auth/logout', { method: 'POST', credentials: 'include' });
            if (!window.location.pathname.includes('/login')) {
                window.location.href = '/login';
            }
            throw new Error('Session expired. Please log in again.');
        }
    }

    return response;
}

/**
 * Use for most api calls, mainly post/put
 */
export async function fetchWithError(url, options = {}) {
    const response = await fetchWithRetry(url, options);
    if (!response.ok) {
        const errorMsg = await parseErrorResponse(response);
        throw new Error(errorMsg);
    }
    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

/**
 * mostly for deletes; api calls that can return either raw responses or empty body
 */
export async function fetchRawWithError(url, options = {}) {
    const response = await fetchWithRetry(url, options);
    if (!response.ok) {
        const errorMsg = await parseErrorResponse(response);
        throw new Error(errorMsg);
    }
    return response;
}

/**
 * public endpoints (no auth required)
 */
export async function fetchSimple(url, options = {}) {
    const response = await fetch(url, {
        ...options,
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        }
    });
    if (!response.ok) {
        const errorMsg = await parseErrorResponse(response);
        throw new Error(errorMsg);
    }
    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

export async function toggleLike(endpoint, btn, showToastFn) {
    const wasLiked = btn.classList.contains('liked');
    const span = btn.querySelector('span');
    const currentCount = parseInt(span.textContent, 10) || 0;
    const icon = btn.querySelector('i');

    span.textContent = wasLiked ? currentCount - 1 : currentCount + 1;
    btn.classList.toggle('liked');
    icon.classList.toggle('fas');
    icon.classList.toggle('far');
    btn.style.pointerEvents = 'none';

    try {
        await fetchWithError(endpoint, { method: 'POST' });
    } catch (err) {
        span.textContent = currentCount;
        btn.classList.toggle('liked');
        icon.classList.toggle('fas');
        icon.classList.toggle('far');
        if (showToastFn) showToastFn('Like failed', 'error');
    } finally {
        btn.style.pointerEvents = 'auto';
    }
}

export async function refreshTokenSilently() {
    try {
        const response = await fetch('/api/auth/refresh', {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' }
        });
        return response.ok;
    } catch {
        return false;
    }
}