(function () {
    const ACTION = 'redirect';
    const REDIRECT_TO = '/controllthing/dunglai';
    const OPEN_URL = '/controllthing/dunglai';

    let lastWidth = window.innerWidth;
    let lastHeight = window.innerHeight;

    function detectByKey(cb, e) {
        const kc = e.keyCode || e.which;
        if (kc === 123) cb('F12');
        if (e.ctrlKey && e.shiftKey && (kc === 73 || kc === 74)) cb('CtrlShiftI/J');
        if (e.ctrlKey && kc === 85) cb('Ctrl+U');
    }

    function detectByResize(cb) {
        const diffW = Math.abs(window.innerWidth - lastWidth);
        const diffH = Math.abs(window.innerHeight - lastHeight);
        if (diffH > 200 || diffW > 200) cb('resize-detect');
    }

    function takeAction(reason) {
        console.warn('DevTools detected:', reason);
        try {
            if (ACTION === 'redirect') {
                window.location.href = REDIRECT_TO;
            } else if (ACTION === 'open') {
                const w = window.open(OPEN_URL, '_blank');
                if (w) {
                    try { w.focus(); } catch (e) {}
                }
            }
        } catch (e) {
            console.error(e);
        }
    }

    let fired = false;
    function cb(reason) {
        if (fired) return;
        fired = true;
        setTimeout(() => takeAction(reason), 50);
    }


    window.addEventListener('keydown', (e) => detectByKey(cb, e), true);


    window.addEventListener('resize', () => detectByResize(cb));


    document.addEventListener('contextmenu', (e) => e.preventDefault());
})();
