# Depending on a local version of the SDK

The adapter has a dependency on the SDK. In order to debug, you may have the need to test the
adapter against a local SDK. You can:

## Deploy your own debug SDK in your local maven repository

TODO

## Use a locally built version of the SDK.

The `copyArtifacts` gradle task fetch the SDK from the master on Gerrit and build it.
You just need to run:

```shell script
./gradlew copyArtifacts
```

Then you have to update the `useLocalPublisherSdk` variable in the `./mediation/build.gradle` file.

# Publishing the adapter

## Bumping the version number

The `version` should be bumped in the `mediation/build.gradle` file.

## Building and publishing the adapter to the production repository

```shell script
./gradlew clean :mediation:publishReleasePublicationToAzureRepository
```

## Publishing the adapter on GitHub

EE-924 TODO
