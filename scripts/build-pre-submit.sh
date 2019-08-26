#!/bin/bash -l

echo "--- Running build for core publisher-sdk ---"
./scripts/build.sh

echo "--- Done build for core publisher-sdk ---"

echo "Update this file to run a clean + build on android-google-mediation for pre-submit"
./gradlew clean build --info --stacktrace
