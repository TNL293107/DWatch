// DWatch — Main JavaScript

// #region agent log
fetch('http://127.0.0.1:7503/ingest/67d8cee0-149e-4d2f-a684-5c7c0c390e8a', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'X-Debug-Session-Id': 'b721a2'
    },
    body: JSON.stringify({
        sessionId: 'b721a2',
        runId: 'pre-fix',
        hypothesisId: 'H1',
        location: 'js/script.js:4',
        message: 'script loaded and DOM state',
        data: {
            href: window.location.href,
            pathname: window.location.pathname,
            imgCount: document.querySelectorAll('img').length
        },
        timestamp: Date.now()
    })
}).catch(() => {});
// #endregion

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
    // #region agent log
    fetch('http://127.0.0.1:7503/ingest/67d8cee0-149e-4d2f-a684-5c7c0c390e8a', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Debug-Session-Id': 'b721a2'
        },
        body: JSON.stringify({
            sessionId: 'b721a2',
            runId: 'pre-fix',
            hypothesisId: 'H3',
            location: 'js/script.js:40',
            message: 'showToast called',
            data: {
                msg,
                type
            },
            timestamp: Date.now()
        })
    }).catch(() => {});
    // #endregion

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
