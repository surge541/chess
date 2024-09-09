package me.surge.util

enum class Button(val code: Int) {
    LEFT(0),
    RIGHT(1),
    MIDDLE(2),
    SIDE_TOP(3),
    SIDE_BOTTOM(4),
    UNKNOWN(-1);

    companion object {

        fun from(code: Int): Button {
            return entries.firstOrNull { it.code == code } ?: UNKNOWN
        }

    }
}