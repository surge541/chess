package me.surge.common.chess

import me.surge.common.packet.IEmbeddable
import org.json.JSONObject

data class Cell(val x: Int, val y: Int, var piece: Pair<Piece, Side>) {

    var moved = false

    fun offset(offsetX: Int, offsetY: Int, board: Board): Cell {
        return board.find(this.x + offsetX, this.y + offsetY)
    }

    fun inherit(cell: Cell) {
        this.piece = cell.piece
        this.moved = true

        cell.piece = Piece.EMPTY to Side.EITHER
        cell.moved = true
    }

    companion object : IEmbeddable<Cell> {

        override fun extract(key: String?, json: JSONObject): Cell? {
            val obj = runCatching { json.optJSONObject(key) }
                .getOrElse {
                    it.printStackTrace()
                    return null
                }

            if (obj == null) {
                return null
            }

            val x = obj.getInt("x")
            val y = obj.getInt("y")
            val piece = obj.getEnum(Piece::class.java, "piece")
            val side = obj.getEnum(Side::class.java, "side")
            val moved = obj.getBoolean("moved")

            return Cell(x, y, piece to side).also {
                it.moved = moved
            }
        }

        override fun embed(obj: Cell): JSONObject = JSONObject()
            .put("x", obj.x)
            .put("y", obj.y)
            .put("piece", obj.piece.first)
            .put("side", obj.piece.second)
            .put("moved", obj.moved)

    }

}