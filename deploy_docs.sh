#!/bin/bash
set -e

# Build the Compose WASM sample.
./gradlew samples:composeApp:wasmJsBrowserDistribution

# Copy outside files into the docs folder.
cp -R samples/composeApp/build/dist/wasmJs/productionExecutable docs/sample

# Copy outside files into the docs folder.
cp README.md docs/index.md

# Deploy to Github pages.
python3 -m mkdocs gh-deploy --force

# Clean up.
rm -r docs/index.md \
   docs/sample \
   site
