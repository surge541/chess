package me.surge.common.chess.operators

import me.surge.common.chess.Board
import me.surge.common.chess.Cell
import me.surge.common.chess.Move
import me.surge.common.chess.Side
import me.surge.common.chess.operators.KingOperator.removeMarkedMoves

object RookOperator : Operator {

    override fun collectTiles(cell: Cell, board: Board, side: Side, removeMarked: Boolean): List<Move> {
        val moves = mutableListOf<Move>()

        // up
        var y = cell.y - 1

        while (y >= 0) {
            val selected = board.find(cell.x, y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            moves.add(Move(side, cell, selected))

            // we've 'met' an opponent's piece, we *can* take it, but we can't progress further
            if (selected.piece.second != side && selected.piece.second != Side.EITHER) {
                break
            }

            y--
        }

        // down
        y = cell.y + 1

        while (y < 8) {
            val selected = board.find(cell.x, y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            moves.add(Move(side, cell, selected))

            // we've 'met' an opponent's piece, we *can* take it, but we can't progress further
            if (selected.piece.second != side && selected.piece.second != Side.EITHER) {
                break
            }

            y += 1
        }

        // left
        var x = cell.x - 1

        while (x >= 0) {
            val selected = board.find(x, cell.y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            moves.add(Move(side, cell, selected))

            // we've 'met' an opponent's piece, we *can* take it, but we can't progress further
            if (selected.piece.second != side && selected.piece.second != Side.EITHER) {
                break
            }

            x -= 1
        }

        // right
        x = cell.x + 1

        while (x < 8) {
            val selected = board.find(x, cell.y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            moves.add(Move(side, cell, selected))

            // we've 'met' an opponent's piece, we *can* take it, but we can't progress further
            if (selected.piece.second != side && selected.piece.second != Side.EITHER) {
                break
            }

            x += 1
        }

        if (removeMarked) {
            removeMarkedMoves(moves, board, side)
        }

        return moves
    }

}