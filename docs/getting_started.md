**Add `mavenCentral()` and Liquid to your list of repositories and dependencies:**

```gradle
repositories {
  // Release versions
  mavenCentral()
  // Snapshot versions
  maven {
    url = uri("https://central.sonatype.com/repository/maven-snapshots/")
  }
}

dependencies {
  implementation("io.github.fletchmckee.liquid:liquid:1.0.0")
}
```

## Usage

A modifier node can’t see pixels drawn behind it or by its ancestors. Liquid mirrors the approach popularized by [Haze](https://github.com/chrisbanes/haze) via the shared state/source/effect pattern:

- **Shared state** - The `LiquidState` manages tracking all source nodes that should be shared with the effect nodes.
- **Source** - You explicitly tag composables whose output should be sampled with `Modifier.liquefiable(liquidState)`. These are recorded into a GraphicsLayer.
- **Effect** - `Modifier.liquid(liquidState)` renders those layers through AGSL shaders and draws the liquid effect upon the sampled content.

Below is a simple example of how to coordinate this pattern:

``` kotlin
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
      .liquid(liquidState) { // (1)!
        // Defaults to 0.dp
        frost = 10.dp
        // Defaults to CircleShape
        shape = RoundedCornerShape(25)
        // Defaults to 0.25f
        refraction = 0.5f
        // Defaults to 0.25f
        curve = 0.5f
        // Defaults to 0f
        edge = 0.1f
        // Defaults to Color.Unspecified
        tint = Color.White.copy(alpha = 0.2f)
        // Defaults to 1f
        saturation = 1.5f
        // Defaults to 0f
        dispersion = 0.25f
      }
  )
}
```

1.  See [LiquidScope](liquidscope.md) for more information.

!!! important
    A `liquid` node cannot have ancestor `liquefiable` nodes outside of its own Modifier chain using the same `LiquidState`. Doing so will result in a fatal SIGSEGV exception.
    See [Node Hirearchy](limitations.md#node-hierarchy) under [Limitations](limitations.md) for more information.
