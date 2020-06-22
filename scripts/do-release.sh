#!/bin/bash -l

set -xEeuo pipefail

# TODO EE-926 Get those credentials from a vault
if [ -f "./scripts/env.secret.sh" ]; then
  source "./scripts/env.secret.sh"
fi

# TODO: Add slack integration and a job for submitting the adapters (line in Mochi)
./gradlew clean :mediation:publishReleasePublicationToAzureRepository

