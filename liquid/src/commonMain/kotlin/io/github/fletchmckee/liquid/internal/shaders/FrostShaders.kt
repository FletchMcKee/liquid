// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal.shaders

// Derived from Haze's
// [HazeShader](https://github.com/chrisbanes/haze/blob/1.6.10/haze/src/commonMain/kotlin/dev/chrisbanes/haze/HazeShaders.kt)
internal val HorizontalFrostShader by lazy(LazyThreadSafetyMode.NONE) {
  frostShader(false)
}

internal val VerticalFrostShader by lazy(LazyThreadSafetyMode.NONE) {
  frostShader(true)
}

// Similar to Haze's shader, but adjusted with cornerRadii and a single shader.
internal fun frostShader(vertical: Boolean): String = """
  uniform shader content;
  uniform float4 effectRect;
  uniform float blurRadius;
  uniform float4 cornerRadii;

  const float MAX_RADIUS = 150.0;

  float gaussian(float x, float sigma) {
    return exp(-(x * x) / (2.0 * sigma * sigma));
  }

  float shape(in float2 p, in float2 b, in half4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    float2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
  }

  bool isInsideShape(float2 coord, float2 liquidSize, float minDim, float2 normalizedSize, half4 vr) {
    float2 localCoord = coord - effectRect.xy;
    float2 uv = localCoord / liquidSize;
    float2 m2 = uv - 0.5;
    float2 shapeCoord = m2 * normalizedSize;
    float2 shapeSize = normalizedSize * 0.5;

    float sdf = shape(shapeCoord, shapeSize, vr);
    return sdf <= 0.0;
  }

  half4 main(float2 fragCoord) {
    float2 liquidSize = effectRect.zw - effectRect.xy;
    float minDim = min(liquidSize.x, liquidSize.y);
    float2 normalizedSize = liquidSize / minDim;
    half4 vr = half4(cornerRadii) / minDim;

    if (!isInsideShape(fragCoord, liquidSize, minDim, normalizedSize, vr)) {
      // If outside the shape, just return as transparent.
      return half4(0.0);
    }

    float r = floor(blurRadius);
    float sigma = max(blurRadius / 2.0, 1.0);

    float weightSum = 1.0;
    half4 result = content.eval(fragCoord);

    for (float i = 1.0; i < MAX_RADIUS; i += 2.0) {
      if (i >= r) break;

      float weightL = gaussian(i, sigma);
      float weightH = gaussian(i + 1.0, sigma);
      float weight = weightL + weightH;

      float2 offset = ${if (vertical) "float2(0.0, i + weightH / weight)" else "float2(i + weightH / weight, 0.0)"};

      float2 coordMinus = fragCoord - offset;
      float2 coordPlus = fragCoord + offset;

      if (isInsideShape(coordMinus, liquidSize, minDim, normalizedSize, vr)) {
        result += weight * content.eval(coordMinus);
        weightSum += weight;
      }

      if (isInsideShape(coordPlus, liquidSize, minDim, normalizedSize, vr)) {
        result += weight * content.eval(coordPlus);
        weightSum += weight;
      }
    }

    // Handle odd radius by sampling one more tap if necessary.
    if (r < MAX_RADIUS && mod(r, 2.0) == 1.0) {
      float weight = gaussian(r, sigma);
      float2 offset = ${if (vertical) "float2(0.0, r)" else "float2(r, 0.0)"};

      float2 coordMinus = fragCoord - offset;
      float2 coordPlus = fragCoord + offset;

      if (isInsideShape(coordMinus, liquidSize, minDim, normalizedSize, vr)) {
        result += weight * content.eval(coordMinus);
        weightSum += weight;
      }

      if (isInsideShape(coordPlus, liquidSize, minDim, normalizedSize, vr)) {
        result += weight * content.eval(coordPlus);
        weightSum += weight;
      }
    }

    return result / weightSum;
  }
"""
