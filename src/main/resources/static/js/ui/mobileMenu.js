export function initMobileMenu() {
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', initMobileMenu);
        return;
    }

    // Create mobile menu btn
    let mobileBtn = document.getElementById('mobileMenuBtn');
    if (!mobileBtn) {
        mobileBtn = document.createElement('button');
        mobileBtn.id = 'mobileMenuBtn';
        mobileBtn.className = 'mobile-menu-btn';
        mobileBtn.innerHTML = '<i class="fas fa-bars"></i>';
        document.body.appendChild(mobileBtn);
    }

    // Create overlay
    let overlay = document.getElementById('sidebarOverlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.id = 'sidebarOverlay';
        overlay.className = 'sidebar-overlay';
        document.body.appendChild(overlay);
    }

    // Add sidebar
    const sidebar = document.querySelector('.sidebar');
    if (!sidebar) {
        console.warn('Sidebar not found');
        return;
    }

    // Add CSS
    if (!document.getElementById('mobileMenuStyles')) {
        const style = document.createElement('style');
        style.id = 'mobileMenuStyles';
        style.textContent = `
            .mobile-menu-btn {
                display: none;
                position: fixed;
                top: 1rem;
                left: 1rem;
                z-index: 1001;
                background: var(--primary, #e28b8b);
                border: none;
                color: white;
                width: 44px;
                height: 44px;
                border-radius: 50%;
                font-size: 1.2rem;
                cursor: pointer;
                box-shadow: 0 2px 8px rgba(0,0,0,0.15);
            }
            .sidebar-overlay {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: rgba(0,0,0,0.5);
                z-index: 999;
            }
            .sidebar-overlay.active {
                display: block;
            }
            @media (max-width: 768px) {
                .mobile-menu-btn {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                .sidebar {
                    position: fixed;
                    left: -280px;
                    top: 0;
                    bottom: 0;
                    width: 260px;
                    z-index: 1000;
                    transition: left 0.3s ease;
                    margin: 0;
                    border-radius: 0 !important;
                    height: 100vh;
                    overflow-y: auto;
                }
                .sidebar.open {
                    left: 0;
                }
                .container {
                    padding-top: 4rem;
                }
            }
        `;
        document.head.appendChild(style);
    }

    // Toggle function
    const openSidebar = () => {
        sidebar.classList.add('open');
        overlay.classList.add('active');
        document.body.style.overflow = 'hidden';
    };

    const closeSidebar = () => {
        sidebar.classList.remove('open');
        overlay.classList.remove('active');
        document.body.style.overflow = '';
    };

    // Event listeners
    mobileBtn.onclick = (e) => {
        e.stopPropagation();
        if (sidebar.classList.contains('open')) {
            closeSidebar();
        } else {
            openSidebar();
        }
    };

    overlay.onclick = closeSidebar;

    // Close on escape key
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && sidebar.classList.contains('open')) {
            closeSidebar();
        }
    });

    // Handle window resize
    window.addEventListener('resize', () => {
        if (window.innerWidth > 768 && sidebar.classList.contains('open')) {
            closeSidebar();
        }
    });
}