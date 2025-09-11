# Liquid

[![Maven Central](https://img.shields.io/maven-central/v/io.github.fletchmckee.liquid/liquid)](https://search.maven.org/search?q=g:io.github.fletchmckee.liquid)
![Build status](https://github.com/fletchmckee/liquid/actions/workflows/build.yml/badge.svg)
![Android](https://img.shields.io/badge/Android-3DDC84?style=flat&logo=android&logoColor=white)

**Liquid** is an Android library that brings [Liquid Glass](https://developer.apple.com/documentation/SwiftUI/Applying-Liquid-Glass-to-custom-views)-style effects to Jetpack Compose.
It lets you refract, frost, and curve the content behind your UI elements, creating dynamic frosted glass, lens, and liquid-like effects.

Powered by RuntimeShaders, Android Graphics Shading Language (AGSL) and ModifierNodeElement APIs, it delivers GPU-accelerated visuals to your Compose UI.

<div align="center">
  <img src="https://github.com/user-attachments/assets/de63e6ae-e662-477d-b0c1-a26f4aad2a2d" width="400" />
</div>

## Getting Started

**Add `mavenCentral()` and Liquid to your list of repositories and dependencies:**

```gradle
repositories {
  mavenCentral() // Release versions
  maven {
    url = uri("https://central.sonatype.com/repository/maven-snapshots/") // Snapshot versions
  }
}

dependencies {
  implementation("io.github.fletchmckee.liquid:liquid:0.1.0-alpha2")
}
```
## Usage

A modifier node can’t see pixels drawn behind it or by its ancestors. Liquid mirrors the approach popularized by [Haze](https://github.com/chrisbanes/haze) via the shared state/source/effect pattern:

- **Shared state** - The `LiquidState` manages tracking all source nodes that should be shared with the effect nodes.
- **Source** - You explicitly tag composables whose output should be sampled with `Modifier.liquefiable(liquidState)`. These are recorded into a GraphicsLayer (API 31+).
- **Effect** - `Modifier.liquid(liquidState)` renders those layers through AGSL shaders and draws the liquid effect upon the sampled content.

Below is a simple example of how to coordinate this pattern:

```kotlin
@Composable
fun LiquidScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
) = Box(modifier) {
  // Source background to be sampled.
  ImageBackground(
    Modifier
      .fillMaxSize()
      .liquefiable(liquidState),
  )
  // Effect button that samples the background to create the liquid effect.
  LiquidButton(
    Modifier
      .align(Alignment.TopStart)
      .liquid(liquidState), // Applies the default liquid effect.
  )
}
```
> [!IMPORTANT]
> A `liquid` node cannot have ancestor `liquefiable` nodes outside of its own Modifier chain using the same `LiquidState`. Doing so will result in a fatal SIGSEGV exception.
> See [Node Hirearchy](#node-hierarchy) under [Limitations](#limitations) for more information.

### LiquidScope

The `LiquidScope` block allows you to customize your liquid effects for each modifier. The default values are applied when no scope
block is passed, however you can alter any one of these fields to achieve the desired effect:

```kotlin
Modifier
  .liquid(liquidState) {
    frost = 10.dp // Defaults to 0.dp
    shape = RoundedCornerShape(25) // Defaults to CircleShape
    refraction = 0.5f // Defaults to 0.25f
    curve = 0.5f // Defaults to 0.25f
    edge = 0.1f // Defaults to 0f
    tint = Color.White.copy(alpha = 0.2f) // Defaults to Color.Unspecified
  }
```

#### Frost

The frost parameter blurs the background contents resulting in a frost-like effect.
You can also apply this effect while setting `refraction` or `curve` to 0f if you only want a blur effect. Any value below 0.dp is ignored.
> [!NOTE]
> This is the most expensive property in the LiquidScope as it creates extras RuntimeShaders to achieve the blur effect.

<div align="center">
  <img src="https://github.com/user-attachments/assets/51f12425-7f0f-4dbe-85d9-a3eea98d2c74" width="400" />
</div>

#### Shape

The shape is just like setting a shape in a `background` or `clip` modifier. However the shape plays an important role in the lens distortion
that creates the liquid effect. It distorts around the corners, so it's recommended (but not required) to use rounded corners. Applying
`CircleShape` (RoundedCornerShape(50)) results in the best effect as it creates smooth distortions whether it is a true circle or a
capsule-shaped composable.

#### Refraction

TBD

#### Curve

TBD

#### Edge

TBD

#### Tint

This is an optional value that is mainly provided for convenience. Most use cases will require some tint, so you can avoid applying an
additional `background` modifier by setting everything in your `liquid` modifier.

## Limitations

#### SDK Level

The minimum API level that will display the liquid effects is 33 (Android 13). This is essentially a RuntimeShader library, so this limits what is possible for API 32 and lower.

- **API 31+** - RenderEffects are available, so we will still create a frost effect using Android's [BlurEffect](https://github.com/androidx/androidx/blob/7cca76e55aaa9c2ff1a038bac0fa2b91cd04dcff/compose/ui/ui-graphics/src/androidMain/kotlin/androidx/compose/ui/graphics/AndroidRenderEffect.android.kt#L50). We also draw a lower quality version of the edge effect. To disable, you can set the `LiquidScope.edge` property to 0f. The `LiquidScope.refraction` and `LiquidScope.curve` properties are ignored. The `LiquidScope.tint` and `LiquidScope.shape` values produce the same effect as API 33+.
- **API 30 and lower** - Has all of the above features outside of the frost effect.

#### Node Hierarchy

The `liquid` modifier cannot be used on nodes that are descendants of `liquefiable` nodes due to how the rendering pipeline works.

When a liquid node renders, it follows this process:

1. It first captures all available liquefiable nodes into a graphics layer to use as sampling sources.
2. This capture process requires drawing each liquefiable node and its entire subtree.
3. If a liquefiable node contains a liquid node as a descendant, this creates infinite recursion as the liquid node tries to capture its ancestor, which tries to draw the liquid node, which tries to capture its ancestor again.

However, the same modifier chain can contain a `liquefiable` node and a `liquid` node. This can be useful as you may want liquid effects to be able to sample other liquid effects:

**Do**
```kotlin
// The two nodes are applied to the same modifier chain.
// Make sure to place the `liquefiable` before the `liquid` modifier.
@Composable
fun LiquefiableAndLiquid(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
) = Box(
  modifier
    .liquefiable(liquidState)
    .liquid(liquidState),
) {
  // Some UI content.
  // See LiquidSliders in :samples:app as an example.
}
```
**Don't**
```kotlin
@Composable
fun LiquefiableWithLiquidDescendant(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
) = Box(
  modifier.liquefiable(liquidState)
) {
  // Will cause recursive draws!
  Box(Modifier.liquid(liquidState))
}
```

#### Rotation, Skew and Scale animations

The current effects are built to handle alpha and translation changes, however handling rotation, skew and scale animations is not yet supported.
Your liquid effect nodes can do all of those animations, it's just that the liquefiable source nodes that are rendered into the effect nodes will not be drawn accurately.
This will require complex matrix transformations and while this is possible and a future goal, it is not yet supported.

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
- Romain Guy [romainguy.dev](https://www.romainguy.dev/) for the [dotonbori.webp](./samples/app/src/main/res/drawable-nodpi/dotonbori.webp).

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
