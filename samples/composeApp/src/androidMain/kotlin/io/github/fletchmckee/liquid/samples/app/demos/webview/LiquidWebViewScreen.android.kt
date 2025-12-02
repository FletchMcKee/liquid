// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.webview

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.utils.blendMode
import io.github.fletchmckee.liquid.samples.app.utils.rememberShaderBrush

@Composable
fun LiquidWebViewScreen(
  modifier: Modifier = Modifier,
  url: String = "https://www.google.com",
  liquidState: LiquidState = rememberLiquidState(),
) = Box(modifier) {
  LiquidWebView(url, liquidState)

  BackgroundScrim(
    liquidState = liquidState,
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(150.dp)
      .align(Alignment.BottomCenter),
  )

  LiquidTextField(
    liquidState = liquidState,
    modifier = Modifier
      .fillMaxWidth()
      .align(Alignment.BottomCenter)
      .systemBarsPadding()
      .padding(24.dp),
  )
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun LiquidWebView(
  url: String,
  liquidState: LiquidState,
) = AndroidView(
  factory = { context ->
    WebView(context).apply {
      layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
      )
      setLayerType(View.LAYER_TYPE_HARDWARE, null)
      webViewClient = WebViewClient()
      settings.javaScriptEnabled = true
      loadUrl(url)
    }
  },
  modifier = Modifier
    .fillMaxSize()
    .padding(
      WindowInsets.systemBars
        .only(WindowInsetsSides.Top)
        .asPaddingValues(),
    )
    .liquefiable(liquidState),
)

@Composable
private fun BackgroundScrim(
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
  shader: ShaderBrush = rememberShaderBrush(
    listOf(
      Color.Transparent,
      MaterialTheme.colorScheme.background.copy(alpha = 0.5f),
    ),
  ),
) = Box(
  modifier
    .liquefiable(liquidState)
    .background(shader),
)

@Composable
private fun LiquidTextField(
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
  textFieldState: TextFieldState = rememberTextFieldState(),
) = TextField(
  state = textFieldState,
  label = {
    Text(
      text = "Search...",
      color = Color.White,
      fontSize = 20.sp,
      modifier = Modifier.blendMode(BlendMode.Difference).padding(1.dp),
    )
  },
  // Way too opinionated.
  colors = TextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    errorContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    errorIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
  ),
  shape = CircleShape,
  modifier = modifier
    .dropShadow(CircleShape, Shadow(radius = 4.dp, Color.Black.copy(alpha = 0.3f)))
    .liquid(liquidState) {
      frost = 2.dp
      // Apple appears to alter their lens effect based on shape/component. This is somewhat close
      // for the TextField `glassEffect(.clear)`.
      refraction = 0.4f
      curve = 0.9f
      edge = 0.05f
      contrast = 1.5f
      tint = Color.White.copy(alpha = 0.4f)
    },
)
