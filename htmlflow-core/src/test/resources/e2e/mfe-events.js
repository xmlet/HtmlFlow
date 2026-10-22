// Wiring for MfeEventsIT. Fragments are sanitized (no <script>), so the host page loads this module.

window.mfe('microfrontend_1', (ctx) => {
    ctx.root.querySelector('#send').addEventListener('click', () => {
        ctx.triggerMfeEvent('item added', { id: 1, item: 'test 1' });
    });
});

window.mfe('microfrontend_2', (ctx) => {
    ctx.listenMfeEvent((event) => {
        const { id, item } = event.detail.payload;
        const li = document.createElement('li');
        li.dataset.id = String(id);
        li.textContent = item;
        ctx.root.querySelector('#items').append(li);
    });
});
