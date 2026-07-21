/**
 * Hides the stock toggle in favour of NavbarItem/ThemeToggle. Both the desktop
 * navbar and the mobile sidebar header render this component, so null covers
 * both. colorMode.disableSwitch would hide it too, but breaks persistence.
 */
export default function NavbarColorModeToggle(): null {
  return null;
}
