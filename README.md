# Liquid

[![Maven Central](https://img.shields.io/maven-central/v/io.github.fletchmckee.liquid/liquid)](https://search.maven.org/search?q=g:io.github.fletchmckee.liquid)
![Build status](https://github.com/fletchmckee/liquid/actions/workflows/build.yml/badge.svg)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-4285F4?style=flat&logo=jetpackcompose&logoColor=white)

**Liquid** brings [Liquid Glass](https://developer.apple.com/documentation/SwiftUI/Applying-Liquid-Glass-to-custom-views)-style effects to Compose Multiplatform.
It lets you refract, frost, and curve the content behind your UI elements, creating dynamic frosted glass, lens, and liquid-like effects.

Powered by RuntimeShaders/RuntimeEffects and ModifierNodeElement APIs, it delivers GPU-accelerated visuals to your Compose UI.

<div align="center">
  <img src="https://github.com/user-attachments/assets/0a9fa7f9-d1ba-47fd-95d4-238d699025f4" width="400" />
</div>

## Acknowledgements

- The [Haze](https://github.com/chrisbanes/haze) library developed by [Chris Banes](https://github.com/chrisbanes) was a large source of
inspiration, particularly for the use of content and effect `Modifier` nodes and blur techniques. Specifically, the
`FrostShaders` available for the liquid nodes are
derived from the [HazeShaders](https://github.com/chrisbanes/haze/blob/main/haze/src/commonMain/kotlin/dev/chrisbanes/haze/HazeShaders.kt)
class.
- The original liquid lens effect was inspired by ShaderToy user [4eckme](https://www.shadertoy.com/user/4eckme) with their
[Liquid Glass example](https://www.shadertoy.com/view/wcKSRD).
- The current spherical liquid lens effect was inspired by GitHub user [Kyant0](https://github.com/Kyant0) with their own
[AndroidLiquidGlass](https://github.com/Kyant0/AndroidLiquidGlass) library.
- The dispersion effect was inspired by ShaderToy user [PuZo](https://www.shadertoy.com/user/PuZo) with their
[Chromatic Aberration example](https://www.shadertoy.com/view/ltByR3).
- Tobias Bjørkli [@tobiasbjorkli](https://www.pexels.com/@tobiasbjorkli/) for the [northern_lights.webp](./samples/app/src/main/res/drawable-nodpi/northern_lights.webp)
- Vlad Alexandru Popa [@vladalex94](https://www.pexels.com/@vladalex94/) for the [ny_city.webp](./samples/app/src/main/res/drawable-nodpi/ny_city.webp).
- Romain Guy [romainguy.dev](https://www.romainguy.dev/) for the [dotonbori.webp](./samples/app/src/main/res/drawable-nodpi/dotonbori.webp).
- Efrem Efre [@efrem-efre-2786187](https://www.pexels.com/@efrem-efre-2786187/) for the [prague_clock.webp](./samples/app/src/main/res/drawable-nodpi/prague_clock.webp).

## License

```
Copyright 2025 Colin McKee

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
