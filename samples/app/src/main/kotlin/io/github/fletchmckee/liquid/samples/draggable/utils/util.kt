package io.github.fletchmckee.liquid.samples.draggable.utils

import androidx.compose.ui.Modifier

fun Modifier.thenIf(condition: Boolean, block: Modifier.() -> Modifier): Modifier {
  return if (condition) this.block() else this
}
