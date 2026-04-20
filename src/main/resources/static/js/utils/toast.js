let toastTimeout = null;

export function showToast(message, type = 'success') {

    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    container.appendChild(toast);

    if (toastTimeout) clearTimeout(toastTimeout);

    toastTimeout = setTimeout(() => {
        if (toast && toast.remove) toast.remove();
    }, 3000);

    toast.addEventListener('click', () => toast.remove());
}