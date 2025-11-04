// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.grid

import android.os.Build
import android.view.RoundedCorner
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView

@Composable
actual fun rememberTopBarShape(): Shape {
  val view = LocalView.current
  val insets = view.rootWindowInsets
  return remember(insets) {
    when {
      Build.VERSION.SDK_INT >= 31 -> {
        RoundedCornerShape(
          topStart = insets.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius?.toFloat() ?: 0f,
          topEnd = insets.getRoundedCorner(RoundedCorner.POSITION_TOP_RIGHT)?.radius?.toFloat() ?: 0f,
          bottomStart = 0f,
          bottomEnd = 0f,
        )
      }
      else -> RectangleShape
    }
  }
}
