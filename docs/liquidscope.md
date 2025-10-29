The `LiquidScope` block allows you to customize your liquid effects for each modifier. The default values are applied when no scope
block is passed, however you can alter any one of the below properties to achieve your desired effect.

### Frost

Blurs the background contents resulting in a frost-like effect.
You can also apply this effect while setting `refraction` or `curve` to 0f if you only want a blur effect. Any value below 0.dp is ignored.

This property is no-op on Android 11 and lower.

<div align="center">
  <img
    src="https://github.com/user-attachments/assets/673b60d3-e10d-4c02-b197-956335bcfbe4"
    alt="Demo of the frost effect"
    width="400" />
</div>

### Shape

Similar to setting a shape in a `background` or `clip` modifier. However the shape plays an important role in the lens distortion
that creates the liquid effect. It distorts around the corners, so it's recommended (but not required) to use rounded corners. Applying
`CircleShape` (RoundedCornerShape(50)) results in the best effect as it creates smooth distortions whether it is a true circle or a
capsule-shaped composable.

<div align="center">
  <img
    src="https://github.com/user-attachments/assets/2808d488-2a37-40b0-9c7c-81af735b00e4"
    alt="Demo of the shape's corner radius changing"
    width="400" />
</div>

### Refraction

Controls how much the background distorts through the liquid lens. Setting this to 0f removes the liquid effect altogether, nullifying any `curve` value.

This property is no-op on Android 12 and lower.

<div align="center">
  <img
    src="https://github.com/user-attachments/assets/bf247862-1555-4e7a-b82f-1966f3b58493"
    alt="Demo of the refraction effect"
    width="400" />
</div>

### Curve

Adjusts how strongly the liquid lens curves at its center vs. edges. Setting this to 0f removes the liquid effect altogether, nullifying any `refraction` value.

This property is no-op on Android 12 and lower.

<div align="center">
  <img
    src="https://github.com/user-attachments/assets/c682499c-b0a4-494b-98a5-6cb3aad576f8"
    alt="Demo of the curve effect"
    width="400" />
</div>

### Edge

Determines the width of the rim lighting around the effect's edge. Higher values create a wider, softer edge and expand the region where rim lighting is applied. Set to `0f` to disable this effect.

On Android 12 and lower, this becomes a boolean where a value > 0f draws a fixed width edge effect, and 0f removes it.

<div align="center">
  <img
    src="https://github.com/user-attachments/assets/cb70a6e4-3fc8-4736-907a-08e2df19a355"
    alt="Demo of the edge effect"
    width="400" />
</div>

### Tint

This is an optional value that is mainly provided for convenience. Most use cases will require some tint, so you can avoid applying an
additional `background` modifier by setting everything in your `liquid` modifier.

### Saturation

Adjusts the vibrancy of the sampled pixels. A value greater than 1f create more vibrant and vivid colors, while a value less than 1f
creates duller and more muted colors.

<div align="center">
  <img
    src="https://github.com/user-attachments/assets/dfb25b70-7f56-4724-b697-f4e4ea849be6"
    alt="Demo of the saturation effect"
    width="400" />
</div>

### Dispersion

Controls the strength of chromatic aberration, separating colors near the edges of the effect to create a prism-like effect.

This property is no-op on Android 12 and lower.

<div align="center">
  <img
    src="https://github.com/user-attachments/assets/c9e55c80-fa28-4192-a791-154143bdcda9"
    alt="Demo of the dispersion effect"
    width="400" />
</div>
