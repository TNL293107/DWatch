// DWatch — Main JavaScript

// Auto-submit cart quantity changes
document.querySelectorAll('.qty-box').forEach(input => {
    input.addEventListener('change', function () {
        const form = document.getElementById('cartForm');
        if (form) form.submit();
    });
});

// Navbar active link highlighting
(function () {
    const path = window.location.pathname;
    document.querySelectorAll('.nav-link').forEach(link => {
        if (link.getAttribute('href') && path.includes(link.getAttribute('href').split('?')[0])) {
            link.style.color = '#c8a96e';
        }
    });
})();

// Smooth scroll to products section from hero button
const heroBtn = document.querySelector('.hero-btn');
if (heroBtn) {
    heroBtn.addEventListener('click', function (e) {
        const target = document.getElementById('products');
        if (target) {
            e.preventDefault();
            target.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    });
}

// Toast notification (used after add to cart)
function showToast(msg, type) {
    const toast = document.createElement('div');
    toast.style.cssText = `
        position: fixed; bottom: 30px; right: 30px; z-index: 9999;
        background: ${type === 'error' ? '#e53935' : '#c8a96e'};
        color: ${type === 'error' ? '#fff' : '#000'};
        padding: 14px 24px; border-radius: 4px;
        font-size: 14px; font-weight: 600;
        box-shadow: 0 4px 20px rgba(0,0,0,.4);
        animation: slideIn .3s ease;
    `;
    toast.textContent = msg;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}
