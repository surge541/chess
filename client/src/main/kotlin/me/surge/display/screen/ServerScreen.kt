package me.surge.display.screen

import me.surge.Main
import me.surge.client.Connection
import me.surge.client.Settings
import me.surge.common.chess.Side
import me.surge.display.components.ButtonComponent
import me.surge.display.components.EnumComponent
import me.surge.display.components.TextComponent
import me.surge.util.Button
import me.surge.util.InputLayers
import me.surge.util.Theme
import org.nvgu.NVGU
import org.nvgu.util.Alignment
import java.awt.Color

class ServerScreen : Screen(null) {

    var message = ""

    val address = register(TextComponent("IP Address", 0f, 0f, 200f, 40f, inputLayer = InputLayers.address)).also { it.input = "localhost" }
    val port = register(TextComponent("Port", 0f, 0f, 90f, 40f, inputLayer = InputLayers.integerDigits)).also { it.input = "5000" }

    private val connect = register(object : ButtonComponent("Connect", 0f, 0f, 300f, 40f) {

        override fun pressed(mouseX: Float, mouseY: Float, button: Button) {
            Main.connection = Connection(address.input, port.input.toInt()).also {
                it.begin()

                if (it.connected) {
                    Main.screen = LoginScreen(this@ServerScreen)
                } else {
                    message = "Failed to connect to ${address.input}:${port.input}"
                }
            }
        }

    })

    override fun update(mouseX: Float, mouseY: Float) {
        address.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f - 20f)
        port.setBounds(x = Main.window.width / 2f + 60f, y = Main.window.height / 2f - 20f)
        connect.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f + 30f)
    }

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        defaultBackground(ctx)

        ctx.text("Join Server", Main.window.width / 2f, Main.window.height / 2f - 150f, Settings.theme.onBackground, "poppins", 30, Alignment.CENTER_MIDDLE)
            .text(message, Main.window.width / 2f, Main.window.height - 30f, Color.RED, "poppins", 16, Alignment.CENTER_MIDDLE)
    }

}