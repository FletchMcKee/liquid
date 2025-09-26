// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal.shaders

import org.intellij.lang.annotations.Language

@Language(value = "AGSL")
internal const val LiquidShader = """
  uniform shader content;
  uniform float4 effectRect;
  uniform float4 cornerRadii;
  uniform float refraction;
  uniform float curve;
  uniform float edge;
  layout(color) uniform half4 tint;

  const float EPSILON = 0.001;

  float computeSdf(in float2 p, in float2 b, in half4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw; // xy is right quadrant, zw is left
    r.x = (p.y > 0.0) ? r.x : r.y; // x is bottom quadrant, y is top
    float2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
  }

  float2 computeSdfNormal(float2 pos, float2 shapeSize, half4 vr) {
    float sdfX1 = computeSdf(pos + float2(EPSILON, 0.0), shapeSize, vr);
    float sdfX2 = computeSdf(pos - float2(EPSILON, 0.0), shapeSize, vr);
    float sdfY1 = computeSdf(pos + float2(0.0, EPSILON), shapeSize, vr);
    float sdfY2 = computeSdf(pos - float2(0.0, EPSILON), shapeSize, vr);
    return normalize(float2(sdfX1 - sdfX2, sdfY1 - sdfY2));
  }

  half4 main(float2 fragCoord) {
    float2 liquidSize = effectRect.zw - effectRect.xy;

    float minDimension = min(liquidSize.x, liquidSize.y);
    float2 center = effectRect.xy + liquidSize * 0.5;
    float2 shapeCoord = (fragCoord - center) / minDimension;
    float2 shapeSize = liquidSize / minDimension;
    half4 shapeVr = half4(cornerRadii) / minDimension;
    float shapeSdf = computeSdf(shapeCoord, shapeSize * 0.5, shapeVr);

    // Using maxDimension for a sharper transition.
    float maxDimension = max(liquidSize.x, liquidSize.y);
    float liquidMask = saturate(-shapeSdf * maxDimension);
    float transition = smoothstep(0.0, 1.0, liquidMask);

    half4 lensVr = min(shapeVr * 1.5, half4(min(shapeSize.x, shapeSize.y) * 0.5));
    float2 sdfNormal = computeSdfNormal(shapeCoord, shapeSize * 0.5, lensVr);

    half4 fragColor;
    if (refraction > 0.0 && curve > 0.0) {
      // Using sdf radial-based distortion instead of sine-based, much closer to Apple's effect.
      float lensDepth = 1.0 - saturate(-shapeSdf / refraction);
      float distortion = 1.0 - sqrt(1.0 - lensDepth * lensDepth);
      float normalDisplacement = distortion * -curve * minDimension;
      float2 lensCoord = fragCoord + normalDisplacement * sdfNormal;

      fragColor = content.eval(lensCoord);
    } else {
      // No lens effect, just use the original coordinates
      fragColor = content.eval(fragCoord);
    }

    float edgeSmooth = smoothstep(-edge, 0.0, shapeSdf);
    // Eventually this will become a uniform.
    float2 lightDirection = float2(-0.15, -0.15);
    float nDotL = abs(dot(sdfNormal, lightDirection));
    float edgeLighting = edgeSmooth * nDotL;
    fragColor.rgb += edgeLighting;
    // Apply the provided tint.
    fragColor.rgb = mix(fragColor.rgb, tint.rgb, tint.a);

    return mix(content.eval(fragCoord), fragColor, transition);
  }
"""
