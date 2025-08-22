// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal.shaders

import org.intellij.lang.annotations.Language

// This is all heavily inspired by
// [Haze](https://github.com/chrisbanes/haze/blob/main/haze/src/commonMain/kotlin/dev/chrisbanes/haze/HazeShaders.kt)
internal val HorizontalFrostShader by lazy(LazyThreadSafetyMode.NONE) {
  frostShader(false)
}

internal val VerticalFrostShader by lazy(LazyThreadSafetyMode.NONE) {
  frostShader(true)
}

@Language("AGSL")
internal fun frostShader(vertical: Boolean): String = """
  uniform shader content;
  uniform float4 effectRect;
  uniform float blurRadius;
  uniform float cornerRadius;

  const float maxRadius = 150.0;

  float gaussian(float x, float sigma) {
    return exp(-(x * x) / (2.0 * sigma * sigma));
  }

  // SDF for rounded rectangle to respect shape boundaries
  float roundedRectangleSDF(float2 position, float2 box, float radius) {
    float2 q = abs(position) - box + float2(radius);
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius;
  }

  bool isInsideShape(float2 coord, float2 rectCenter, float2 rectHalfSize) {
    float2 localPos = coord - rectCenter;
    return roundedRectangleSDF(localPos, rectHalfSize, cornerRadius) <= 0.0;
  }

  half4 main(float2 coord) {
    float2 rectSize = effectRect.zw - effectRect.xy;
    float2 rectCenter = effectRect.xy + rectSize * 0.5;
    float2 rectHalfSize = rectSize * 0.5;

    float r = floor(blurRadius);
    float sigma = max(blurRadius / 2.0, 1.0);

    float weightSum = 1.0;
    half4 result = content.eval(coord);

    for (float i = 1.0; i < maxRadius; i += 2.0) {
      if (i >= r) break;

      float weightL = gaussian(i, sigma);
      float weightH = gaussian(i + 1.0, sigma);
      float weight = weightL + weightH;

      float2 offset = ${if (vertical) "float2(0.0, i + weightH / weight)" else "float2(i + weightH / weight, 0.0)"};

      float2 coordMinus = coord - offset;
      float2 coordPlus = coord + offset;

      if (isInsideShape(coordMinus, rectCenter, rectHalfSize)) {
        result += weight * content.eval(coordMinus);
        weightSum += weight;
      }

      if (isInsideShape(coordPlus, rectCenter, rectHalfSize)) {
        result += weight * content.eval(coordPlus);
        weightSum += weight;
      }
    }

    // Handle odd radius by sampling one more tap if necessary
    if (r < maxRadius && mod(r, 2.0) == 1.0) {
      float weight = gaussian(r, sigma);
      float2 offset = ${if (vertical) "float2(0.0, r)" else "float2(r, 0.0)"};

      float2 coordMinus = coord - offset;
      float2 coordPlus = coord + offset;

      if (isInsideShape(coordMinus, rectCenter, rectHalfSize)) {
        result += weight * content.eval(coordMinus);
        weightSum += weight;
      }

      if (isInsideShape(coordPlus, rectCenter, rectHalfSize)) {
        result += weight * content.eval(coordPlus);
        weightSum += weight;
      }
    }

    return result / weightSum;
  }
""".trimIndent()
