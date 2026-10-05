# ts-engine Commands

All commands should be run from the `ts-engine/` directory.

---

## Compile TypeScript → JavaScript (no minification)

Outputs to `./dist`

```bash
npm run build:tsc
```

Or directly with tsc pointing to a specific file:

```bash
npx tsc src/htmlflow-mfe-engine.ts --outDir ./path-to-folder --target es2020 --lib dom,dom.iterable,es2020 --skipLibCheck
```

> Output file: `htmlflow-mfe-engine` — readable, unminified JavaScript.

---

## Compile TypeScript → JavaScript (minified)

Uses esbuild for fast bundling and minification:

```bash
npm run build:minified
```

Or directly:

```bash
npx esbuild src/htmlflow-mfe-engine.ts --bundle --minify --outfile=./dist/htmlflow-mfe-engine
```

> Output file: `htmlflow-mfe-engine` — single line, minified.

---

## Compile without emitting files (type-check only)

Useful to check for errors without producing output:

```bash
npx tsc --noEmit
```

---

## Watch mode — recompile on every save

```bash
npx tsc --watch --outDir ./dist
```

> Stays running and recompiles automatically whenever `htmlflow-mfe-engine.ts` changes.

---

## Watch mode — minified (esbuild)

```bash
npx esbuild src/htmlflow-mfe-engine.ts --bundle --minify --outfile=.dist/htmlflow-mfe-engine --watch
```

---

## Install dependencies

```bash
npm install
```
