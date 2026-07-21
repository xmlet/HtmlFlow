/**
 * Suppresses the stock navbar colour-mode toggle in favour of the custom one in
 * src/theme/NavbarItem/ThemeToggle.tsx, which matches the other navbar icons.
 *
 * Hiding it via colorMode.disableSwitch is not an option: that flag also strips
 * the localStorage read out of the pre-paint inline script and makes
 * ColorModeProvider delete the stored choice on mount, so the preference never
 * survives a reload. Both desktop Navbar/Content and MobileSidebar/Header
 * render this component, so returning null here covers both.
 */
export default function NavbarColorModeToggle(): null {
  return null;
}
