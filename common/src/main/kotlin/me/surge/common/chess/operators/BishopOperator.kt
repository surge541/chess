package me.surge.common.chess.operators

import me.surge.common.chess.Board
import me.surge.common.chess.Cell
import me.surge.common.chess.Move
import me.surge.common.chess.Side
import me.surge.common.chess.operators.KingOperator.removeMarkedMoves

object BishopOperator : Operator {

    override fun collectTiles(cell: Cell, board: Board, side: Side, removeMarked: Boolean): List<Move> {
        val moves = mutableListOf<Move>()

        var x = cell.x - 1
        var y = cell.y - 1

        // top left
        while (x in 0..7 && y >= 0 && y <= 7) {
            val selected = board.find(x, y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            moves.add(Move(side, cell, selected))

            // we've 'met' an opponent's piece, we *can* take it, but we can't progress further
            if (selected.piece.second != side && selected.piece.second != Side.EITHER) {
                break
            }

            x--
            y--
        }

        x = cell.x - 1
        y = cell.y + 1

        // bottom left
        while (x in 0..7 && y >= 0 && y <= 7) {
            val selected = board.find(x, y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            moves.add(Move(side, cell, selected))

            // we've 'met' an opponent's piece, we *can* take it, but we can't progress further
            if (selected.piece.second != side && selected.piece.second != Side.EITHER) {
                break
            }

            x--
            y++
        }

        x = cell.x + 1
        y = cell.y + 1

        // bottom right
        while (x in 0..7 && y >= 0 && y <= 7) {
            val selected = board.find(x, y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            moves.add(Move(side, cell, selected))

            // we've 'met' an opponent's piece, we *can* take it, but we can't progress further
            if (selected.piece.second != side && selected.piece.second != Side.EITHER) {
                break
            }

            x++
            y++
        }

        x = cell.x + 1
        y = cell.y - 1

        // top right
        while (x in 0..7 && y >= 0 && y <= 7) {
            val selected = board.find(x, y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            moves.add(Move(side, cell, selected))

            // we've 'met' an opponent's piece, we *can* take it, but we can't progress further
            if (selected.piece.second != side && selected.piece.second != Side.EITHER) {
                break
            }

            x++
            y--
        }

        if (removeMarked) {
            removeMarkedMoves(moves, board, side)
        }

        return moves
    }

}