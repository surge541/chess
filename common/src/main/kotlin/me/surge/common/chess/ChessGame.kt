package me.surge.common.chess

import me.surge.common.auth.PublicAccountDetails
import me.surge.common.chess.operators.KingOperator
import me.surge.common.packet.IEmbeddable
import org.json.JSONObject
import java.util.concurrent.CopyOnWriteArrayList

class ChessGame(val id: Int, val white: PublicAccountDetails, val black: PublicAccountDetails) {

    var turn = Side.WHITE
    var board = Board()

    var playing = true
    var winner: Side? = null
    var endReason: EndReason? = null

    val moves = CopyOnWriteArrayList<Move>()

    fun update(move: Move) {
        if (move.side != turn) {
            return
        }

        board.set(move.also { moves.add(it) })
        turn = if (turn == Side.WHITE) Side.BLACK else Side.WHITE

        val checkmateStatus = checkmated()

        if (checkmateStatus.first) {
            winner = checkmateStatus.second
            playing = false
            endReason = EndReason.CHECKMATE
        }
    }

    /**
     * Checks if either side has been checkmated by the other
     * @return whether a checkmate has occurred, and the side which wins
     * if neither has won, it should return <code>false to Side.EITHER</code>
     */
    private fun checkmated(): Pair<Boolean, Side> {
        val white = board.findKing(Side.WHITE)
        val black = board.findKing(Side.BLACK)

        if (KingOperator.inCheck(white, board, Side.WHITE)) {
            if (KingOperator.collectTiles(white, board, Side.WHITE).isEmpty()) {
                return true to Side.BLACK
            }
        }

        if (KingOperator.inCheck(black, board, Side.BLACK)) {
            if (KingOperator.collectTiles(black, board, Side.BLACK).isEmpty()) {
                return true to Side.WHITE
            }
        }

        return false to Side.EITHER
    }

    override fun toString() = "Game $id (W $white, B $black)\n\tTurn: $turn\n\tMoves:\n\t\t${moves.joinToString { "$it\n\t\t" }}"

    enum class EndReason {
        CHECKMATE,
        RESIGNATION,
        DRAW,
        STALEMATE
    }

    companion object : IEmbeddable<ChessGame> {

        override fun extract(key: String?, json: JSONObject): ChessGame? {
            val obj = runCatching { json.getJSONObject(key) }
                .getOrElse {
                    return null
                }

            val id = obj.getInt("id")
            val white = PublicAccountDetails.extract("white", obj)!!
            val black = PublicAccountDetails.extract("black", obj)!!
            val side = obj.getEnum(Side::class.java, "side")
            val board = Board.extract("board", obj)!!

            val moves = extractMoves(obj.getString("moves"), board)

            /*obj.getJSONArray("moves").forEach {
                it as JSONObject

                val from = it.getJSONObject("from")
                val to = it.getJSONObject("to")

                moves.add(
                    Move(
                        it.getEnum(Side::class.java, "side"),
                        Cell(
                            from.getInt("x"),
                            from.getInt("y"),
                            from.getJSONObject("piece").getEnum(Piece::class.java, "first") to from.getJSONObject("piece").getEnum(Side::class.java, "second")
                        ),

                        Cell(
                            to.getInt("x"),
                            to.getInt("y"),
                            to.getJSONObject("piece").getEnum(Piece::class.java, "first") to to.getJSONObject("piece").getEnum(Side::class.java, "second")
                        )
                    )
                )
            }*/

            val playing = obj.getBoolean("playing")
            val endReason = obj.optEnum(EndReason::class.java, "endReason")
            val winner = obj.optEnum(Side::class.java, "winner")

            return ChessGame(id, white, black).also {
                it.turn = side
                it.moves.clear()
                it.moves.addAll(moves)

                it.board = board

                it.playing = playing
                it.endReason = endReason
                it.winner = winner
            }
        }

        override fun embed(obj: ChessGame): JSONObject = JSONObject()
            .put("id", obj.id)
            .put("white", PublicAccountDetails.embed(obj.white))
            .put("black", PublicAccountDetails.embed(obj.black))
            .put("board", Board.embed(obj.board))
            .put("moves", embedMoves(obj.moves))
            .put("side", obj.turn)
            .put("playing", obj.playing)
            .put("endReason", obj.endReason)
            .put("winner", obj.winner)

        fun embedMoves(moves: List<Move>) = buildString {
            for (move in moves) {
                append("[${move.from.x},${move.from.y},${move.to.x},${move.to.y}")
            }
        }

        fun extractMoves(string: String, board: Board): List<Move> {
            if (string.isEmpty()) {
                return emptyList()
            }

            val moves = mutableListOf<Move>()
            var side = Side.WHITE

            val splitMoves = string.split("[")

            splitMoves.forEach {
                // first is always empty
                if (it.isEmpty()) {
                    return@forEach
                }

                val parts = it.split(",")

                moves.add(Move(side, board.find(parts[0].toInt(), parts[1].toInt()), board.find(parts[2].toInt(), parts[3].toInt())))

                side = if (side == Side.WHITE) Side.BLACK else Side.WHITE
            }

            return moves
        }

    }

}