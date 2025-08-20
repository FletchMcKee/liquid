package io.github.fletchmckee.liquid.samples.draggable.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = Purple40,
  secondary = PurpleGrey40,
  tertiary = PurpleGrey80,
  surfaceVariant = Purple80,
  background = Color.DarkGray,
  onBackground = Color.White,
  onSurfaceVariant = Color(0xFF1C1B1F),
)

private val LightColorScheme = lightColorScheme(
  primary = Purple40,
  secondary = PurpleGrey40,
  tertiary = Pink40,
  background = Color(0x80FFFFFF),
  surface = Color(0xFFFFFBFE),
  onPrimary = Color.White,
  onSecondary = Color.White,
  onTertiary = Color.White,
  onBackground = Color(0xFF1C1B1F),
  onSurface = Color(0xFF1C1B1F),
)

@Composable
fun DraggableTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content,
  )
}
