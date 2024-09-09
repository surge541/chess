package me.surge.display.screen

import me.surge.Main
import me.surge.client.Connection
import me.surge.display.components.ButtonComponent
import me.surge.display.components.TextComponent
import me.surge.util.Button
import me.surge.util.InputLayers
import me.surge.util.Theme
import org.nvgu.NVGU
import org.nvgu.util.Alignment

class ServerScreen : Screen() {

    val address = register(TextComponent("IP Address", 0f, 0f, 200f, 40f, inputLayer = InputLayers.address))
    val port = register(TextComponent("Port", 0f, 0f, 90f, 40f, inputLayer = InputLayers.integerDigits))

    private val connect = register(object : ButtonComponent("Connect", 0f, 0f, 300f, 40f) {

        override fun pressed(mouseX: Float, mouseY: Float, button: Button) {
            Main.connection = Connection(address.input, port.input.toInt()).also {
                it.begin()

                if (it.connected) {
                    Main.screen = MainScreen()
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
        ctx.text("Join Server", Main.window.width / 2f, Main.window.height / 2f - 150f, Theme.highlight, "poppins", 30, Alignment.CENTER_MIDDLE)
    }

}