# Change Log

## [Unreleased]

## [0.2.0]

  - Improved lens effect using spherical lens refraction ([#26](https://github.com/FletchMcKee/liquid/pull/26))
  - Performance improvements ([#22](https://github.com/FletchMcKee/liquid/pull/22))
    - Also fixed bug for RectangleShape liquid nodes for API 34 and lower.
    - All API levels now record liquefiable layers. While API 30 and lower cannot use RuntimeShaders or RenderEffects, this can still be
    useful (ex. semi-transparent nodes with a shadow).

## [0.1.0] - 2025-09-12

  - Initial release.

## [0.1.0-rc1] - 2025-09-11

  Changed:
  - Improved lens effect by removing parallel distortion along spine extents.

## [0.1.0-alpha2] - 2025-09-06

  New:
  - Added `tint` as a LiquidScope property, allowing users to avoid the need for chaining a color background modifier to a liquid modifier.
  - Added improved support for Android 12 and lower.

  Changed:
  - Lowered minSdk to 23 instead of 26.
  - Altered the lens effect to be based on max dimension instead of minimum dimension (subject to change).

## [0.1.0-alpha] - 2025-09-03

Initial (alpha) Release

[Unreleased]: https://github.com/fletchmckee/liquid/compare/0.2.0...HEAD
[0.2.0]: https://github.com/fletchmckee/liquid/releases/tag/0.2.0
[0.1.0]: https://github.com/fletchmckee/liquid/releases/tag/0.1.0
[0.1.0-rc1]: https://github.com/fletchmckee/liquid/releases/tag/0.1.0-rc1
[0.1.0-alpha2]: https://github.com/fletchmckee/liquid/releases/tag/0.1.0-alpha2
[0.1.0-alpha]: https://github.com/fletchmckee/liquid/releases/tag/0.1.0-alpha
