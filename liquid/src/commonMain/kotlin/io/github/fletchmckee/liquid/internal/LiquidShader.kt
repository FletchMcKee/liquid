// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

internal const val LiquidShader = """
  uniform shader content;
  uniform float2 size;
  uniform float4 cornerRadii;
  uniform float refraction;
  uniform float curve;
  uniform float edge;
  layout(color) uniform half4 tint;
  uniform float saturation;
  uniform float dispersion;
  uniform float contrast;

  const float AA_WIDTH_PX = 1.5;

  float computeSdf(in float2 p, in float2 b, in half4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw; // xy is right quadrant, zw is left
    r.x = (p.y > 0.0) ? r.x : r.y; // x is bottom quadrant, y is top
    float2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
  }

  // See `sdgBox` for reference:
  // https://iquilezles.org/articles/distgradfunctions2d/
  float2 computeSdfNormal(float2 pos, float2 shapeSize, half4 vr) {
    vr.xy = (pos.x > 0.0) ? vr.xy : vr.zw;
    vr.x = (pos.y > 0.0) ? vr.x : vr.y;
    float r = vr.x;
    float2 w = abs(pos) - (shapeSize - r);
    float2 s = float2(pos.x < 0.0 ? -1.0 : 1.0, pos.y < 0.0 ? -1.0 : 1.0);
    float g = max(w.x, w.y);
    float2 q = max(w, 0.0);
    float l = length(q);

    // Multiply by s to restore quadrant signs.
    return s * ((g > 0.0) ? q / l : ((w.x > w.y) ? float2(1.0, 0.0) : float2(0.0, 1.0)));
  }

  half3 applyColorAdjustments(half3 color) {
    float lum = dot(color, half3(0.2126, 0.7152, 0.0722));
    color = saturate(mix(half3(lum), color, saturation));
    return saturate((color - 0.5) * contrast + 0.5);
  }

  half4 main(float2 fragCoord) {
    float minDimension = min(size.x, size.y);
    float2 center = size * 0.5;
    float2 shapeCoord = (fragCoord - center) / minDimension;
    float2 shapeSize = size / minDimension;
    float2 halfShapeSize = shapeSize * 0.5;
    half4 shapeVr = half4(cornerRadii);
    float shapeSdf = computeSdf(shapeCoord, halfShapeSize, shapeVr);

    // Using 1.5 softens jagged pixels around corners without softening the edge too much.
    float aaWidth = AA_WIDTH_PX / minDimension;
    if (shapeSdf > aaWidth) {
      return half4(0.0);
    }

    half4 lensVr = min(shapeVr * 1.5, half4(min(shapeSize.x, shapeSize.y) * 0.5));
    float2 sdfNormal = computeSdfNormal(shapeCoord, halfShapeSize, lensVr);

    half4 fragColor;
    float2 baseCoord = fragCoord;
    // Applies the lens effect.
    if (refraction > 0.0 && curve > 0.0) {
      float lensDepth = 1.0 - saturate(-shapeSdf / refraction);
      float distortion = 1.0 - sqrt(1.0 - lensDepth * lensDepth);
      float normalDisplacement = distortion * -curve * minDimension;
      baseCoord = fragCoord + normalDisplacement * sdfNormal;
    }

    // Applies the dispersion effect.
    if (dispersion > 0.0) {
      float2 distFromCenter = (fragCoord - center) / size;
      // Cubic multiply is more efficient than pow() for integer exponents.
      // Also pow() didn't work for screenshot tests while this does.
      float2 aberration = dispersion * distFromCenter * distFromCenter * distFromCenter * minDimension;

      float2 coordR = baseCoord - aberration;
      float2 coordG = baseCoord;
      float2 coordB = baseCoord + aberration;

      // Check if aberrated samples fall outside the shape.
      float2 shapeCoordR = (coordR - center) / minDimension;
      float2 shapeCoordB = (coordB - center) / minDimension;
      bool validR = computeSdf(shapeCoordR, halfShapeSize, shapeVr) <= 0.0;
      bool validB = computeSdf(shapeCoordB, halfShapeSize, shapeVr) <= 0.0;

      // Use the existing coord (which is colorG) if the red or blue coord fall outside of the shape.
      half4 colorG = content.eval(coordG);
      half4 colorR = validR ? content.eval(coordR) : colorG;
      half4 colorB = validB ? content.eval(coordB) : colorG;
      fragColor = half4(colorR.r, colorG.g, colorB.b, colorG.a);
    } else {
      fragColor = content.eval(baseCoord);
    }

    if (fragColor.a <= 0.0) {
      fragColor = content.eval(fragCoord);
    }

    fragColor.rgb = applyColorAdjustments(fragColor.rgb);
    // Apply the provided tint before the lighting effects but after color adjustments.
    // Otherwise we saturate/contrast the user's provided tint instead of the source content.
    fragColor.rgb = mix(fragColor.rgb, tint.rgb, tint.a);

    float edgeSmooth = smoothstep(-edge, 0.0, shapeSdf);
    // Eventually this will become a uniform.
    float2 lightDirection = float2(-0.15, -0.15);
    float nDotL = abs(dot(sdfNormal, lightDirection));
    float edgeLighting = edgeSmooth * nDotL;
    fragColor.rgb += edgeLighting;

    // Centers the AA gradient to the edge of our shape.
    float aaAlpha = 1.0 - smoothstep(-aaWidth * 0.5, aaWidth * 0.5, shapeSdf);
    return fragColor * aaAlpha;
  }
"""
