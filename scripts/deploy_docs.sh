#!/bin/bash
set -e

# Make sure package-lock.json is up to date
./gradlew kotlinWasmUpgradePackageLock kotlinUpgradePackageLock

mkdir -p docs/sample
# Build the Compose WASM and JS samples.
./gradlew samples:web:wasmJsBrowserDistribution
./gradlew samples:web:jsBrowserDistribution

# Copy outside files into the docs folder.
cp -R samples/web/build/dist/wasmJs/productionExecutable docs/sample/wasm
cp -R samples/web/build/dist/js/productionExecutable docs/sample/js

# Clean and generate new Dokka docs.
rm -rf docs/api
./gradlew clean dokkaGenerate
cp -R liquid/build/dokka/html docs/api

# Copy outside files into the docs folder.
sed -e '/full documentation here/ { N; d; }' \
    -e 's|docs/gifs/|gifs/|g' \
    < README.md > docs/index.md

# Deploy to Github pages.
python3 -m mkdocs gh-deploy --force

# Clean up.
rm -r docs/index.md \
   docs/sample \
   docs/api \
   site
