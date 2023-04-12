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
