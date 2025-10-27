// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.popup

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.navigation.NavController
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.common.SliderScaffold
import io.github.fletchmckee.liquid.samples.app.nodes.testTagsAsResourceId
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialFrost
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.thenIf
import liquid_root.samples.composeapp.generated.resources.Res
import liquid_root.samples.composeapp.generated.resources.northern_lights
import org.jetbrains.compose.resources.painterResource

@Composable
fun LiquidPopupScreen(
  navController: NavController,
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
) {
  val initialFrost = LocalInitialFrost.current
  val useLiquid = LocalUseLiquid.current
  var showPopup by rememberSaveable { mutableStateOf(false) }
  var frost by rememberSaveable { mutableFloatStateOf(initialFrost) }

  SliderScaffold(
    navController = navController,
    frostProvider = { frost },
    onFrostChange = { frost = it },
    modifier = modifier,
  ) {
    NorthernLightsBackground(
      liquidState = liquidState,
      useLiquid = useLiquid,
      onClick = { showPopup = !showPopup },
      shouldBlur = showPopup,
    )

    LiquidPopup(
      showPopup = showPopup,
      liquidState = liquidState,
      useLiquid = useLiquid,
      frostProvider = { frost },
      onDismissRequest = { showPopup = false },
    )
  }
}

@Composable
private fun NorthernLightsBackground(
  liquidState: LiquidState,
  useLiquid: Boolean,
  onClick: () -> Unit,
  shouldBlur: Boolean,
) {
  val animatedBlur: Float by animateFloatAsState(
    targetValue = if (shouldBlur) 10f else 0f,
    animationSpec = tween(500),
    label = "blur",
  )

  Image(
    painter = painterResource(Res.drawable.northern_lights),
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = Modifier
      .fillMaxSize()
      .thenIf(useLiquid) {
        liquefiable(liquidState)
      }
      .blur(animatedBlur.dp)
      .clickable(
        onClick = onClick,
      )
      .testTag("nyCityBackground")
      .testTagsAsResourceId(true),
  )
}

@Composable
private fun LiquidPopup(
  showPopup: Boolean,
  liquidState: LiquidState,
  useLiquid: Boolean,
  frostProvider: () -> Float,
  onDismissRequest: () -> Unit,
  popupShape: Shape = RoundedCornerShape(15),
  containerColor: Color = MaterialTheme.colorScheme.surface,
) {
//  if (showPopup) {
//    BackHandler { onDismissRequest() }
//  }

  Popup(
    popupPositionProvider = rememberPopupPositionProvider(),
    onDismissRequest = onDismissRequest,
  ) {
    Box(
      modifier = Modifier
        .clip(popupShape)
        .thenIf(useLiquid) {
          liquid(liquidState) {
            frost = frostProvider().dp
            refraction = 0.15f
            curve = 0.3f
            shape = popupShape
            edge = 0.1f
            tint = containerColor
          }
        },
    ) {
      Text(
        text = "Hello",
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.headlineLarge.merge(
          fontSize = 50.sp,
          fontWeight = FontWeight.Bold,
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.align(Alignment.Center),
      )
    }
  }
}

@Composable
internal fun rememberPopupPositionProvider() = remember {
  object : PopupPositionProvider {
    override fun calculatePosition(
      anchorBounds: IntRect,
      windowSize: IntSize,
      layoutDirection: LayoutDirection,
      popupContentSize: IntSize,
    ): IntOffset = IntOffset(
      x = anchorBounds.left + anchorBounds.width / 2,
      y = anchorBounds.top + anchorBounds.height * 2,
    )
  }
}
