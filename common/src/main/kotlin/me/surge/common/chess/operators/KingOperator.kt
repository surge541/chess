package me.surge.common.chess.operators

import me.surge.common.chess.Board
import me.surge.common.chess.Cell
import me.surge.common.chess.Side

object QueenOperator : Operator {

    override fun collectTiles(cell: Cell, board: Board, side: Side): List<Cell> {
        val cells = mutableListOf<Cell>()

        return cells
    }

}