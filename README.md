# Liquid

[![Maven Central](https://img.shields.io/maven-central/v/io.github.fletchmckee.liquid/liquid)](https://search.maven.org/search?q=g:io.github.fletchmckee.liquid)
![Build status](https://github.com/fletchmckee/liquid/actions/workflows/build.yml/badge.svg)
![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)

**Liquid** is an Android library that provides 'Liquid Glass' effects for Jetpack Compose.

<div align="center">
  <img src="https://github.com/user-attachments/assets/de63e6ae-e662-477d-b0c1-a26f4aad2a2d" width="400" />
</div>

## Getting Started

**Make sure that you have `mavenCentral()` in your list of repositories and add Liquid to your dependencies:**

```gradle
repositories {
  mavenCentral() // Release versions
  maven {
    url = uri("https://central.sonatype.com/repository/maven-snapshots/") // Snapshot versions
  }
}

dependencies {
  implementation("io.github.fletchmckee.liquid:liquid:0.1.0-alpha")
}
```

## Acknowledgements

- The [Haze](https://github.com/chrisbanes/haze) library developed by [Chris Banes](https://github.com/chrisbanes) was a large source of
inspiration, particularly for the use of content and effect `Modifier` nodes and blur techniques. Specifically, the
[FrostShaders](./liquid/src/main/kotlin/io/github/fletchmckee/liquid/internal/shaders/FrostShaders.kt) available for the liquid nodes are
derived from the [HazeShaders](https://github.com/chrisbanes/haze/blob/main/haze/src/commonMain/kotlin/dev/chrisbanes/haze/HazeShaders.kt)
class.
- The liquid lens effect was inspired by ShaderToy user [4eckme](https://www.shadertoy.com/user/4eckme) with their
[Liquid Glass example](https://www.shadertoy.com/view/wcKSRD).
- Tobias Bjørkli [@tobiasbjorkli](https://www.pexels.com/@tobiasbjorkli/) for the [northern_lights.webp](./samples/app/src/main/res/drawable-nodpi/northern_lights.webp)
- Vlad Alexandru Popa [@vladalex94](https://www.pexels.com/@vladalex94/) for the [ny_city.webp](./samples/app/src/main/res/drawable-nodpi/ny_city.webp).

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
