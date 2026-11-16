package org.xmlet.htmlflow.datastar.modifiers.core

import kotlin.time.Duration

private const val NANOSECONDS_PER_MILLISECOND = 1_000_000L
private const val MILLISECONDS_PER_SECOND = 1_000L

internal fun Duration.toModifierDuration(): String {
    require(isFinite()) { "Modifier durations must be finite." }
    require(this >= Duration.ZERO) { "Modifier durations must not be negative." }

    val wholeMilliseconds = inWholeMilliseconds
    require(wholeMilliseconds * NANOSECONDS_PER_MILLISECOND == inWholeNanoseconds) {
        "Modifier durations must be expressible as whole milliseconds."
    }

    return if (wholeMilliseconds % MILLISECONDS_PER_SECOND == 0L) {
        "${wholeMilliseconds / MILLISECONDS_PER_SECOND}s"
    } else {
        "${wholeMilliseconds}ms"
    }
}
