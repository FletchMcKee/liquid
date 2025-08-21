// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.liquid.samples.draggable.utils

import androidx.compose.ui.Modifier

fun Modifier.thenIf(condition: Boolean, block: Modifier.() -> Modifier): Modifier = if (condition) this.block() else this
