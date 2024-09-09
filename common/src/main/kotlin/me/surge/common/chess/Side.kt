package me.surge.common.chess

enum class Side {

    WHITE,
    BLACK,
    EITHER;

    companion object {

        val Side.opposite: Side
            get() = if (this == WHITE) BLACK else WHITE

    }

}