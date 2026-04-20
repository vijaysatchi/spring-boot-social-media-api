import {fetchRawWithError, fetchSimple, fetchWithError} from "../utils/api.js";
import { showToast } from "../utils/toast.js";

let currentUser = window.__INITIAL_STATE__?.currentUser || null;
let authListeners = [];

export function getCurrentUser() { return currentUser; }
export function isLoggedIn() { return currentUser !== null; }

export function addAuthListener(callback) {
    authListeners.push(callback);
    callback(currentUser);
    return () => { authListeners = authListeners.filter(cb => cb !== callback); };
}

function notifyListeners() {
    authListeners.forEach(cb => cb(currentUser));
}

// Validation helpers
function validatePassword(password) {
    if (!password || password.length < 6) {
        throw new Error('Password must be at least 6 characters');
    }
    if (password.length > 50) {
        throw new Error('Password must be less than 50 characters');
    }
    return true;
}

function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email || !emailRegex.test(email)) {
        throw new Error('Please enter a valid email address');
    }
    return true;
}

function validateUsername(username) {
    if (!username || username.trim().length < 2) {
        throw new Error('Username must be at least 2 characters');
    }
    if (username.length > 50) {
        throw new Error('Username must be less than 50 characters');
    }
    return true;
}

export async function login(email, password) {
    try {
        validateEmail(email);
        validatePassword(password);

        const user = await fetchSimple('/api/auth/login', {
        // const user = await fetchWithError('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
        currentUser = user;
        notifyListeners();
        showToast('Logged in successfully!', 'success');
        return user;
    } catch (err) {
        showToast(err.message, 'error');
        throw err;
    }
}

export async function register(name, email, password, confirmPassword) {
    try {
        validateUsername(name);
        validateEmail(email);
        validatePassword(password);

        if (password !== confirmPassword) {
            throw new Error('Passwords do not match');
        }

        // const user = await fetchWithError('/api/auth/register', {
        const user = await fetchSimple('/api/auth/register', {
            method: 'POST',
            body: JSON.stringify({ name, email, password })
        });
        currentUser = user;
        notifyListeners();
        showToast('Registration successful! Welcome!', 'success');
        return user;
    } catch (err) {
        showToast(err.message, 'error');
        throw err;
    }
}

export async function logout() {
    try { await fetchRawWithError('/api/auth/logout', { method: 'POST', credentials: 'include' }); } catch {}
    currentUser = null;
    notifyListeners();
    window.location.href = '/';
}