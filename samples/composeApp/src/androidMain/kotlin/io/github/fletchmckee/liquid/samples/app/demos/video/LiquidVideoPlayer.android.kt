// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.app.demos.video

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquefiable
import io.github.fletchmckee.liquid.liquid
import io.github.fletchmckee.liquid.rememberLiquidState
import io.github.fletchmckee.liquid.samples.app.R

@Composable
fun VideoPlayer(
  modifier: Modifier = Modifier,
  liquidState: LiquidState = rememberLiquidState(),
) {
  val context = LocalContext.current
  var player by remember { mutableStateOf<Player?>(null) }
  var currentPosition by rememberSaveable { mutableLongStateOf(0) }

  LifecycleStartEffect(Unit) {
    player = ExoPlayer.Builder(context)
      .build()
      .apply {
        setMediaItem(MediaItem.fromUri(BigBuckBunnyUrl))
        seekTo(currentPosition)
        prepare()
      }

    onStopOrDispose {
      currentPosition = player?.currentPosition ?: 0
      player?.apply { release() }
      player = null
    }
  }

  player?.let {
    MediaPlayerScreen(
      player = it,
      liquidState = liquidState,
      modifier = modifier,
    )
  }
}

@OptIn(UnstableApi::class)
@Composable
private fun MediaPlayerScreen(
  player: Player,
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
  contentScale: ContentScale = ContentScale.FillWidth,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
  var showControls by remember { mutableStateOf(true) }
  val presentationState = rememberPresentationState(player)
  val scaledModifier = Modifier.resizeWithContentScale(contentScale, presentationState.videoSizeDp)

  Box(modifier) {
    PlayerSurface(
      player = player,
      surfaceType = SURFACE_TYPE_TEXTURE_VIEW,
      modifier = scaledModifier
        .liquefiable(liquidState)
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          onClick = { showControls = !showControls },
        ),
    )

    if (presentationState.coverSurface) {
      // Cover the surface that is being prepared with a shutter.
      Box(
        Modifier
          .matchParentSize()
          .background(Color.Black),
      )
    }

    LiquidPlayPauseButton(
      player = player,
      liquidState = liquidState,
      showControls = showControls,
      modifier = Modifier.align(Alignment.Center),
    )
  }
}

@Composable
private fun LiquidPlayPauseButton(
  player: Player,
  liquidState: LiquidState,
  showControls: Boolean,
  modifier: Modifier = Modifier,
  buttonColor: Color = MaterialTheme.colorScheme.background.copy(alpha = 0.05f),
) = AnimatedVisibility(
  visible = showControls,
  enter = scaleIn(animationSpec = spring(stiffness = Spring.StiffnessHigh)),
  exit = scaleOut(animationSpec = spring(stiffness = Spring.StiffnessHigh)),
  modifier = modifier,
) {
  var isPlaying by remember { mutableStateOf(player.isPlaying) }

  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .size(120.dp)
      .shadow(4.dp, CircleShape)
      .liquid(liquidState) {
        frost = 2.5.dp
        curve = 0.4f
        edge = 0.02f
        tint = buttonColor
      }
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = {
          when {
            player.isPlaying -> {
              player.pause()
              isPlaying = false
            }

            else -> {
              player.play()
              isPlaying = true
            }
          }
        },
      ),
  ) {
    Icon(
      imageVector = if (isPlaying) ImageVector.vectorResource(R.drawable.pause) else Icons.Filled.PlayArrow,
      contentDescription = "Play button",
      tint = Color.White,
      modifier = Modifier.size(64.dp),
    )
  }
}

private const val BigBuckBunnyUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
