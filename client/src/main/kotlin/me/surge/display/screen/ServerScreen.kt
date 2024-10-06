package me.surge.display.screen

import me.surge.Main
import me.surge.client.ServerConnection
import me.surge.client.Settings
import me.surge.display.components.ButtonComponent
import me.surge.display.components.TextComponent
import me.surge.util.Button
import me.surge.util.InputLayers
import org.nvgu.NVGU
import org.nvgu.util.Alignment
import java.awt.Color

class ServerScreen : Screen(null) {

    var message = ""

    val address = register(TextComponent("IP Address", 0f, 0f, 200f, 40f, inputLayer = InputLayers.address)).also { it.input = "localhost" }
    val port = register(TextComponent("Port", 0f, 0f, 90f, 40f, inputLayer = InputLayers.integerDigits)).also { it.input = "5000" }

    private val connect = register(object : ButtonComponent("Connect", 0f, 0f, 300f, 40f) {

        override fun pressed(button: Button) {
            val serverConnection = ServerConnection(address.input, port.input.toInt())

            serverConnection.begin()

            if (serverConnection.connected) {
                Main.screen = LoginScreen(this@ServerScreen)
                Main.serverConnection = serverConnection
            } else {
                message = "Failed to connect to ${address.input}:${port.input}"
                Main.serverConnection = null
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

        //println(message)

        ctx.text("Join Server", Main.window.width / 2f, Main.window.height / 2f - 150f, Settings.theme.onBackground, "poppins", 30, Alignment.CENTER_MIDDLE)
            .text(message, Main.window.width / 2f, Main.window.height - 30f, Color.RED, "poppins", 16, Alignment.CENTER_MIDDLE)
    }

}