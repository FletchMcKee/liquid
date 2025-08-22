// Copyright 2024, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.internal

import kotlin.jvm.JvmInline

// This is directly from the Haze library.
@JvmInline
internal value class Bitmask(private val value: Int = 0) {
  operator fun plus(flag: Int): Bitmask = Bitmask(value or flag)
  operator fun minus(flag: Int): Bitmask = Bitmask(value and flag.inv())
  operator fun contains(flag: Int): Boolean = (flag and value) == flag
  fun any(flag: Int): Boolean = (flag and value) != 0
  fun isEmpty(): Boolean = value == 0
}

internal object Fields {
  const val Frost: Int = 0b1
  const val Shape: Int = 0b1 shl 1
  const val Refraction: Int = 0b1 shl 2
  const val Curve: Int = 0b1 shl 3
  const val Edge: Int = 0b1 shl 4

  const val Liquefiables: Int = 0b1 shl 5
  const val Bounds: Int = 0b1 shl 6

  const val RenderEffectFields: Int = Frost or Shape or Refraction or Curve or Edge
}
