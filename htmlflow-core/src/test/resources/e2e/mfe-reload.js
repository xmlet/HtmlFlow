// Wiring for MfeEventsIT reload test. Fragments are sanitized (no <script>), so the host page loads this module.

mfe('microfrontend_1', (ctx) => {
    ctx.root.querySelector('#reload').addEventListener('click', () => {
        ctx.triggerMfeEvent('reload requested', {});
    });
});

mfe('microfrontend_2', (ctx) => {
    ctx.listenMfeEvent(() => ctx.reloadMfe());
});
