package me.surge

import me.surge.amalia.Bus
import me.surge.amalia.handler.Listener
import me.surge.client.Connection
import me.surge.client.Settings
import me.surge.common.auth.Account
import me.surge.common.background
import me.surge.common.chess.ChessGame
import me.surge.common.chess.ChessGame.EndReason
import me.surge.common.chess.Side
import me.surge.common.log.Logger
import me.surge.common.packet.GameUpdateRequestPacket
import me.surge.common.util.Timer
import me.surge.display.Window
import me.surge.display.screen.Screen
import me.surge.display.screen.ServerScreen
import me.surge.event.CharPressedEvent
import me.surge.event.ClickEvent
import me.surge.event.KeyEvent
import me.surge.util.Theme
import me.surge.util.subscribeThis
import me.surge.util.unsubscribeThis
import org.nvgu.NVGU
import org.nvgu.util.Alignment
import kotlin.concurrent.thread

object Main {

    val logger = Logger("Client")
    val window = Window("Chess", 1280, 720)

    val nvgu = NVGU()
    val bus = Bus().also { it.subscribe(Main) }

    var screen: Screen = ServerScreen()
        set(value) {
            // unsubscribe all
            field.unsubscribeThis()
                 .unsubscribe()

            field = value.subscribeThis()
        }

    var connection: Connection? = null
    lateinit var account: Account

    private val gameUpdateTimer = Timer()
    private lateinit var gameUpdateThread: Thread

    var game: ChessGame? = null
    var side: Side? = null

    var lastWinner: Side? = null
    var lastEndReason: EndReason? = null

    private var open = true
    val backgroundThreads = mutableListOf<Thread>()

    @JvmStatic fun main(args: Array<String>) {
        /*val board = Board()
        println(board)
        println(KingOperator.collectTiles(board.find(4, 7), board, Side.WHITE).map { "$it\n" })

        return*/

        Settings.load()

        gameUpdateThread = thread {
            while (open) {
                try {
                    if (gameUpdateTimer.passed(500) && game != null) {
                        connection?.post(GameUpdateRequestPacket(game!!.id))
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
        }.background(backgroundThreads)

        window.run({
            nvgu.create()
                .createFont("poppins", this.javaClass.getResourceAsStream("/Poppins-Regular.ttf"))
                .setFontData("poppins", 20, Alignment.LEFT_TOP)

            Theme.registeredThemes.forEach(Theme.Option::loadTextures)
        }) {
            if (connection != null && !connection!!.connected) {
                connection = null
                screen = ServerScreen()
            }

            if (game != null && !game!!.playing) {
                lastWinner = game!!.winner
                lastEndReason = game!!.endReason
            }

            nvgu.frame(window.width, window.height) {
                screen.update(window.mouseX, window.mouseY)
                screen.beginDraw(nvgu, window.mouseX, window.mouseY)
            }
        }.terminate {
            open = false
            nvgu.destroy()

            backgroundThreads.forEach {
                it.interrupt()
            }
        }

        Settings.save()
    }

    @Listener
    fun clickEvent(event: ClickEvent) {
        screen.acceptClick(event.mouseX, event.mouseY, event.button)
    }

    @Listener
    fun charEvent(event: CharPressedEvent) {
        screen.acceptChar(event.char)
    }

    @Listener
    fun keyEvent(event: KeyEvent) {
        screen.acceptKey(event.code)
    }

    @Listener
    fun onGameUpdateResponse(packet: GameUpdateRequestPacket.GameUpdateRequestResponsePacket) {
        this.game = packet.game

        if (game?.playing == false) {
            this.lastWinner = game?.winner
            this.lastEndReason = game?.endReason
        }
    }

}