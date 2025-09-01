// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal.shaders

import org.intellij.lang.annotations.Language

@Language("AGSL")
internal const val LiquidShader = """
  uniform shader content;
  uniform float4 effectRect;
  uniform float4 cornerRadii;
  uniform float refraction;
  uniform float curve;
  uniform float edge;

  const float HALF_PI = 1.57079633;

  float shape(in float2 p, in float2 b, in half4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw; // xy is right quadrant, zw is left
    r.x = (p.y > 0.0) ? r.x : r.y; // x is bottom quadrant, y is top
    float2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
  }

  float2 computeSdfNormal(float2 pos, float2 shapeSize, half4 vr) {
    float eps = 0.001;
    float sdfX1 = shape(pos + float2(eps, 0.0), shapeSize, vr);
    float sdfX2 = shape(pos - float2(eps, 0.0), shapeSize, vr);
    float sdfY1 = shape(pos + float2(0.0, eps), shapeSize, vr);
    float sdfY2 = shape(pos - float2(0.0, eps), shapeSize, vr);
    return normalize(float2(sdfX1 - sdfX2, sdfY1 - sdfY2));
  }

  half4 main(float2 fragCoord) {
    float2 liquidSize = effectRect.zw - effectRect.xy;
    // The fragCoord is larger than the effect coordinates as the frostRadius has been added as additional padding.
    float2 localCoord = fragCoord - effectRect.xy;

    float2 uv = localCoord / liquidSize;
    float2 m2 = uv - 0.5;

    float minDim = min(liquidSize.x, liquidSize.y);
    float2 normalizedSize = liquidSize / minDim;

    float2 shapeCoord = m2 * normalizedSize;
    float2 shapeSize = normalizedSize * 0.5;
    // Scale corner radii to normalized space.
    half4 vr = half4(cornerRadii) / minDim;
    float sdf = shape(shapeCoord, shapeSize, vr);

    float liquidMask = saturate(-sdf * (max(liquidSize.x, liquidSize.y) * 0.5));
    float transition = smoothstep(0.0, 1.0, liquidMask);

    half4 fragColor;
    // Only apply lens effect if refraction and curve are both positive
    if (refraction > 0.0 && curve > 0.0) {
      // The lens is most responsible for the overall effect.
      // Sine creates a smooth curve from 0 to 1 as input goes from 0 (sin(0) = 0) to π/2 (sin(π/2) = 1).
      // Credit to ShaderToy user [4eckme](https://www.shadertoy.com/user/4eckme) for this lens formula.
      // https://www.shadertoy.com/view/wcKSRD
      float2 lens = m2 * sin(pow(saturate(-sdf / refraction), curve) * HALF_PI) + 0.5;
      float2 lensCoord = effectRect.xy + lens * liquidSize;
      fragColor = content.eval(lensCoord);
    } else {
      // No lens effect, just use the original coordinates.
      fragColor = content.eval(fragCoord);
    }

    float edgeSmooth = smoothstep(-edge, 0.0, sdf);

    float2 lightDirection = float2(-0.3, -0.3);
    float2 sdfNormal = computeSdfNormal(shapeCoord, shapeSize, vr);

    float nDotL = dot(sdfNormal, normalize(lightDirection));
    float edgeLightingTop = edgeSmooth * saturate(nDotL) * 0.2;
    float edgeLightingBottom = edgeSmooth * saturate(-nDotL) * 0.2;

    fragColor.rgb += edgeLightingTop;
    fragColor.rgb += edgeLightingBottom;

    return mix(content.eval(fragCoord), fragColor, transition);
  }
"""
