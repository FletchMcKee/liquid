#### SDK Level for Android

This only applies for the Android target.

The minimum API level that will display the liquid effects is 33 (Android 13). This is effectively a RuntimeShader library, so this limits what is possible for API 32 and lower.

- **API 31+:**
  - The `refraction`, `curve` and `dispersion` properties are ignored.
  - We draw a lower quality version of the edge effect. To disable, you can set the `edge` property to 0f.
  - All other values produce the same effect as API 33+.
- **API 30 and lower:**
  - Has all of the above features except `frost` is ignored as RenderEffects are unavailable.

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
  // See LiquidControls in :samples:app as an example.
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

#### RotationX/Y and Skew Animations

The current effects are built to handle alpha, scale, rotationZ and translation changes. However handling rotationX/Y and skew animations is not supported.
Your liquid effect nodes can do all of those animations, it's just that the liquefiable source nodes that are rendered into the effect nodes will not be drawn accurately.

#### Frost/Lens Effect in LazyList Items

Attempting to apply a `liquid` effect with non-zero `frost/refraction/curve` values presents a challenge when used in LazyLists. When
offscreen items are being scrolled on screen, the visible edge may attempt to pull pixels from offscreen that don't exist and are instead an
approximation of the Skia blur effect. This can result in glitchy UI until the pixels that the lens effect pulls towards the edge are on
screen.

This is not an issue when using `refraction/curve` without a `frost` effect or vice versa. This also shouldn't be an issue if your LazyList
is behind a Scaffold with a TopAppBar and BottomAppBar as it should have proper pixels to sample by the time it is appearing on screen.

We could provide the viewport as a uniform in the RuntimeShader, but this would hinder performance as it would require recreating the
RenderEffect every time a `liquid` effect's position on screen changes which is frequent with LazyLists. It would also add additional
complexity to account for scale and rotation. We may provide TileMode as an additional LiquidScope property to give more control over the
edge treatment, but this is a confusing API in regards to the blur effect that doesn't fully solve the problem.

For now, it is recommended to either place your `liquid` LazyList behind a Scaffold, or use low `refraction/curve` values (0.1 or less) when
using a high `frost` (10.dp or more) or vice versa.
