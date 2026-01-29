// Copyright 2025, Colin McKee
// SPDX-License-Identifier: Apache-2.0
package io.github.fletchmckee.buildlogic

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

internal fun Project.configureTesting() {
  tasks.withType(Test::class.java).configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

    failOnNoDiscoveredTests.set(false)
    reports.html.required.set(false)
    reports.junitXml.required.set(false)

    testLogging {
      exceptionFormat = TestExceptionFormat.FULL
      events(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
    }
  }
}
