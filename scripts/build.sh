#!/bin/bash -l

rm -rf build/output
mkdir -p build/output/
cd build/output

PUBLISHER_SDK_CONFIGURATION="release"
USERNAME=$(whoami)

echo "Downloading $PUBLISHER_SDK_CONFIGURATION build for publisher-sdk as $USERNAME from mochi... \n"
rm -rf mochi

git clone ssh://qabot@review.criteois.lan:29418/pub-sdk/mochi

echo "Clone complete"

cd mochi
echo "Using $ANDROID_HOME as android sdk"
#echo "sdk.dir=/Users/qabot/Library/Android/sdk" >> local.properties

./gradlew clean build
cp -f publisher-sdk/build/outputs/aar/publisher-sdk-$PUBLISHER_SDK_CONFIGURATION.aar ../../../publisher-sdk-release/


