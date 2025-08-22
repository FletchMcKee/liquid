// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import org.intellij.lang.annotations.Language

@Language("AGSL")
internal val LiquidShader = """
  uniform shader content;
  uniform float2 resolution;
  uniform float blurRadius;
  uniform float cornerRadius;
  uniform float refraction;
  uniform float curve;
  uniform float sharp;
  layout(color) uniform half4 tintColor;

  const float PI = 3.14159265;
  const float PI_2 = PI * 2.0;
  const float HALF_PI = PI / 2.0;
  const float DIRECTIONS = 10.0;
  const float PI_DIRECTIONS = PI / DIRECTIONS;
  const float QUALITY = 10.0;

  float shape(in float2 p, in float2 b, in half4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    float2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
  }

  float2 sdfNormal(float2 pos, float2 shapeSize, half4 vr) {
    float eps = 0.001;
    float sdfX1 = shape(pos + float2(eps, 0.0), shapeSize, vr);
    float sdfX2 = shape(pos - float2(eps, 0.0), shapeSize, vr);
    float sdfY1 = shape(pos + float2(0.0, eps), shapeSize, vr);
    float sdfY2 = shape(pos - float2(0.0, eps), shapeSize, vr);
    return normalize(float2(sdfX1 - sdfX2, sdfY1 - sdfY2));
  }

  half4 blur(float2 lens, float2 ir) {
    if (blurRadius < 1.0) {
      // Skip blur sampling; just sample content with lens refraction
      return content.eval(lens * ir);
    }

    half4 color = half4(0.0);
    float totalWeight = 0.0;

    for (float d = 0.0; d < PI_2; d += PI_DIRECTIONS) {
      float2 dir = float2(cos(d), sin(d));
      for (float i = 1.0 / QUALITY; i <= 1.0; i += 1.0 / QUALITY) {
        float2 offset = dir * (blurRadius / ir.y) * i;

        // Gaussian-like weight that falls off with distance
        float weight = exp(-i * i * 3.0);

        color += content.eval((lens + offset) * ir) * weight;
        totalWeight += weight;
      }
    }

    return color / totalWeight;
  }

  half4 main(float2 fragCoord) {
    float2 ir = fragCoord;
    float aspectRatio = ir.x / ir.y;

    float2 uv = fragCoord / ir;
    float2 m2 = uv - 0.5;

    float2 shapeCoord = float2(m2.x * aspectRatio, m2.y);
    float2 shapeSize = float2(0.5 * aspectRatio, 0.5);

    float radius = cornerRadius / ir.y;
    half4 vr = half4(radius);

    float sdf = shape(shapeCoord, shapeSize, vr);
    float safeSharp = sharp != 0.0 ? sharp : 0.1;

    float liquidMask = saturate(-sdf / safeSharp * 32.0);
    float transition = smoothstep(0.0, 1.0, liquidMask);

    if (transition <= 0.0) {
      return content.eval(fragCoord);
    }

    // Sine creates a smooth curve from 0 to 1 as input goes 0 (sin(0) = 0) to π/2 (sin(π/2) = 1).
    // Credit goes to ShaderToy user [4eckme](https://www.shadertoy.com/user/4eckme) for this lens formula.
    // https://www.shadertoy.com/view/wcKSRD
    float2 lens = m2 * sin(pow(saturate(-sdf / refraction), curve) * HALF_PI) + 0.5;
    half4 fragColor = blur(lens, ir);

    float edgeSmooth = smoothstep(-sharp, 0.0, sdf);

    // Eventually I want this to be uniforms, but for now this works as a subtle gradient overlay, giving it a gloss appearance.
    float gradientFactor = (uv.x + uv.y) * 0.5;
    half4 gradientColor1 = half4(1.0, 1.0, 1.0, 0.05);
    half4 gradientColor2 = half4(1.0, 1.0, 1.0, 0.0);
    half4 gradientColor = mix(gradientColor1, gradientColor2, gradientFactor);
    fragColor.rgb = mix(fragColor.rgb, gradientColor.rgb, gradientColor.a);

    float2 lightDirection = float2(-0.4, -0.4);
    float2 sdfNormal = sdfNormal(shapeCoord, shapeSize, vr);

    // Dot product for lighting
    float nDotL = dot(sdfNormal, normalize(lightDirection));
    float edgeLighting = edgeSmooth * saturate(nDotL) * 0.3;
    float edgeLightingBottom = edgeSmooth * saturate(-nDotL) * 0.2;

    half4 lighting = fragColor;
    lighting.rgb += edgeLighting;
    lighting.rgb += edgeLightingBottom;

    // Apply the provided tint
    lighting.rgb = mix(lighting.rgb, tintColor.rgb, tintColor.a);

    return mix(content.eval(fragCoord), lighting, transition);
  }
""".trimIndent()

