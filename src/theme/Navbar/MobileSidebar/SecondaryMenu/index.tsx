import React, { type ReactNode } from 'react';
import { useThemeConfig } from '@docusaurus/theme-common';
import { useNavbarSecondaryMenu } from '@docusaurus/theme-common/internal';
import { LuChevronLeft, LuBookOpen } from 'react-icons/lu';
import SidebarSearchField from '@site/src/components/Search/SidebarSearchField';

function BackButton({ onClick }: { onClick: () => void }): ReactNode {
  return (
    <button type="button" className="hf-back" onClick={onClick}>
      <LuChevronLeft aria-hidden />
      <span>All sections</span>
    </button>
  );
}

export default function NavbarMobileSidebarSecondaryMenu(): ReactNode {
  const isPrimaryMenuEmpty = useThemeConfig().navbar.items.length === 0;
  const secondaryMenu = useNavbarSecondaryMenu();
  return (
    <>
      {!isPrimaryMenuEmpty && <BackButton onClick={() => secondaryMenu.hide()} />}
      <SidebarSearchField />
      <div className="hf-guide-header">
        <LuBookOpen aria-hidden />
        <span>HtmlFlow Guide</span>
      </div>
      {secondaryMenu.content}
    </>
  );
}
