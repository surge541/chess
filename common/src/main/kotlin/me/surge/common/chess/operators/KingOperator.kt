package me.surge.common.chess.operators

import me.surge.common.chess.*
import java.util.concurrent.CopyOnWriteArrayList

object KingOperator : Operator {

    val offsets = listOf(
        // left
        -1 to -1,
        -1 to 0,
        -1 to 1,

        // right
        1 to -1,
        1 to 0,
        1 to 1,

        // top
        0 to -1,

        // bottom
        0 to 1,
    )

    override fun collectTiles(cell: Cell, board: Board, side: Side, removeMarked: Boolean): List<Cell> {
        val cells = CopyOnWriteArrayList<Cell>()

        offsets.forEach { offset ->
            val selected = board.findNullable(cell.x + offset.first, cell.y + offset.second)

            if (selected != null) {
                // one of our pieces, we can't take
                if (selected.piece.second == side) {
                    return@forEach
                }

                cells.add(selected)
            }
        }

        cells.addAll(getCastlingCells(cell, board).first)

        if (removeMarked) {
            removeMarkedCells(cell, cells, board, side)
        }

        return cells
    }

    fun getCastlingCells(cell: Cell, board: Board): Pair<List<Cell>, Pair<Cell?, Cell?>> {
        val cells = mutableListOf<Cell>()

        var kingsideRook: Cell? = null
        var queensideRook: Cell? = null

        if (!cell.moved) {
            kingsideRook = cell.offset(3, 0, board)
            queensideRook = cell.offset(-4, 0, board)

            if (!kingsideRook.moved) {
                var kingSide = true

                for (x in cell.x + 1..<kingsideRook.x) {
                    val middleCell = board.find(x, cell.y)

                    if (middleCell.piece.first != Piece.EMPTY) {
                        kingSide = false
                        break
                    }
                }

                if (kingSide) {
                    cells.add(board.find(cell.x + 2, cell.y))
                }
            }

            if (!queensideRook.moved) {
                var queenSide = true

                for (x in cell.x - 1 downTo queensideRook.x + 1) {
                    val middleCell = board.find(x, cell.y)

                    if (middleCell.piece.first != Piece.EMPTY) {
                        queenSide = false
                        break
                    }
                }

                if (queenSide) {
                    cells.add(board.find(cell.x - 3, cell.y))
                }
            }
        }

        return cells to (kingsideRook to queensideRook)
    }

    fun removeMarkedCells(origin: Cell, cells: MutableList<Cell>, board: Board, side: Side) {
        val buffer = mutableListOf<Cell>()

        cells.forEach {
            val move = Move(side, origin, it)
            val dummyBoard = board.makeDummyClone()

            dummyBoard.set(move.copy())

            if (inCheck(dummyBoard.findKing(side), dummyBoard, side)) {
                buffer.add(it)
            }
        }

        cells.removeIf { cell -> buffer.any { it.x == cell.x && it.y == cell.y } }
    }

    fun inCheck(cell: Cell, board: Board, side: Side): Boolean {
        val cells = mutableListOf<Cell>()

        fun checkLine(x: Int, y: Int, condition: (Int, Int) -> Boolean, increment: (Int, Int) -> Pair<Int, Int>) {
            var x = x
            var y = y

            while (condition(x, y)) {
                val selected = board.findNullable(x, y)

                if (selected != null) {
                    // skip empty cells
                    if (selected.piece.first == Piece.EMPTY) {
                        val next = increment(x, y)
                        x = next.first
                        y = next.second

                        continue
                    }

                    cells.add(selected)
                    break
                }

                val next = increment(x, y)
                x = next.first
                y = next.second
            }
        }

        // left
        checkLine(cell.x - 1, cell.y, { x, _ -> x >= 0 }, { x, y -> x - 1 to y })

        // right
        checkLine(cell.x + 1, cell.y, { x, _ -> x <= 7 }, { x, y -> x + 1 to y })

        // up
        checkLine(cell.x, cell.y - 1, { _, y -> y >= 0 }, { x, y -> x to y - 1 })

        // down
        checkLine(cell.x, cell.y + 1, { _, y -> y <= 7 }, { x, y -> x to y + 1 })

        // top left
        checkLine(cell.x - 1, cell.y - 1, { x, y -> x >= 0 && y >= 0 }, { x, y -> x - 1 to y - 1 })

        // top right
        checkLine(cell.x + 1, cell.y - 1, { x, y -> x <= 7 && y >= 0 }, { x, y -> x + 1 to y - 1 })

        // bottom left
        checkLine(cell.x - 1, cell.y + 1, { x, y -> x >= 0 && y <= 7 }, { x, y -> x - 1 to y + 1 })

        // bottom right
        checkLine(cell.x + 1, cell.y + 1, { x, y -> x <= 7 && y <= 7 }, { x, y -> x + 1 to y + 1 })

        // knight checks
        KnightOperator.offsets.forEach { (x, y) ->
            val selected = board.findNullable(cell.x + x, cell.y + y)

            if (selected != null) {
                // one of our pieces, we can't take
                if (selected.piece.second == side) {
                    return@forEach
                }

                // skip empty cells
                if (selected.piece.first == Piece.EMPTY) {
                    return@forEach
                }

                cells.add(selected)
            }
        }

        cells.forEach { marked ->
            val operator = Operator.getOperator(marked, marked.piece.second)

            if (operator == KingOperator) {
                offsets.forEach { offset ->
                    val offsetCell = board.findNullable(marked.x + offset.first, marked.y + offset.second)

                    if (offsetCell != null && offsetCell.piece.first == Piece.KING) {
                        return offsetCell.x == cell.x && offsetCell.y == cell.y
                    }
                }

                return@forEach
            }

            if (operator != null && operator.collectTiles(marked, board, marked.piece.second, removeMarked = false).contains(cell)) {
                return true
            }
        }

        return false
    }

}