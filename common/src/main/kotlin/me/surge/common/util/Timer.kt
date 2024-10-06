package me.surge.common.util

class Timer {

    private val time: Long
        get() = System.currentTimeMillis() - lastMillis

    private var lastMillis = -1L

    /**
     * Whether we have passed the given [duration] in milliseconds
     *
     * @param duration in milliseconds
     * @param reset if to reset the timer after checking, if passed
     */
    fun passed(duration: Long, reset: Boolean = true): Boolean {
        if (time > duration) {
            if (reset) {
                reset()
            }

            return true
        }

        return false
    }

    /**
     * Resets the timer to 0ms
     */
    private fun reset() {
        lastMillis = System.currentTimeMillis()
    }

}