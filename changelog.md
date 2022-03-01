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
