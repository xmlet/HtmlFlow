# ts-engine Commands

All commands should be run from the `ts-engine/` directory.

---

## Compile TypeScript → JavaScript (no minification)

Outputs to `../mfe-shell/src/main/resources/META-INF/resources/`

```bash
npm run build:tsc
```

Or directly with tsc pointing to a specific file:

```bash
npx tsc src/base.ts --outDir ../mfe-shell/src/main/resources/META-INF/resources --target es2020 --lib dom,dom.iterable,es2020 --skipLibCheck
```

> Output file: `base.js` — readable, unminified JavaScript.

---

## Compile TypeScript → JavaScript (minified)

Uses esbuild for fast bundling and minification:

```bash
npm run build:minified
```

Or directly:

```bash
npx esbuild src/base.ts --bundle --minify --outfile=../mfe-shell/src/main/resources/META-INF/resources/base.js
```

> Output file: `base.js` — single line, minified.

---

## Compile without emitting files (type-check only)

Useful to check for errors without producing output:

```bash
npx tsc --noEmit
```

---

## Watch mode — recompile on every save

```bash
npx tsc --watch --outDir ../mfe-shell/src/main/resources/META-INF/resources
```

> Stays running and recompiles automatically whenever `base.ts` changes.

---

## Watch mode — minified (esbuild)

```bash
npx esbuild src/base.ts --bundle --minify --outfile=../mfe-shell/src/main/resources/META-INF/resources/base.js --watch
```

---

## Install dependencies

```bash
npm install
```