@Language("AGSL")
internal val LiquidShaderV2 = """
  uniform shader content;
  uniform float4 effectRect;
  uniform float cornerRadius;
  uniform float refraction;
  uniform float curve;
  uniform float sharp;

  const float HALF_PI = 1.57079633;

  float shape(in float2 p, in float2 b, in half4 r) {
    r.xy = (p.x > 0.0) ? r.xy : r.zw;
    r.x = (p.y > 0.0) ? r.x : r.y;
    float2 q = abs(p) - b + r.x;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r.x;
  }

  float2 sdfNormal(float2 pos, float2 shapeSize, half4 vr) {
    float eps = 0.001;
    float sdfX1 = shape(pos + float2(eps, 0.0), shapeSize, vr);
    float sdfX2 = shape(pos - float2(eps, 0.0), shapeSize, vr);
    float sdfY1 = shape(pos + float2(0.0, eps), shapeSize, vr);
    float sdfY2 = shape(pos - float2(0.0, eps), shapeSize, vr);
    return normalize(float2(sdfX1 - sdfX2, sdfY1 - sdfY2));
  }

  half4 main(float2 fragCoord) {
    float2 liquidSize = effectRect.zw - effectRect.xy;
    float2 localCoord = fragCoord - effectRect.xy;

    float aspectRatio = liquidSize.x / liquidSize.y;

    float2 uv = localCoord / liquidSize;
    float2 m2 = uv - 0.5;

    float2 shapeCoord = float2(m2.x * aspectRatio, m2.y);
    float2 shapeSize = float2(0.5 * aspectRatio, 0.5);

    float radius = cornerRadius / liquidSize.y;
    // Eventually I'll add support for four different corner radii.
    half4 vr = half4(radius);
    float sdf = shape(shapeCoord, shapeSize, vr);

    float liquidMask = saturate(-sdf * (max(liquidSize.x, liquidSize.y) * 0.5));
    float transition = smoothstep(0.0, 1.0, liquidMask);

    // The lens is most responsible for the overall effect.
    // Sine creates a smooth curve from 0 to 1 as input goes 0 (sin(0) = 0) to π/2 (sin(π/2) = 1).
    // Credit to ShaderToy user [4eckme](https://www.shadertoy.com/user/4eckme) for this lens formula.
    // https://www.shadertoy.com/view/wcKSRD
    float2 lens = m2 * sin(pow(saturate(-sdf / refraction), curve) * HALF_PI) + 0.5;
    float2 lensCoord = effectRect.xy + lens * liquidSize;

    half4 fragColor = content.eval(lensCoord);
    float edgeSmooth = smoothstep(-sharp, 0.0, sdf);

    // Will also likely make this a customizable uniform.
    float2 lightDirection = float2(-0.3, -0.3);
    float2 sdfNormal = sdfNormal(shapeCoord, shapeSize, vr);

    float nDotL = dot(sdfNormal, normalize(lightDirection));
    float edgeLighting = edgeSmooth * saturate(nDotL) * 0.2;
    float edgeLightingBottom = edgeSmooth * saturate(-nDotL) * 0.2;

    half4 lighting = fragColor;
    lighting.rgb += edgeLighting;
    lighting.rgb += edgeLightingBottom;

    return mix(content.eval(fragCoord), lighting, transition);
  }
""".trimIndent()
