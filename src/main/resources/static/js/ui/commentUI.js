import { isLoggedIn, addAuthListener } from '../auth/state.js';

export function autoResizeTextarea(textarea) {
    textarea.style.height = 'auto';
    textarea.style.height = Math.min(textarea.scrollHeight, 100) + 'px';
}

export function initCommentInput(textareaId, buttonId, onSubmit) {
    const textarea = document.getElementById(textareaId);
    const button = document.getElementById(buttonId);
    if (!textarea || !button) return;

    const updateUI = () => {
        const loggedIn = isLoggedIn();
        textarea.disabled = !loggedIn;
        textarea.placeholder = loggedIn ? 'Write a comment...' : 'Log in to comment';
        const text = textarea.value.trim();
        button.disabled = !loggedIn || text.length === 0;
    };

    const handleInput = () => {
        const text = textarea.value.trim();
        button.disabled = !isLoggedIn() || text.length === 0;
        autoResizeTextarea(textarea);
    };

    textarea.addEventListener('input', handleInput);
    textarea.addEventListener('keydown', (e) => {
        if (e.key === 'Enter' && !e.shiftKey && isLoggedIn() && !button.disabled) {
            e.preventDefault();
            onSubmit(textarea.value.trim()).then(() => {
                textarea.value = '';
                handleInput();
            }).catch(() => {});
        }
    });
    button.addEventListener('click', () => {
        if (!isLoggedIn()) return window.location.href = '/login';
        const text = textarea.value.trim();
        if (!text) return;
        onSubmit(text).then(() => {
            textarea.value = '';
            handleInput();
        }).catch(() => {});
    });
    updateUI();
    addAuthListener(() => updateUI());
}