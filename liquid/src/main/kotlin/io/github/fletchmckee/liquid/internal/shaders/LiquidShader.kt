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

  const float HALF_PI = 1.57079633;
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
    // Convert from absolute fragment coordinates to local coordinates relative to the effect bounds.
    float2 localCoord = fragCoord - effectRect.xy;

    float2 uv = localCoord / liquidSize;
    float2 m2 = uv - 0.5;

    // The shapeSdf is normalized by minDimension to preserve aspect ratio.
    float minDimension = min(liquidSize.x, liquidSize.y);
    float2 shapeSize = liquidSize / minDimension;
    float2 shapeCoord = m2 * shapeSize;
    half4 shapeVr = half4(cornerRadii) / minDimension;
    float shapeSdf = computeSdf(shapeCoord, shapeSize * 0.5, shapeVr);

    // The lensSdf is normalized by maxDimension to create uniform square distortion.
    // This prevents stretching artifacts in rectangular shapes.
    float maxDimension = max(liquidSize.x, liquidSize.y);
    float2 lensSize = liquidSize / maxDimension;
    float2 lensCoord = m2 * lensSize;
    half4 lensVr = half4(cornerRadii) / maxDimension;
    float lensSdf = computeSdf(lensCoord, lensSize * 0.5, lensVr);

    // Using maxDimension for a sharper transition.
    float liquidMask = saturate(-shapeSdf * maxDimension);
    float transition = smoothstep(0.0, 1.0, liquidMask);

    half4 fragColor;
    // Only apply lens effect if refraction and curve are both positive.
    if (refraction > 0.0 && curve > 0.0) {
      // Sine creates a smooth curve from 0 to 1 as input goes from 0 (sin(0) = 0) to π/2 (sin(π/2) = 1).
      // Credit to ShaderToy user [4eckme](https://www.shadertoy.com/user/4eckme) for this lens formula.
      // https://www.shadertoy.com/view/wcKSRD
      float2 lens = m2 * sin(pow(saturate(-lensSdf / refraction), curve) * HALF_PI) + 0.5;
      float2 sampleCoord = effectRect.xy + lens * liquidSize;
      fragColor = content.eval(sampleCoord);
    } else {
      // No lens effect, just use the original coordinates.
      fragColor = content.eval(fragCoord);
    }

    float edgeSmooth = smoothstep(-edge, 0.0, shapeSdf);
    float2 lightDirection = float2(-0.3, -0.3);
    float2 sdfNormal = computeSdfNormal(shapeCoord, shapeSize * 0.5, shapeVr);

    // Apple's liquid glass effects generally seem to have lighting from two opposite corners which is why for now
    // we have edgeLightingTop and its opposite edgeLightingBottom.
    float nDotL = dot(sdfNormal, normalize(lightDirection));
    float edgeLightingTop = edgeSmooth * saturate(nDotL) * 0.2;
    float edgeLightingBottom = edgeSmooth * saturate(-nDotL) * 0.2;

    fragColor.rgb += edgeLightingTop;
    fragColor.rgb += edgeLightingBottom;
    // Apply the provided tint.
    fragColor.rgb = mix(fragColor.rgb, tint.rgb, tint.a);

    return mix(content.eval(fragCoord), fragColor, transition);
  }
"""
