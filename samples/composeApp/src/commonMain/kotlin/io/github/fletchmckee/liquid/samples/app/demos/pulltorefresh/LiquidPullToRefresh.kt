// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.pulltorefresh

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.common.ShaderBackground
import io.github.fletchmckee.liquid.samples.app.common.SliderScaffold
import io.github.fletchmckee.liquid.samples.app.demos.many.LoremIpsum
import kotlin.math.abs
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

@Composable
fun LiquidPullToRefresh(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
  refreshState: PullToRefreshState = rememberPullToRefreshState(),
  navController: NavController = rememberNavController(),
) = SliderScaffold(
  navController = navController,
  modifier = modifier,
) {
  var cacheKey by rememberSaveable { mutableIntStateOf(abs(Random.nextInt())) }
  var isRefreshing by rememberSaveable { mutableStateOf(false) }

  LaunchedEffect(isRefreshing) {
    if (isRefreshing) {
      // Give an artificial delay so that we always display two full rotations since this
      // is what we're demoing.
      delay(4.5.seconds)
      cacheKey = abs(Random.nextInt())
    }
  }

  PullToRefreshBox(
    state = refreshState,
    isRefreshing = isRefreshing,
    onRefresh = { isRefreshing = true },
    modifier = Modifier.fillMaxSize(),
    indicator = {
      LiquidRefreshIndicator(
        state = refreshState,
        liquidState = liquidState,
        isRefreshing = isRefreshing,
        modifier = Modifier.align(Alignment.TopCenter),
      ) {
        Icon(
          imageVector = Icons.Default.Refresh,
          contentDescription = null,
          modifier = Modifier.size(80.dp),
          tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
        )
      }
    },
  ) {
    ShaderBackground(liquidState)

    PicsumList(
      liquidState = liquidState,
      onComplete = { isRefreshing = false },
      cacheKey = cacheKey,
    )
  }
}

@Composable
private fun PicsumList(
  liquidState: LiquidState,
  onComplete: () -> Unit,
  cacheKey: Int,
) = LazyColumn(
  modifier = Modifier
    .fillMaxSize()
    .clipToBounds()
    .liquefiable(liquidState),
  contentPadding = WindowInsets.systemBars.asPaddingValues(),
  overscrollEffect = null,
  horizontalAlignment = Alignment.CenterHorizontally,
) {
  items(
    count = 50,
    key = { it },
    contentType = { "picsumIpsum" },
  ) { index ->
    PicsumIpsumCard(
      // Only signal onComplete for the first card since this is the one the
      // refresh indicator will animate over.
      onComplete = if (index == 0) onComplete else { -> },
      cacheKey = index + cacheKey,
    )
  }
}

@Composable
private fun PicsumIpsumCard(
  onComplete: () -> Unit,
  cacheKey: Int,
  containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
) = Column(
  modifier = Modifier.fillMaxWidth(),
  horizontalAlignment = Alignment.CenterHorizontally,
) {
  PicsumImage(
    cacheKey = cacheKey,
    onComplete = onComplete,
  )

  Text(
    text = LoremIpsum,
    color = MaterialTheme.colorScheme.onSurfaceVariant,
    style = MaterialTheme.typography.bodyLarge,
    maxLines = 5,
    overflow = TextOverflow.Ellipsis,
    modifier = Modifier
      .background(containerColor)
      .padding(16.dp),
  )
}

@Composable
private fun PicsumImage(
  cacheKey: Int,
  onComplete: () -> Unit,
) {
  // Hack so that we don't see loading screens between refreshing images.
  // Otherwise the LiquidRefreshIndicator animates over a gray screen which defeats the purpose.
  var lastSuccessfulPainter by remember { mutableStateOf<Painter?>(null) }
  val painter = rememberAsyncImagePainter(
    model = "https://picsum.photos/600?random=$cacheKey",
    onSuccess = {
      lastSuccessfulPainter = it.painter
      onComplete()
    },
    onError = {
      lastSuccessfulPainter = ColorPainter(Color.Magenta)
      onComplete()
    },
    placeholder = lastSuccessfulPainter ?: ColorPainter(Color.LightGray),
  )

  Image(
    painter = painter,
    contentDescription = null,
    contentScale = ContentScale.Crop,
    modifier = Modifier
      .widthIn(max = 600.dp)
      .aspectRatio(1f),
  )
}

@Composable
private fun LiquidRefreshIndicator(
  state: PullToRefreshState,
  liquidState: LiquidState,
  isRefreshing: Boolean,
  modifier: Modifier = Modifier,
  indicatorShape: Shape = RoundedCornerShape(30),
  indicatorColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.1f),
  indicatorSize: Dp = DefaultIndicatorSize,
  threshold: Dp = DefaultThreshold,
  content: @Composable () -> Unit,
) {
  val infiniteTransition = rememberInfiniteTransition(label = "refresh")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = if (isRefreshing) 720f else 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000),
      repeatMode = RepeatMode.Restart,
      initialStartOffset = StartOffset(500),
    ),
    label = "rotation",
  )

  val scale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isRefreshing) 1.5f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000),
      repeatMode = RepeatMode.Reverse,
      initialStartOffset = StartOffset(500),
    ),
    label = "scale",
  )

  Box(
    modifier = modifier
      .size(indicatorSize)
      .drawWithContent {
        clipRect(
          top = 0f,
          left = -Float.MAX_VALUE,
          right = Float.MAX_VALUE,
          bottom = Float.MAX_VALUE,
        ) {
          this@drawWithContent.drawContent()
        }
      }
      .layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
          placeable.placeWithLayer(
            x = 0,
            y = 0,
            layerBlock = {
              translationY = state.distanceFraction * threshold.roundToPx() - size.height
              shadowElevation = 6.dp.toPx()
              shape = indicatorShape
              rotationZ = rotation
              scaleX = scale
              scaleY = scale
            },
          )
        }
      }
      .liquid(liquidState) {
        frost = 2.dp
        shape = indicatorShape
        // Generally the best combos are when refraction * curve <= cornerPercent².
        curve = 0.3f
        refraction = 0.3f
        edge = 0.05f
        dispersion = 0.02f
        saturation = 1.5f
        tint = indicatorColor
      },
    contentAlignment = Alignment.Center,
  ) {
    content()
  }
}

private val DefaultIndicatorSize = 150.dp
private val DefaultThreshold = 270.dp
