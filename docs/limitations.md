#### SDK Level

The minimum API level that will display the liquid effects is 33 (Android 13). This is effectively a RuntimeShader library, so this limits what is possible for API 32 and lower.

- **API 31+** - RenderEffects are available, so we will still create a frost effect using Android's [BlurEffect](https://github.com/androidx/androidx/blob/7cca76e55aaa9c2ff1a038bac0fa2b91cd04dcff/compose/ui/ui-graphics/src/androidMain/kotlin/androidx/compose/ui/graphics/AndroidRenderEffect.android.kt#L50). We also draw a lower quality version of the edge effect. To disable, you can set the `edge` property to 0f. The `refraction`, `curve` and `dispersion` properties are ignored. The `tint`, `shape` and `saturation` values produce the same effect as API 33+.
- **API 30 and lower** - Has all of the above features outside of the `frost` effect.

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
