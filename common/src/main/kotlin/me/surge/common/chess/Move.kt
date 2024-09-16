package me.surge.common.chess

import me.surge.common.packet.IEmbeddable
import org.json.JSONObject

data class Move(val side: Side, val from: Cell, val to: Cell) {

    var tag: Tag? = null
    var claimCell: Cell? = null

    override fun toString() = "Move($side, From: ${from.x}${from.y}, To: ${to.x}${to.y}${if (tag != null) ", $tag" else ""}${if (claimCell != null) ", $claimCell" else ""})"

    fun copy() = Move(side, from.copy(), to.copy()).tag(this.tag)

    fun tag(tag: Tag?): Move {
        this.tag = tag
        return this
    }

    fun claimCell(cell: Cell?): Move {
        this.claimCell = cell
        return this
    }

    fun write(): String {
        return "${from.x}${from.y},${to.x}${to.y},${tag},${claimCell?.x ?: ""}${claimCell?.y ?: ""}"
    }

    enum class Tag {
        DOUBLE_PAWN_MOVE
    }

    companion object : IEmbeddable<Move> {

        fun read(input: String, board: Board, side: Side): Move {
            val parts = input.split(",")

            val from = parts[0]
            val to = parts[1]

            val tag = if (parts[2] == "null") null else Move.Tag.valueOf(parts[2])
            val claimed = parts[3]

            return Move(
                side,
                board.find(
                    from[0].digitToInt(),
                    from[1].digitToInt()
                ),
                board.find(
                    to[0].digitToInt(),
                    to[1].digitToInt()
                )
            ).tag(tag)
             .claimCell(with(claimed) {
                 if (isEmpty()) {
                     null
                 } else {
                     board.find(
                         this[0].digitToInt(),
                         this[1].digitToInt()
                     )
                 }
             })
        }

        override fun extract(key: String?, json: JSONObject): Move? {
            val obj = runCatching { json.getJSONObject(key) }
                .getOrElse {
                    return null
                }

            val side = obj.getEnum(Side::class.java, "side")
            val tag = obj.optEnum(Tag::class.java, "tag")
            val from = Cell.extract("from", obj)!!
            val to = Cell.extract("to", obj)!!
            val claimed = Cell.extract("claimed", obj)

            return Move(side, from, to)
                .tag(tag)
                .claimCell(claimed)
        }

        override fun embed(obj: Move) = JSONObject()
            .put("side", obj.side)
            .put("tag", obj.tag)
            .put("from", Cell.embed(obj.from))
            .put("to", Cell.embed(obj.to))
            .put("claimed", if (obj.claimCell != null) Cell.embed(obj.claimCell!!) else null)


    }

}
