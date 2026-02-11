// Copyright 2026, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.rememberPicsumPainter
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import liquid_root.samples.shared.generated.resources.Res
import liquid_root.samples.shared.generated.resources.dotonbori
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun PicsumBackground(
  liquidState: LiquidState,
  cacheKey: Int,
  modifier: Modifier = Modifier,
  useLiquid: Boolean = LocalUseLiquid.current,
  defaultDrawable: DrawableResource = Res.drawable.dotonbori,
  contentScale: ContentScale = ContentScale.Crop,
) {
  val defaultPainter = painterResource(defaultDrawable)
  val painter = rememberPicsumPainter(
    cacheKey = cacheKey,
    defaultPainter = defaultPainter,
    error = defaultPainter,
  )

  Image(
    painter = painter,
    contentDescription = null,
    contentScale = contentScale,
    modifier = modifier
      .fillMaxSize()
      .thenIf(useLiquid) {
        liquefiable(liquidState)
      }
      .background(Color.LightGray),
  )
}
