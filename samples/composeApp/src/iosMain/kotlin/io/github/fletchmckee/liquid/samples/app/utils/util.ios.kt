// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.utils

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle

actual fun formatFloat(value: Float, format: String): String {
  val formatter = NSNumberFormatter()
  when (format) {
    "%,.0f" -> {
      formatter.numberStyle = NSNumberFormatterDecimalStyle
      formatter.maximumFractionDigits = 0u
    }
    "%,.2f" -> {
      formatter.numberStyle = NSNumberFormatterDecimalStyle
      formatter.minimumFractionDigits = 2u
      formatter.maximumFractionDigits = 2u
    }
  }
  return formatter.stringFromNumber(NSNumber(value)) ?: value.toString()
}
