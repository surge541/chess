package me.surge.common.chess.operators

import me.surge.common.chess.Board
import me.surge.common.chess.Cell
import me.surge.common.chess.Move
import me.surge.common.chess.Side
import me.surge.common.chess.operators.KingOperator.removeMarkedMoves

object KnightOperator : Operator {

    //  x    y
    @JvmStatic val offsets = listOf(
        1 to -2,
        2 to -1,

        2 to 1,
        1 to 2,

        -1 to -2,
        -2 to -1,

        -2 to 1,
        -1 to 2
    )

    override fun collectTiles(cell: Cell, board: Board, side: Side, removeMarked: Boolean): List<Move> {
        val moves = mutableListOf<Move>()

        offsets.forEach { (x, y) ->
            val selected = board.findNullable(cell.x + x, cell.y + y)

            if (selected != null) {
                // one of our pieces, we can't take
                if (selected.piece.second == side) {
                    return@forEach
                }

                moves.add(Move(side, cell, selected))
            }
        }

        if (removeMarked) {
            removeMarkedMoves(moves, board, side)
        }

        return moves
    }

}