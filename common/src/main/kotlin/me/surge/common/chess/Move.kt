package me.surge.common.chess

import me.surge.common.packet.IEmbeddable
import org.json.JSONObject

data class Move(val side: Side, val from: Cell, val to: Cell) {

    override fun toString() = "Move($side, From: ${from.code}, To: ${to.code})"

    fun copy() = Move(side, from.copy(), to.copy())

    companion object : IEmbeddable<Move> {

        override fun extract(key: String?, json: JSONObject): Move? {
            val obj = runCatching { json.getJSONObject(key) }
                .getOrElse {
                    return null
                }

            val side = obj.getEnum(Side::class.java, "side")
            val from = Cell.extract("from", obj)!!
            val to = Cell.extract("to", obj)!!

            return Move(side, from, to)
        }

        override fun embed(obj: Move) = JSONObject()
            .put("side", obj.side)
            .put("from", Cell.embed(obj.from))
            .put("to", Cell.embed(obj.to))

    }

}
