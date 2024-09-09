package me.surge.display.screen

import me.surge.Main
import me.surge.amalia.handler.Listener
import me.surge.client.Settings
import me.surge.common.chess.Board
import me.surge.common.chess.Side
import me.surge.common.packet.GameCreationRequestPacket
import me.surge.common.packet.GameUpdateRequestPacket
import me.surge.display.components.BoardComponent
import me.surge.display.components.ButtonComponent
import me.surge.display.components.EnumComponent
import me.surge.util.Button
import me.surge.util.Theme
import org.nvgu.NVGU
import org.nvgu.util.Border
import java.awt.Rectangle
import kotlin.concurrent.thread

class MainScreen(previous: Screen?) : Screen(previous) {

    val userInfoBounds: Rectangle
        get() = Rectangle(bounds.x + 15, bounds.y + 15, 300, 80)

    val play = register(object : ButtonComponent("Play Random Opponent", 820f, 200f, 250f, 40f) {

        override fun pressed(mouseX: Float, mouseY: Float, button: Button) {
            Main.connection!!.post(GameCreationRequestPacket(
                Main.account,
                enum.selected
            ))
        }

    })

    val enum = register(EnumComponent("Side", Side.EITHER, 820f, 260f, 200f, 40f))

    var board = register(BoardComponent(Board(), 400f, 200f, 400f))

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        defaultBackground(ctx)

        // user info
        run {
            ctx.roundedRectangle(userInfoBounds, Settings.theme.cornerRadius, Settings.theme.primary)
                .text(Main.account.username, userInfoBounds.x + Settings.theme.cornerRadius, userInfoBounds.y + Settings.theme.cornerRadius, Settings.theme.surface, "poppins", 16)
        }
    }

    @Listener
    fun gameCreationRequestResponse(packet: GameCreationRequestPacket.GameCreationRequestResponsePacket) {
        Main.logger.info("Game Created")

        if (packet.account == null) {
            return
        }

        Main.account = packet.account!!
        Main.game = packet.game!!
        Main.side = if (Main.account.id == packet.game!!.white.id) Side.WHITE else Side.BLACK

        board.board = Main.game!!.board
        board.resetCells()
    }

}