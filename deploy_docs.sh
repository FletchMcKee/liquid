#!/bin/bash
set -e

mkdir -p docs/sample
# Build the Compose WASM and JS samples.
./gradlew samples:composeApp:wasmJsBrowserDistribution
./gradlew samples:composeApp:jsBrowserDistribution

# Copy outside files into the docs folder.
cp -R samples/composeApp/build/dist/wasmJs/productionExecutable docs/sample/wasm
cp -R samples/composeApp/build/dist/js/productionExecutable docs/sample/js

# Copy outside files into the docs folder.
cp README.md docs/index.md

# Deploy to Github pages.
python3 -m mkdocs gh-deploy --force

# Clean up.
rm -r docs/index.md \
   docs/sample \
   site
