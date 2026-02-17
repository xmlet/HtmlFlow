// src/client-modules/aos.ts
import AOS from 'aos';
import 'aos/dist/aos.css'; // Import the CSS globally
import ExecutionEnvironment from '@docusaurus/ExecutionEnvironment';

if (ExecutionEnvironment.canUseDOM) {
  AOS.init({
    // Global settings:
    offset: 120, // offset (in px) from the original trigger point
    delay: 0, // values from 0 to 3000, with step 50ms
    duration: 1000, // values from 0 to 3000, with step 50ms
    easing: 'ease', // default easing for AOS animations
    once: false, // whether animation should happen only once - while scrolling down
    mirror: false, // whether elements should animate out while scrolling past them
  });
}

// Refresh AOS when the route changes (Docusaurus is a SPA)
export function onRouteDidUpdate() {
  if (ExecutionEnvironment.canUseDOM) {
    setTimeout(() => {
      AOS.refresh();
    }, 100);
  }
}
