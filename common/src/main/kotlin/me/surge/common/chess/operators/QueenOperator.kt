package me.surge.common.chess.operators

import me.surge.common.chess.Board
import me.surge.common.chess.Cell
import me.surge.common.chess.Move
import me.surge.common.chess.Side
import me.surge.common.chess.operators.KingOperator.removeMarkedMoves

object QueenOperator : Operator {

    override fun collectTiles(cell: Cell, board: Board, side: Side, removeMarked: Boolean): List<Move> {
        val moves = mutableListOf<Move>()

        moves.addAll(RookOperator.collectTiles(cell, board, side))
        moves.addAll(BishopOperator.collectTiles(cell, board, side))

        if (removeMarked) {
            removeMarkedMoves(moves, board, side)
        }

        return moves
    }

}