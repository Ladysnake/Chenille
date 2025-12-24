------------------------------------------------------
Version 0.16.1
------------------------------------------------------
**Additions**
- Added quick configuration to publish to Up's maven repository (https://maven.uuid.gg)

**Changes**
- Quick maven repository configurations now leverage [exclusive content filters](https://docs.gradle.org/current/userguide/filtering_repository_content.html#sec:declaring-content-repositories)
  - This should further reduce unnecessary HTTP calls when fetching dependencies

------------------------------------------------------
Version 0.16.0
------------------------------------------------------
**Changes**
- Swap out `org.cadixdev.licenser` gradle licenser plugin for the [`dev.yumi.gradle.licenser`](https://plugins.gradle.org/plugin/dev.yumi.gradle.licenser) plugin
  - The task to update the license headers is now called `applyLicenses`, and the task for checking license headers is `checkLicenses`
  - Behaviour should be largely unchanged
  - Fixes an incompatibility with Gradle 9

------------------------------------------------------
Version 0.15.0
------------------------------------------------------
**Changes**
- The `checkGitStatus` task now allows trailing data branch names, like "1.15-quilt"

  **Fixes**
- The `checkGitStatus` task should now work with private repositories
- Updated content filter for the Ladysnake maven

------------------------------------------------------
Version 0.14.0
------------------------------------------------------
**Changes**
- Improved Modrinth support with new project properties:
  - Use `modrinth_versions` to set the semicolon-separated list of game versions on Modrinth
    - If unspecified, will fallback to `curseforge_versions`
  - Use `mr_requirements`, `mr_optionals`, `mr_embeddeds`, and `mr_incompatibles` to configure relations with other modrinth projects
  - More information [on the website](https://ladysnake.org/wiki/chenille)

------------------------------------------------------
Version 0.13.1
------------------------------------------------------
**Fixes**
- Updated content filter for the Ladysnake maven

------------------------------------------------------
Version 0.13.0
------------------------------------------------------
**Additions**
- Projects can now use the `customLicense` property to have quick configuration with a custom template

------------------------------------------------------
Version 0.12.2
------------------------------------------------------
**Fixes**
- Loom is now pinned to `1.6-SNAPSHOT` instead of latest

------------------------------------------------------
Version 0.12.1
------------------------------------------------------
**Changes**
- Publishing of versions ending in "-SNAPSHOT" to the Ladysnake maven now targets the snapshot repository by default

**Fixes**
- Test mod dependency configurations work once again

------------------------------------------------------
Version 0.12.0
------------------------------------------------------
**Changes**
- Chenille no longer applies Loom on its own

------------------------------------------------------
Version 0.11.3
------------------------------------------------------
**Fixes**
- Fixed error message for missing Ladysnake credentials

------------------------------------------------------
Version 0.11.2
------------------------------------------------------
**Fixes**
- Fixed ladysnake maven names containing illegal characters

------------------------------------------------------
Version 0.11.1
------------------------------------------------------
**Fixes**
- Fixed the `withLadysnakeMaven` default parameter on groovy buildscript 

------------------------------------------------------
Version 0.11.0
------------------------------------------------------
**Additions**
- Added quick configuration to publish to the new Ladysnake maven repository

**Changes**
- Switched the Ladysnake maven repository url from ladysnake.jfrog.io to maven.ladysnake.org (Jfrog bad)

------------------------------------------------------
Version 0.10.2
------------------------------------------------------
**Changes**
- Fixed dependency on the wrong java version

------------------------------------------------------
Version 0.10.1
------------------------------------------------------
**Changes**
- Subprojects are no longer automatically configured as additional files for Modrinth publication

------------------------------------------------------
Version 0.10.0
------------------------------------------------------
**Changes**
- Updated minotaur to 2.4.3
- Made test runs generation work with quilt-testing API
- Bumped loom dependency

------------------------------------------------------
Version 0.9.0
------------------------------------------------------
**Changes**
- Now applies quilt-loom to the project if fabric-loom was not already applied

------------------------------------------------------
Version 0.8.0
------------------------------------------------------
**Changes**
- Updated loom to 0.12

**Fixes**
- Actually fixed curse publishing with default artifact, no for real !

**Removals**
- The test mod dependency configuration has been removed, hopefully loom implements an equivalent feature properly soon

------------------------------------------------------
Version 0.7.2
------------------------------------------------------
**Fixes**
- Actually fixed curse publishing with default artifact, hopefully

------------------------------------------------------
Version 0.7.1
------------------------------------------------------
**Fixes**
- Fixed curse publishing with default artifact

------------------------------------------------------
Version 0.7.0
------------------------------------------------------
**Additions**
- The main artifact for non-maven publications can now be configured in `configurePublishing`

**Removals**
- Removed the parameterless overload for `configurePublishing`

------------------------------------------------------
Version 0.6.3
------------------------------------------------------
**Fixes**
- Fixed `allCommonRepositories()` causing stack overflow

------------------------------------------------------
Version 0.6.2
------------------------------------------------------
**Additions**
- Added the GPL_WITH_MC_EXCEPTION license header

**Changes**
- The default display name for license headers now capitalizes the project name while accounting for snake casing

**Fixes**
- Fixed license plugin not being automatically applied and configured

------------------------------------------------------
Version 0.6.1
------------------------------------------------------
**Fixes**
- Fixed publishing configuration failing when a mavenJava publication was already setup

------------------------------------------------------
Version 0.6.0
------------------------------------------------------
**Changes**
- Release default configuration must now be applied through the gradle extension

**Fixes**
- Actually fixed artifactory publishing

------------------------------------------------------
Version 0.5.1
------------------------------------------------------
**Fixes**
- Fixed artifactory configuration
------------------------------------------------------
Version 0.5.0
------------------------------------------------------
**Changes**
- Renamed `modOptionalImplementation` to `modLocalImplementation` for consistency with `modLocalRuntime`

------------------------------------------------------
Version 0.4.0
------------------------------------------------------
**Additions**
- Added default maven+artifactory configuration method

------------------------------------------------------
Version 0.3.0
------------------------------------------------------
**Changes**
- Made testmod configuration more modular

------------------------------------------------------
Version 0.2.0
------------------------------------------------------
**Additions**
- Added jamieswhiteshirt and shedaniel common repositories

**Changes**
- Repository extensions have been moved to the chenille extension
- Renamed `defaultRepositories()` to `allCommonRepositories()`

------------------------------------------------------
Version 0.1
------------------------------------------------------
Initial release

**Additions**
- Shorthand methods for common repositories
- Default configuration for release tasks
- Git state check task
- Test mod configuration
- Changelog utilities
