#!/bin/sh
set -euo pipefail


FRONTEND_PROJECT="frontend"
WEBSITE_BUILD_COMMAND="yarn build:pages"
TEMPLATE_DIR="api/src/main/resources/templates"
ASSETS_DIR="api/src/main/resources/assets"


assertRoot() {
  if [ ! -f "settings.gradle.kts" ]; then
    echo "Error: Must run this script from the root of the project (settings.gradle.kts not found)."
    exit 1
  fi
}

assertNodeModules() {
  if [ ! -d "$FRONTEND_PROJECT/node_modules" ]; then
    echo "Cannot find node_modules in $FRONTEND_PROJECT/node_modules - did you run 'yarn install'?"
    exit 1
  fi
}


buildPages() {
  echo "Building pages..."
  (
    cd "$FRONTEND_PROJECT"
    $WEBSITE_BUILD_COMMAND
  )
}

copyPagesToTemplates() {
  echo "Copying pages to $TEMPLATE_DIR"
  rm -rf "$TEMPLATE_DIR"
  mkdir -p "$TEMPLATE_DIR"
  cp $FRONTEND_PROJECT/dist-pages/pages/*.html "$TEMPLATE_DIR"
}

copyAssets() {
  echo "Copying assets to $ASSETS_DIR"
  rm -rf "$ASSETS_DIR"
  mkdir -p "$ASSETS_DIR"
  cp -r "$FRONTEND_PROJECT/dist-pages/assets/." "$ASSETS_DIR"
}


assertRoot
assertNodeModules
buildPages
copyPagesToTemplates
copyAssets