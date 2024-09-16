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

    fun update(move: Move) {
        if (move.side != turn) {
            return
        }

        board.set(move)
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

    override fun toString() = "Game $id (W $white, B $black)\n\tTurn: $turn\n\tMoves:\n\t\t${board.moves.joinToString { "$it\n\t\t" }}"

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

            val playing = obj.getBoolean("playing")
            val endReason = obj.optEnum(EndReason::class.java, "endReason")
            val winner = obj.optEnum(Side::class.java, "winner")

            return ChessGame(id, white, black).also {
                it.turn = side

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
            .put("side", obj.turn)
            .put("playing", obj.playing)
            .put("endReason", obj.endReason)
            .put("winner", obj.winner)

    }

}