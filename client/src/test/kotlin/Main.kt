import me.surge.common.auth.PublicAccountDetails
import me.surge.common.chess.Board
import me.surge.common.chess.ChessGame
import me.surge.common.chess.Move
import me.surge.common.chess.Side
import me.surge.common.chess.operators.PawnOperator

fun main(args: Array<String>) {
    /*val board = Board()

    val game = ChessGame(
        0,
        PublicAccountDetails(0, "TestUsername", true, game),
        PublicAccountDetails(1, "TestUsername2", true, game)
    )

    game.board = board

    println(board)

    board.set(Move(Side.WHITE, board.find(3, 6), board.find(3, 4)).tag(Move.Tag.DOUBLE_PAWN_MOVE))
    println(PawnOperator.collectTiles(board.find(0, 1), board, Side.BLACK))
    board.set(Move(Side.BLACK, board.find(2, 1), board.find(2, 2)))
    board.set(Move(Side.WHITE, board.find(7, 6), board.find(7, 4)).tag(Move.Tag.DOUBLE_PAWN_MOVE))
    board.set(Move(Side.BLACK, board.find(3, 0), board.find(0, 3)))

    println(board)

    println(game.checkmated())*/
}