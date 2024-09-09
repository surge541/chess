package me.surge.common.chess.operators

import me.surge.common.chess.Board
import me.surge.common.chess.Cell
import me.surge.common.chess.Side

object BishopOperator : Operator {

    override fun collectTiles(cell: Cell, board: Board, side: Side): List<Cell> {
        val cells = mutableListOf<Cell>()

        var x = cell.x
        var y = cell.y

        // top left
        while (x > 0 && y > 0) {
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

        return cells
    }

}