package me.surge.common.chess.operators

import me.surge.common.chess.*
import me.surge.common.chess.Side.Companion.opposite
import me.surge.common.chess.operators.KingOperator.removeMarkedCells

object PawnOperator : Operator {

    override fun collectTiles(cell: Cell, board: Board, side: Side, removeMarked: Boolean): List<Cell> {
        // if white, then we are going upwards, so decreasing Y, else we are increasing it
        val increment = if (cell.piece.second == Side.WHITE) -1 else 1

        val cells = mutableListOf<Cell>()

        val offsetA = board.find(cell.x, cell.y + increment)

        if (offsetA.piece.first == Piece.EMPTY) {
            // add cell in front of pawn
            cells.add(offsetA)

            // starting pos
            if (!cell.moved) {
                val offsetB = offsetA.offset(0, increment, board)

                if (offsetB.piece.first == Piece.EMPTY) {
                    cells.add(offsetB)
                }
            }
        }

        if (cell.x > 0) {
            val leftTake = board.find(cell.x - 1, cell.y + increment)

            if (leftTake.piece.first != Piece.EMPTY && leftTake.piece.second != cell.piece.second) {
                cells.add(leftTake)
            }
        }

        if (cell.x < 7) {
            val rightTake = board.find(cell.x + 1, cell.y + increment)

            if (rightTake.piece.first != Piece.EMPTY && rightTake.piece.second != cell.piece.second) {
                cells.add(rightTake)
            }
        }

        val left = board.findNullable(cell.x - 1, cell.y)

        if (left != null && left.piece.first == Piece.PAWN && left.piece.second == side.opposite) {
            println("Left Tag: ${left.tag}")
            if (left.tag == Cell.Tag.DOUBLE_MOVE) {
                cells.add(left.offset(0, if (left.piece.second == Side.WHITE) 1 else -1, board).also {
                    it.claimedCell = left.x to left.y
                })
            }
        }

        if (removeMarked) {
            removeMarkedCells(cell, cells, board, side)
        }

        return cells
    }

}