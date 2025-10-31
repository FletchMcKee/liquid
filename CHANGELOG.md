# Change Log

## [Unreleased]

## [1.0.0] - 2025-10-31

New:
- Migrated to Compose Multiplatform ([#37](https://github.com/FletchMcKee/liquid/pull/37)).

Changed:
- Using Android and Skia's blur instead of the custom frost shader ([#51](https://github.com/FletchMcKee/liquid/pull/51)).
  - Keeps parity between all platforms and also has significant performance improvements.

Fixed:
- Tint is applied before edge lighting and saturation ([#54](https://github.com/FletchMcKee/liquid/pull/54)).

## [1.0.0-rc2] - 2025-10-30

New:
- Support for Compose Multiplatform 1.9.2 ([#46](https://github.com/FletchMcKee/liquid/pull/46)).
- Screenshot testing for iOS and jvm ([#56](https://github.com/FletchMcKee/liquid/pull/56)).

Fixed:
- Tint is applied before edge lighting and saturation ([#54](https://github.com/FletchMcKee/liquid/pull/54)).

## [1.0.0-rc1] - 2025-10-27

New:
- Migrated to Compose Multiplatform ([#37](https://github.com/FletchMcKee/liquid/pull/37)).

Changed:
- Using Android and Skia's blur instead of the custom frost shader ([#51](https://github.com/FletchMcKee/liquid/pull/51)).
  - Keeps parity between all platforms and also has significant performance improvements.

## [0.3.1] - 2025-10-13

Fixed:
- Resetting frost value bug fix for API 31 and 32.

## [0.3.0] - 2025-10-09

New:
- Liquid node rotationZ and scale support ([#29](https://github.com/FletchMcKee/liquid/pull/29))
- Added `saturation` as a new LiquidScope property ([#29](https://github.com/FletchMcKee/liquid/pull/29)).
- Added `dispersion` as a new LiquidScope property ([#32](https://github.com/FletchMcKee/liquid/pull/32)).

Fixed:
- Fixed a normalize/divide by zero bug that was causing lines to appear in some shapes ([#29](https://github.com/FletchMcKee/liquid/pull/29)).

## [0.2.0] - 2025-09-26

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

[Unreleased]: https://github.com/fletchmckee/liquid/compare/1.0.0...HEAD
[1.0.0]: https://github.com/fletchmckee/liquid/releases/tag/1.0.0
[1.0.0-rc2]: https://github.com/fletchmckee/liquid/releases/tag/1.0.0-rc2
[1.0.0-rc1]: https://github.com/fletchmckee/liquid/releases/tag/1.0.0-rc1
[0.3.1]: https://github.com/fletchmckee/liquid/releases/tag/0.3.1
[0.3.0]: https://github.com/fletchmckee/liquid/releases/tag/0.3.0
[0.2.0]: https://github.com/fletchmckee/liquid/releases/tag/0.2.0
[0.1.0]: https://github.com/fletchmckee/liquid/releases/tag/0.1.0
[0.1.0-rc1]: https://github.com/fletchmckee/liquid/releases/tag/0.1.0-rc1
[0.1.0-alpha2]: https://github.com/fletchmckee/liquid/releases/tag/0.1.0-alpha2
[0.1.0-alpha]: https://github.com/fletchmckee/liquid/releases/tag/0.1.0-alpha
