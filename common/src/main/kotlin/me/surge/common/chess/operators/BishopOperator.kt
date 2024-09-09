package me.surge.common.chess.operators

import me.surge.common.chess.Board
import me.surge.common.chess.Cell
import me.surge.common.chess.Side
import me.surge.common.chess.operators.KingOperator.removeMarkedCells

object BishopOperator : Operator {

    override fun collectTiles(cell: Cell, board: Board, side: Side, removeMarked: Boolean): List<Cell> {
        val cells = mutableListOf<Cell>()

        var x = cell.x - 1
        var y = cell.y - 1

        // top left
        while (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
            val selected = board.find(x, y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            cells.add(selected)

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
        while (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
            val selected = board.find(x, y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            cells.add(selected)

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
        while (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
            val selected = board.find(x, y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            cells.add(selected)

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
        while (x >= 0 && x <= 7 && y >= 0 && y <= 7) {
            val selected = board.find(x, y)

            // one of our pieces, we can't take
            if (selected.piece.second == side) {
                break
            }

            cells.add(selected)

            // we've 'met' an opponent's piece, we *can* take it, but we can't progress further
            if (selected.piece.second != side && selected.piece.second != Side.EITHER) {
                break
            }

            x++
            y--
        }

        if (removeMarked) {
            removeMarkedCells(cell, cells, board, side)
        }

        return cells
    }

}