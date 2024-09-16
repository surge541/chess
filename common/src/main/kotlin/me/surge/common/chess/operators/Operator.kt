package me.surge.common.chess.operators

import me.surge.common.chess.Board
import me.surge.common.chess.Cell
import me.surge.common.chess.Move
import me.surge.common.chess.Side

import me.surge.common.chess.Piece.*

interface Operator {

    fun collectTiles(cell: Cell, board: Board, side: Side, removeMarked: Boolean = true): List<Move>

    companion object {

        fun getOperator(cell: Cell, side: Side): Operator? {
            if (cell.piece.second != side) {
                return null
            }

            return when (cell.piece.first) {
                PAWN -> PawnOperator
                ROOK -> RookOperator
                KNIGHT -> KnightOperator
                BISHOP -> BishopOperator
                QUEEN -> QueenOperator
                KING -> KingOperator

                else -> null
            }
        }

    }

}