package io.github.fletchmckee.liquid.samples.app.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.fletchmckee.liquid.LiquidState
import io.github.fletchmckee.liquid.liquid

/**
 * Glass pill container for bottom bars.
 *
 * Usage:
 * ```
 * val liquidState = rememberLiquidState()
 * LiquidBottomAppBar(liquidState) {
 *     // Row content goes here
 * }
 * ```
 */
@Composable
fun LiquidBottomAppBar(
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
  bottomBarShape: Shape = CircleShape,
  containerColor: Color = Color.White.copy(alpha = 0.94f),
  // Optional: draw a simple solid background behind the liquid effect (useful for camera preview).
  solidBackgroundColor: Color? = null,
  frost: Dp = 32.dp,
  content: @Composable () -> Unit = {},
) = Row(
  modifier = modifier
    .widthIn(max = 640.dp)
    .padding(24.dp)
    .clip(bottomBarShape)
    .then(
      if (solidBackgroundColor != null) Modifier.background(solidBackgroundColor) else Modifier,
    )
    .liquid(liquidState) {
      // All "Liquid" styling applied directly
      this@liquid.frost = frost
      refraction = 0.25f
      curve = 0.5f
      shape = bottomBarShape
      edge = 0.1f
      tint = containerColor
      saturation = 1.2f
      contrast = 1.15f
      dispersion = 0.05f
    },
) {
  content()
}

/**
 * Data class representing a single tab item in the liquid bottom navigation bar.
 */
data class LiquidTabItemData(
  val label: String,
  val icon: @Composable (tint: Color) -> Unit,
  val onClick: () -> Unit,
)

/**
 * Individual tab item composable with liquid glass effect when selected.
 * Fixed height prevents layout jumps on selection.
 *
 * Usage:
 * ```
 * LiquidTabItem(
 *     selected = index == selectedIndex,
 *     label = "Camera",
 *     icon = { tint -> Icon(Icons.Filled.PhotoCamera, null, tint = tint) },
 *     onClick = { /* navigate */ },
 *     liquidState = liquidState
 * )
 * ```
 */
@Composable
fun LiquidTabItem(
  selected: Boolean,
  label: String,
  icon: @Composable (tint: Color) -> Unit,
  onClick: () -> Unit,
  liquidState: LiquidState,
  selectedColor: Color = MaterialTheme.colorScheme.primary,
  unselectedColor: Color = Color(0x99000000),
  modifier: Modifier = Modifier,
) {
  // No ripple / hover highlight
  val interactionSource = remember { MutableInteractionSource() }

  // Keep ALL items the same size so selection never "jumps"
  // Match Material/iOS guidance: 56dp height, 24dp icons, 12sp labels.
  val itemHeight = 56.dp
  val iconSize = 24.dp
  val shape = RoundedCornerShape(999.dp)
  val tint = if (selected) selectedColor else unselectedColor

  Box(
    modifier = modifier
      .height(itemHeight)
      .fillMaxWidth()
      // Less outer padding so the chip can be visually larger
      .padding(horizontal = 0.dp, vertical = 4.dp)
      .clip(shape)
      .then(
        if (selected) {
          Modifier.liquid(liquidState) {
            // Softer highlight that stays readable on white backgrounds.
            frost = 12.dp
            refraction = 0.22f
            curve = 0.8f
            edge = 0.00f
            this@liquid.shape = shape
            // Keep the lens neutral so it doesn't borrow the icon tint (avoids blue wash).
            this@liquid.tint = Color(0xFFe5e6ea).copy(alpha = 0.7f)
            saturation = 1.2f
            contrast = 1.15f
            dispersion = 0.05f
          }
        } else Modifier,
      )
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick,
      ),
    contentAlignment = Alignment.Center,
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Box(
        modifier = Modifier.size(iconSize),
        contentAlignment = Alignment.Center,
      ) {
        icon(tint)
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
        color = tint,
        textAlign = TextAlign.Center,
      )
    }
  }
}

/**
 * Complete bottom bar that renders tabs and applies the glass effect.
 *
 * Usage:
 * ```
 * val liquidState = rememberLiquidState()
 * val tabs = listOf(
 *     LiquidTabItemData("Camera", icon = { tint -> Icon(Icons.Filled.PhotoCamera, null, tint) }) { /* ... */ },
 *     LiquidTabItemData("Library", icon = { tint -> Icon(Icons.Filled.Folder, null, tint) }) { /* ... */ }
 * )
 * LiquidBottomNavigationBar(
 *     tabs = tabs,
 *     selectedIndex = selectedIndex,
 *     liquidState = liquidState
 * )
 * ```
 */
@Composable
fun LiquidBottomNavigationBar(
  tabs: List<LiquidTabItemData>,
  selectedIndex: Int,
  liquidState: LiquidState,
  modifier: Modifier = Modifier,
  // Default: slightly translucent. Callers can override (e.g. Camera tab uses solid white).
  containerColor: Color = Color.White.copy(alpha = 0.75f),
  solidBackgroundColor: Color? = null,
  barFrost: Dp = 48.dp,
  selectedColor: Color = MaterialTheme.colorScheme.primary,
  unselectedColor: Color = Color(0x99000000),
) {
  LiquidBottomAppBar(
    liquidState = liquidState,
    containerColor = containerColor,
    solidBackgroundColor = solidBackgroundColor,
    frost = barFrost,
    modifier = modifier.fillMaxWidth(),
  ) {
    // We use a Row here to distribute the items evenly inside the Liquid bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        // Small inset so first/last items don't hug the pill edges.
        .padding(horizontal = 6.dp, vertical = 0.5.dp),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      tabs.forEachIndexed { index, tab ->
        Box(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 0.dp),
        ) {
          LiquidTabItem(
            selected = index == selectedIndex,
            label = tab.label,
            icon = tab.icon,
            onClick = tab.onClick,
            liquidState = liquidState,
            selectedColor = selectedColor,
            unselectedColor = unselectedColor,
          )
        }
      }
    }
  }
}
