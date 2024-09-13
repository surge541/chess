package me.surge.common.chess

import me.surge.common.chess.Piece.*
import me.surge.common.chess.Side.*
import me.surge.common.chess.operators.KingOperator
import me.surge.common.packet.IEmbeddable
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList

class Board {

    val cells = mutableListOf<Cell>().also {
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                if (initialCells.any { initialCell -> initialCell.x == x && initialCell.y == y }) {
                    val initial = initialCells.first { initialCell -> initialCell.x == x && initialCell.y == y }
                    it.add(Cell(initial.x, initial.y, initial.piece.copy()))
                    continue
                }

                it.add(Cell(x, y, EMPTY to EITHER))
            }
        }
    }

    val moves = CopyOnWriteArrayList<Move>()

    fun find(x: Int, y: Int): Cell = cells.find { cell -> cell.x == x && cell.y == y }!!
    fun findNullable(x: Int, y: Int): Cell? = cells.find { cell -> cell.x == x && cell.y == y }

    fun set(move: Move) {
        val starting = find(move.from.x, move.from.y)
        val resultant = find(move.to.x, move.to.y)

        // attempting to take our own piece?
        if (resultant.piece.second == starting.piece.second) {
            return
        }

        var rookSwap: Pair<Cell, Cell>? = null

        if (!starting.moved && starting.piece.first == KING) {
            val castlingData = KingOperator.getCastlingCells(starting, this)

            if (castlingData.first.contains(resultant)) {
                // king side
                if (resultant.x > starting.x) {
                    val rook = castlingData.second.first!!
                    rookSwap = rook to starting.offset(1, 0, this)
                } else {
                    val rook = castlingData.second.second!!
                    rookSwap = rook to starting.offset(-2, 0, this)
                }
            }
        }

        resultant.inherit(starting, this)

        rookSwap?.second?.inherit(rookSwap.first, this)

        moves.add(move)
    }

    fun findKing(side: Side): Cell {
        return cells.first { it.piece.first == KING && it.piece.second == side }
    }

    fun makeDummyClone() = Board().also { board ->
        board.cells.clear()

        cells.forEach { originalCell ->
            board.cells.add(Cell(originalCell.x, originalCell.y, originalCell.piece).also {
                // this function is used during check detections
                // without setting `moved` to true, it will assume that all pawns can move two tiles
                it.moved = true
            })
        }
    }

    override fun toString(): String {
        var result = ""

        var y = 0

        cells.forEach { cell ->
            if (y != cell.y) {
                result += '\n'
                y = cell.y
            }

            if (cell.piece.first == EMPTY) {
                result += "    "
                return@forEach
            }

            result += "${cell.piece.second.name[0]}${cell.piece.first.pieceName}"

            result += if (cell.piece.first == KNIGHT) {
                " "
            } else {
                "  "
            }
        }

        return result
    }

    companion object : IEmbeddable<Board> {

        val initialCells = arrayListOf(
            Cell(0, 0, ROOK to BLACK),
            Cell(1, 0, KNIGHT to BLACK),
            Cell(2, 0, BISHOP to BLACK),
            Cell(3, 0, QUEEN to BLACK),
            Cell(4, 0, KING to BLACK),
            Cell(5, 0, BISHOP to BLACK),
            Cell(6, 0, KNIGHT to BLACK),
            Cell(7, 0, ROOK to BLACK),

            Cell(0, 7, ROOK to WHITE),
            Cell(1, 7, KNIGHT to WHITE),
            Cell(2, 7, BISHOP to WHITE),
            Cell(3, 7, QUEEN to WHITE),
            Cell(4, 7, KING to WHITE),
            Cell(5, 7, BISHOP to WHITE),
            Cell(6, 7, KNIGHT to WHITE),
            Cell(7, 7, ROOK to WHITE)

            // Rook Backrank Checkmate
            /*Cell(7, 7, KING to WHITE),
            Cell(0, 0, KING to BLACK),
            Cell(3, 1, ROOK to WHITE),
            Cell(4, 2, ROOK to WHITE),*/

            // Kings
            /*Cell(2, 4, KING to WHITE),
            Cell(4, 4, KING to BLACK),*/

            // castling
            /*Cell(0, 0, ROOK to BLACK),
            Cell(4, 0, KING to BLACK),
            Cell(7, 0, ROOK to BLACK),

            Cell(0, 7, ROOK to WHITE),
            Cell(4, 7, KING to WHITE),
            Cell(7, 7, ROOK to WHITE)*/
        ).also {
            fun pawns(y: Int, side: Side) {
                for (x in 0 until 8) {
                    it.add(Cell(x, y, PAWN to side))
                }
            }

            pawns(1, BLACK)
            pawns(6, WHITE)
        }

        override fun embed(obj: Board) = JSONObject().also {
            it.put("moves", embedMoves(obj.moves))
        }

        override fun extract(key: String?, json: JSONObject): Board? {
            val obj = runCatching { json.getJSONObject(key) }
                .getOrElse {
                    return null
                }

            val board = Board()

            val moves = extractMoves(obj.getString("moves"), board)

            return board.also {
                moves.forEach { move ->
                    it.set(move)
                }
            }
        }

        private fun embedMoves(moves: List<Move>) = buildString {
            for (move in moves) {
                append("[${move.from.x},${move.from.y},${move.to.x},${move.to.y}")
            }
        }

        private fun extractMoves(string: String, board: Board): List<Move> {
            if (string.isEmpty()) {
                return emptyList()
            }

            val moves = mutableListOf<Move>()
            var side = WHITE

            val splitMoves = string.split("[")

            splitMoves.forEach {
                // first is always empty
                if (it.isEmpty()) {
                    return@forEach
                }

                val parts = it.split(",")

                moves.add(Move(side, board.find(parts[0].toInt(), parts[1].toInt()), board.find(parts[2].toInt(), parts[3].toInt())))

                side = if (side == WHITE) BLACK else WHITE
            }

            return moves
        }

    }

}