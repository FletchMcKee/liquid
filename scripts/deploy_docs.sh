#!/bin/bash
set -e

mkdir -p docs/sample
# Build the Compose WASM and JS samples.
./gradlew samples:composeApp:wasmJsBrowserDistribution
./gradlew samples:composeApp:jsBrowserDistribution

# Copy outside files into the docs folder.
cp -R samples/composeApp/build/dist/wasmJs/productionExecutable docs/sample/wasm
cp -R samples/composeApp/build/dist/js/productionExecutable docs/sample/js

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
