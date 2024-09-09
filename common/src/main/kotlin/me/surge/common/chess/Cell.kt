package me.surge.common.chess

import me.surge.common.chessColumn
import me.surge.common.chessRow
import me.surge.common.packet.IEmbeddable
import org.json.JSONObject

data class Cell(val x: Int, val y: Int, var piece: Pair<Piece, Side>) {

    var moved = false

    val code: String
        get() = "${x.chessColumn}${x.chessRow}"

    var tag: Tag? = null
    var claimedCell: Pair<Int, Int>? = null

    fun offset(offsetX: Int, offsetY: Int, board: Board): Cell {
        return board.find(this.x + offsetX, this.y + offsetY)
    }

    fun inherit(cell: Cell, board: Board) {
        this.piece = cell.piece
        this.moved = true

        // TODO: castling
        cell.piece = Piece.EMPTY to Side.EITHER
        cell.moved = true

        if (cell.claimedCell != null) {
            board.find(cell.claimedCell!!.first, cell.claimedCell!!.second).piece = Piece.EMPTY to Side.EITHER
            cell.claimedCell = null
        }

        cell.tag = this.tag
        this.tag = null
    }

    fun tag(tag: Tag): Cell {
        this.tag = tag
        return this
    }

    enum class Tag {
        DOUBLE_MOVE
    }

    companion object : IEmbeddable<Cell> {

        override fun extract(key: String?, json: JSONObject): Cell? {
            val obj = runCatching { json.getJSONObject(key) }
                .getOrElse {
                    it.printStackTrace()
                    return null
                }

            val x = obj.getInt("x")
            val y = obj.getInt("y")
            val piece = obj.getEnum(Piece::class.java, "piece")
            val side = obj.getEnum(Side::class.java, "side")
            val moved = obj.getBoolean("moved")
            val tag = obj.optEnum(Tag::class.java, "tag")
            val claimedX = obj.optInt("claimedX", -1)
            val claimedY = obj.optInt("claimedY", -1)

            return Cell(x, y, piece to side).also {
                it.moved = moved
                it.tag = tag

                if (claimedX != -1) {
                    it.claimedCell = claimedX to claimedY
                } else {
                    it.claimedCell = null
                }
            }
        }

        override fun embed(obj: Cell) = JSONObject()
            .put("x", obj.x)
            .put("y", obj.y)
            .put("piece", obj.piece.first)
            .put("side", obj.piece.second)
            .put("moved", obj.moved)
            .put("tag", obj.tag)
            .put("claimedX", obj.claimedCell?.first)
            .put("claimedY", obj.claimedCell?.second)

    }

}