#!/bin/bash -l

# Run this script to assemble and publish artifacts to Criteo nexus.

set -Eeuo pipefail

echo "Building and publishing artifacts to nexus"

./scripts/build-pre-submit.sh

# The Nexus prod is acting as a preprod
./gradlew publishReleasePublicationToNexusProdRepository -PappendTimestamp=true
