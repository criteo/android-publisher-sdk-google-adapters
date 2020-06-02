# Depending on a local version of the SDK

The adapter has a dependency on the SDK. In order to debug, you may have the need to test the
adapter against a local SDK. You can:

## Deploy your own debug SDK in your local maven repository

To use a local SDK as a dependency in this adapter project, you need to publish your local SDK in
the maven local repository. You can then refresh your dependencies, the last version should be
selected, and you will end up with your local version.

```shell script
# Given the mochi folder is next to this adapter folder (and you are at the root of this adapter
# folder)

# You need to append the timestamp to differentiate your version against prod/preprod versions.
# Moreover, this allows you to differentiate your own local versions.
pushd ../mochi && ./gradlew clean publishToMavenLocal -PappendTimestamp=true && popd

# It is published, you need to refresh the dependencies
./gradlew clean build --refresh-dependencies
```

If you want to go back on a preprod dependency, you need to clean your maven local repository
(generally located at `~.m2/repository`), and refresh the dependencies.

# Publishing the adapter

## Bumping the version number

The `adapter_base_version` should be bumped in the [project level build.gradle file](build.gradle).

## Building and publishing the adapter to the production repository

```shell script
./gradlew clean :mediation:publishReleasePublicationToAzureRepository
```

## Publishing the adapter on GitHub

EE-924 TODO
