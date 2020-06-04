#!/bin/bash -l

set -xEeuo pipefail

# TODO: Add slack integration and a job for submitting the adapters (line in Mochi)
./gradlew clean :mediation:publishReleasePublicationToAzureRepository

