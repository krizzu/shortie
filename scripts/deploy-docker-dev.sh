#!/bin/sh
set -eu


IMAGE="krizzu/shortie"
PLATFORMS="linux/amd64,linux/arm64"

# validate input exists
if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <semantic-version>"
  echo "Example: $0 1.2.3"
  exit 1
fi

VERSION="$1"
echo "$VERSION" | grep -Eq '^[0-9]+\.[0-9]+\.[0-9]+$' || {
  echo "Error: version must be semantic (x.y.z)"
  exit 1
}

VERSION="$VERSION-dev"

echo "building $IMAGE:$VERSION (also $IMAGE:dev)"
echo

docker buildx build \
  -f ./docker/Dockerfile \
  --platform "$PLATFORMS" \
  -t "$IMAGE:$VERSION" \
  -t "$IMAGE:dev" \
  --push .

echo "completed:"
echo "  - $IMAGE:$VERSION"
echo "  - $IMAGE:dev"