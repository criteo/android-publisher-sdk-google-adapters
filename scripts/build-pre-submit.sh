#!/bin/bash -l

echo "Update this file to run a clean + build on android-google-mediation for pre-submit"
./gradlew clean build assembleAndroidTest --info --stacktrace

