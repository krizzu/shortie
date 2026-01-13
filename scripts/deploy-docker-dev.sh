#!/bin/sh
set -eu

IMAGE="krizzu/shortie"
PLATFORMS="linux/amd64,linux/arm64"
VERSION_FILE="version.txt"

if [ ! -f "$VERSION_FILE" ]; then
  echo "$VERSION_FILE not found in root?"
  exit 1
fi

VERSION=$(cat $VERSION_FILE | tr -d '[:space:]')
VERSION="$VERSION-dev"

echo "building $IMAGE:$VERSION (also $IMAGE:dev)"
echo

docker buildx build \
  -f ./docker/Dockerfile \
  --platform "$PLATFORMS" \
  --build-arg APP_VERSION="$VERSION" \
  -t "$IMAGE:$VERSION" \
  -t "$IMAGE:dev" \
  --push .

echo "completed:"
echo "  - $IMAGE:$VERSION"
echo "  - $IMAGE:dev"