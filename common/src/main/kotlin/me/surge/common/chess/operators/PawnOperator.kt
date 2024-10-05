package me.surge.common.chess.operators

import me.surge.common.chess.*
import me.surge.common.chess.Side.Companion.opposite
import me.surge.common.chess.operators.KingOperator.removeMarkedMoves

object PawnOperator : Operator {

    override fun collectTiles(cell: Cell, board: Board, side: Side, removeMarked: Boolean): List<Move> {
        // if white, then we are going upwards, so decreasing Y, else we are increasing it
        val increment = if (cell.piece.second == Side.WHITE) -1 else 1

        val moves = mutableListOf<Move>()

        val offsetA = board.find(cell.x, cell.y + increment)

        if (offsetA.piece.first == Piece.EMPTY) {
            // add cell in front of pawn
            moves.add(Move(side, cell, offsetA.copy()))

            // starting pos
            if (!cell.moved) {
                val offsetB = offsetA.offset(0, increment, board)

                if (offsetB.piece.first == Piece.EMPTY) {
                    moves.add(Move(side, cell, offsetB.copy()).tag(Move.Tag.DOUBLE_PAWN_MOVE))
                }
            }
        }

        if (cell.x > 0) {
            val leftTake = board.find(cell.x - 1, cell.y + increment)

            if (leftTake.piece.first != Piece.EMPTY && leftTake.piece.second != cell.piece.second) {
                moves.add(Move(side, cell, leftTake.copy()))
            }
        }

        if (cell.x < 7) {
            val rightTake = board.find(cell.x + 1, cell.y + increment)

            if (rightTake.piece.first != Piece.EMPTY && rightTake.piece.second != cell.piece.second) {
                moves.add(Move(side, cell, rightTake.copy()))
            }
        }

        val left = board.findNullable(cell.x - 1, cell.y)

        if (left != null && left.piece.first == Piece.PAWN && left.piece.second == side.opposite && board.moves.isNotEmpty()) {
            val lastMove = board.moves.last()

            // did en-passant
            if (lastMove.to == left && lastMove.tag == Move.Tag.DOUBLE_PAWN_MOVE) {
                moves.add(
                    Move(
                        side,
                        cell,
                        left.offset(0, if (left.piece.second == Side.WHITE) 1 else -1, board)
                    ).claimCell(left)
                )
            }
        }

        val right = board.findNullable(cell.x + 1, cell.y)

        if (right != null && right.piece.first == Piece.PAWN && right.piece.second == side.opposite && board.moves.isNotEmpty()) {
            val lastMove = board.moves.last()

            // did en-passant
            if (lastMove.to == right && lastMove.tag == Move.Tag.DOUBLE_PAWN_MOVE) {
                moves.add(
                    Move(
                        side,
                        cell,
                        right.offset(0, if (right.piece.second == Side.WHITE) 1 else -1, board)
                    ).claimCell(right)
                )
            }
        }

        if (removeMarked) {
            removeMarkedMoves(moves, board, side)
        }

        moves.forEach {
            when (it.from.piece.second) {
                Side.WHITE -> {
                    if (it.to.y == 0) {
                        it.tag(Move.Tag.PAWN_PROMOTION)
                    }
                }

                Side.BLACK -> {
                    if (it.to.y == 7) {
                        it.tag(Move.Tag.PAWN_PROMOTION)
                    }
                }

                else -> {}
            }
        }

        return moves
    }

}