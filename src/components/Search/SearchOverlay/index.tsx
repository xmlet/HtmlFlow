import React, { useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import { createPortal } from 'react-dom';
import { useHistory } from '@docusaurus/router';
import {
  LuSearch,
  LuCornerDownLeft,
  LuArrowUp,
  LuArrowDown,
  LuFileText,
  LuNewspaper,
  LuBookOpen,
  LuLibrary,
} from 'react-icons/lu';
import { JUMP_TO, type JumpIcon } from '../searchData';
import { useDocSearch, type Area } from '../useDocSearch';

function escapeRegExp(s: string): string {
  return s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
}

function Highlight({ text, query }: { text: string; query: string }): ReactNode {
  const tokens = query.trim().split(/\s+/).filter(Boolean);
  if (!tokens.length) return <>{text}</>;
  const lower = new Set(tokens.map(t => t.toLowerCase()));
  const re = new RegExp(`(${tokens.map(escapeRegExp).join('|')})`, 'ig');
  const parts = text.split(re);
  return (
    <>
      {parts.map((part, i) =>
        lower.has(part.toLowerCase()) ? <mark key={i}>{part}</mark> : <React.Fragment key={i}>{part}</React.Fragment>
      )}
    </>
  );
}

const AREA_ICON: Record<Area, ReactNode> = {
  Guide: <LuFileText />,
  News: <LuNewspaper />,
  Other: <LuFileText />,
};

const JUMP_ICON: Record<JumpIcon, ReactNode> = {
  guide: <LuBookOpen />,
  api: <LuLibrary />,
  news: <LuNewspaper />,
};

export default function SearchOverlay({ onClose }: { onClose: () => void }): ReactNode {
  const history = useHistory();
  const inputRef = useRef<HTMLInputElement>(null);
  const rowRefs = useRef<(HTMLButtonElement | null)[]>([]);
  const [query, setQuery] = useState('');
  const [active, setActive] = useState(0);
  const { groups, total, loading, warmup } = useDocSearch(query);

  const hasQuery = query.trim().length > 0;

  // Flat, ordered list of every selectable row — the source of truth for
  // arrow-key navigation. Resting: jump-to entries. Querying: result rows.
  const flat = useMemo<string[]>(() => {
    if (hasQuery) return groups.flatMap(g => g.items.map(it => it.url));
    return JUMP_TO.map(j => j.href);
  }, [hasQuery, groups, query]);

  useEffect(() => {
    warmup();
    inputRef.current?.focus();
  }, [warmup]);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  useEffect(() => {
    setActive(a => (a >= flat.length ? 0 : a));
  }, [flat.length]);

  useEffect(() => {
    setActive(0);
  }, [query]);

  useEffect(() => {
    rowRefs.current[active]?.scrollIntoView({ block: 'nearest' });
  }, [active]);

  const go = (href: string) => {
    onClose();
    history.push(href);
  };

  const onInputKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'ArrowDown') {
      e.preventDefault();
      setActive(a => Math.min(a + 1, flat.length - 1));
    } else if (e.key === 'ArrowUp') {
      e.preventDefault();
      setActive(a => Math.max(a - 1, 0));
    } else if (e.key === 'Enter') {
      e.preventDefault();
      const sel = flat[active];
      if (sel) go(sel);
    }
  };

  const resting = (
    <div className="hf-ov-body">
      <section className="hf-ov-section">
        <span className="hf-ov-section-title">Jump To</span>
        <ul className="hf-ov-jumps">
          {JUMP_TO.map((entry, i) => (
            <li key={entry.label}>
              <button
                type="button"
                ref={el => {
                  rowRefs.current[i] = el;
                }}
                className={`hf-ov-jump${active === i ? ' hf-row--active' : ''}`}
                onMouseMove={() => setActive(i)}
                onClick={() => go(entry.href)}
              >
                <span className="hf-ov-jump__icon">{JUMP_ICON[entry.icon]}</span>
                <span className="hf-ov-jump__body">
                  <span className="hf-ov-jump__label">{entry.label}</span>
                  <span className="hf-ov-jump__area">{entry.area}</span>
                </span>
              </button>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );

  let base = 0;
  const results = (
    <div className="hf-ov-body">
      {groups.map(group => {
        const groupBase = base;
        base += group.items.length;
        return (
          <section className="hf-ov-section" key={group.area}>
            <span className="hf-ov-section-title">{group.area}</span>
            <ul className="hf-ov-results">
              {group.items.map((item, j) => {
                const idx = groupBase + j;
                return (
                  <li key={item.url}>
                    <button
                      type="button"
                      ref={el => {
                        rowRefs.current[idx] = el;
                      }}
                      className={`hf-result${active === idx ? ' hf-row--active' : ''}`}
                      onMouseMove={() => setActive(idx)}
                      onClick={() => go(item.url)}
                    >
                      <span className="hf-result__icon">{AREA_ICON[item.area]}</span>
                      <span className="hf-result__body">
                        <span className="hf-result__title">
                          <Highlight text={item.title} query={query} />
                        </span>
                        {item.breadcrumb.length > 0 && (
                          <span className="hf-result__crumb">{item.breadcrumb.join(' › ')}</span>
                        )}
                        {item.snippet && (
                          <span className="hf-result__snippet">
                            <Highlight text={item.snippet} query={query} />
                          </span>
                        )}
                      </span>
                    </button>
                  </li>
                );
              })}
            </ul>
          </section>
        );
      })}

      {total === 0 && !loading && (
        <p className="hf-ov-empty">
          No results for <strong>{query}</strong>.
        </p>
      )}
    </div>
  );

  return createPortal(
    <div
      className="hf-ov"
      role="dialog"
      aria-modal="true"
      aria-label="Search documentation"
      onMouseDown={e => {
        if (e.target === e.currentTarget) onClose();
      }}
    >
      <div className="hf-ov-panel">
        <div className="hf-ov-bar">
          <span className="hf-ov-bar__icon">
            <LuSearch aria-hidden />
          </span>
          <input
            ref={inputRef}
            className="hf-ov-input"
            type="search"
            placeholder="Search documentation, API, examples..."
            value={query}
            onChange={e => setQuery(e.target.value)}
            onKeyDown={onInputKeyDown}
            autoComplete="off"
            spellCheck={false}
          />
          <button type="button" className="hf-ov-esc" onClick={onClose}>
            Esc
          </button>
        </div>

        {hasQuery ? results : resting}

        <div className="hf-ov-foot">
          <div className="hf-ov-foot__keys">
            <span>
              <kbd>
                <LuArrowUp aria-hidden />
              </kbd>
              <kbd>
                <LuArrowDown aria-hidden />
              </kbd>
              Navigate
            </span>
            <span>
              <kbd>
                <LuCornerDownLeft aria-hidden />
              </kbd>
              Open
            </span>
            <span>
              <kbd>Esc</kbd>
              Close
            </span>
          </div>
        </div>
      </div>
    </div>,
    document.body
  );
}
