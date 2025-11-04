// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.utils

import kotlin.math.absoluteValue

actual fun formatFloat(value: Float, format: String): String = when (format) {
  "%,.0f" -> value.toInt().toString()
  "%,.2f" -> {
    val intPart = value.toInt()
    val fracPart = ((value - intPart) * 100).toInt().absoluteValue
    "$intPart.${fracPart.toString().padStart(2, '0')}"
  }
  else -> value.toString()
}
