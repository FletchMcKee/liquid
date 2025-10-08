// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.popup

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.R
import io.github.fletchmckee.liquid.samples.app.theme.LocalInitialFrost
import io.github.fletchmckee.liquid.samples.app.theme.LocalUseLiquid
import io.github.fletchmckee.liquid.samples.app.utils.thenIf

@Composable
fun LiquidPopupScreen(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
) = Box(modifier) {
  val useLiquid = LocalUseLiquid.current
  val initialFrost = LocalInitialFrost.current

  var showPopup by rememberSaveable { mutableStateOf(false) }
  NyCityBackground(
    liquidState = liquidState,
    useLiquid = useLiquid,
    onClick = { showPopup = !showPopup },
  )

  // You could use Android's `.blur` modifier in NyCityBackground instead,
  // but the frost looks better IMO.
  BlurBackground(
    liquidState = liquidState,
    shouldBlur = showPopup,
    useLiquid = useLiquid,
    initialFrost = initialFrost,
  )

  LiquidPopup(
    showPopup = showPopup,
    liquidState = liquidState,
    useLiquid = useLiquid,
    initialFrost = initialFrost,
    onDismissRequest = { showPopup = false },
  )
}

@Composable
private fun NyCityBackground(
  liquidState: LiquidState,
  useLiquid: Boolean,
  onClick: () -> Unit,
) = Image(
  painter = painterResource(R.drawable.ny_city),
  contentDescription = null,
  contentScale = ContentScale.Crop,
  modifier = Modifier
    .fillMaxSize()
    .thenIf(useLiquid) {
      liquefiable(liquidState)
    }
    .clickable(
      onClick = onClick,
    )
    .testTag("nyCityBackground")
    .semantics { testTagsAsResourceId = true },
)

@Composable
private fun BlurBackground(
  liquidState: LiquidState,
  shouldBlur: Boolean,
  useLiquid: Boolean,
  initialFrost: Float,
) {
  val animatedBlur: Float by animateFloatAsState(
    targetValue = if (shouldBlur) initialFrost else 0f,
    animationSpec = tween(500),
    label = "blur",
  )

  Box(
    Modifier
      .fillMaxSize()
      .thenIf(useLiquid) {
        liquid(liquidState) {
          refraction = 0f // Not adding liquid effect, just the frost
          frost = animatedBlur.dp
          shape = RectangleShape
        }
      },
  )
}

@Composable
private fun LiquidPopup(
  showPopup: Boolean,
  liquidState: LiquidState,
  useLiquid: Boolean,
  initialFrost: Float,
  onDismissRequest: () -> Unit,
  popupShape: Shape = RoundedCornerShape(15),
  containerColor: Color = MaterialTheme.colorScheme.surface,
) {
  if (showPopup) {
    BackHandler { onDismissRequest() }
  }

  Popup(
    onDismissRequest = onDismissRequest,
  ) {
    AnimatedVisibility(
      visible = showPopup,
      enter = scaleIn(
        initialScale = 0.8f,
        transformOrigin = TransformOrigin.Center,
        animationSpec = spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessLow,
        ),
      ) + fadeIn(),
      exit = scaleOut(
        targetScale = 0.8f,
        animationSpec = tween(300),
      ) + fadeOut(animationSpec = tween(300)),
      modifier = Modifier.fillMaxSize(),
    ) {
      BoxWithConstraints(
        modifier = Modifier
          .fillMaxSize()
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onDismissRequest,
          ),
        contentAlignment = Alignment.Center,
      ) {
        val maxSquareSize = min(maxWidth, maxHeight) - 48.dp // 24.dp padding on each side

        Box(
          modifier = Modifier
            .size(maxSquareSize)
            .clip(popupShape)
            .thenIf(useLiquid) {
              liquid(liquidState) {
                frost = initialFrost.dp
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
  }
}
