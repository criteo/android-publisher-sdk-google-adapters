#!/bin/bash -l

echo "Update this file to run a clean + build on android-google-mediation for buld+test+publish to nexus"
#ORIGINAL_VERSION=2.2.1
#VERSION=$ORIGINAL_VERSION-criteo-$(date -u +%Y%m%d%H%M%S)
sh scripts/build-pre-submit.sh
#sh gradlew publish -Pversion=$VERSION -PpublishUrl="http://nexus.criteo.prod/content/repositories/criteo.android.releases/"
