# Publishing the adapter

## Bumping the version number

The `version` should be bumped in the `mediation/build.gradle` file.

## Building and publishing the adapter to the production repository

The adapter has a dependency on the SDK. That is what the `copyArtifacts` is doing.

```shell script
./gradlew clean copyArtifacts :mediation:publishReleasePublicationToAzureRepository
```

## Publishing the adapter on GitHub

EE-924 TODO
