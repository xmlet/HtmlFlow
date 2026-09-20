"use strict";
class Mfe extends HTMLElement {
    constructor() {
        super();
        this.mfeName = "";
        this.mfeUrlResource = "";
        this.mfeListeningEventName = "";
        this.mfeTriggerEventName = "";
        this.readyEventSuffix = "-fragment-ready";
        this.mfeStylingUrl = "";
        this.isMfeStreamingData = "";
        this.attachShadow({ mode: 'open' });
    }
    connectedCallback() {
        this.mfeUrlResource = this.getAttribute("mfe-url");
        this.mfeName = this.getAttribute("mfe-name");
        this.mfeStylingUrl = this.getAttribute("mfe-styling-url");
        this.mfeListeningEventName = this.getAttribute("mfe-listen-event");
        this.mfeTriggerEventName = this.getAttribute("mfe-trigger-event");
        this.isMfeStreamingData = this.getAttribute("mfe-stream-data");
        if (this.mfeListeningEventName) {
            window.addEventListener(this.mfeListeningEventName, this.reloadFragment.bind(this));
        }
        this.loadFragment();
    }
    async fetchData() {
        if (this.mfeUrlResource) {
            const response = await fetch(this.mfeUrlResource);
            if (!response.ok) {
                throw new Error(`Failed to fetch ${this.mfeUrlResource}: ${response.status}`);
            }
            return response.text();
        }
    }
    async fetchStreamData() {
        this.wrapper = document.createElement('div');
        if (this.mfeUrlResource) {
            const response = await fetch(this.mfeUrlResource);
            if (response.body) {
                for await (const value of response.body) { //ReadableStream<Uint8Array<ArrayBufferLike>> not yet supported by ts
                    const uint8Array = new Uint8Array(value);
                    const chunk = new TextDecoder().decode(uint8Array);
                    this.wrapper.insertAdjacentHTML('beforeend', chunk);
                    this.shadowRoot?.appendChild(this.wrapper);
                }
            }
        }
    }
    buildFragment(html) {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        if (this.shadowRoot) {
            this.shadowRoot.innerHTML = "";
        }
        const fragment = document.createDocumentFragment();
        const link = document.createElement('link');
        if (this.mfeStylingUrl) {
            link.setAttribute('rel', 'stylesheet');
            link.setAttribute('href', this.mfeStylingUrl);
            fragment.append(link);
        }
        doc.body.childNodes.forEach(child => {
            fragment.append(child);
        });
        return this.shadowRoot?.appendChild(fragment);
    }
    triggerEvent(eventName, message, payload, bubbles = true, composed = true) {
        const event = new CustomEvent(eventName, {
            detail: { message, payload },
            bubbles,
            composed
        });
        dispatchEvent(event);
    }
    reloadFragment() {
        this.loadFragment();
    }
    loadFragment() {
        if (this.isMfeStreamingData === "true") {
            this.fetchStreamData().then(r => console.info("Finished fetching stream!")).catch(err => console.error(err));
        }
        else {
            this.fetchData().then(r => {
                if (r) {
                    this.buildFragment(r);
                    console.log("eventname: ", this.mfeName + this.readyEventSuffix);
                    this.dispatchEvent(new Event(this.mfeName + this.readyEventSuffix, { bubbles: true, composed: true }));
                }
            });
        }
    }
    disconnectedCallback() {
        console.log("Custom element removed from page.");
    }
    adoptedCallback() {
        console.log("Custom element moved to new page.");
    }
    attributeChangedCallback(name, oldValue, newValue) {
        console.log(`Attribute ${name} has changed from ${oldValue} to ${newValue}.`);
    }
}
window.customElements.define('micro-frontend', Mfe);
