package me.surge.display.screen

import me.surge.Main
import me.surge.client.Connection
import me.surge.common.packet.LoginPacket
import me.surge.display.components.ButtonComponent
import me.surge.display.components.TextComponent
import me.surge.util.Button
import me.surge.util.InputLayers
import me.surge.util.Theme
import org.nvgu.NVGU
import org.nvgu.util.Alignment

class LoginScreen : Screen() {

    val email = register(TextComponent("Email", 0f, 0f, 300f, 40f, inputLayer = InputLayers.email))
    val password = register(TextComponent("Password", 0f, 0f, 300f, 40f, inputLayer = InputLayers.password, censor = true))

    private val login = register(object : ButtonComponent("Login", 0f, 0f, 300f, 40f) {

        override fun pressed(mouseX: Float, mouseY: Float, button: Button) {
            if (email.input.isNotBlank() && password.input.isNotBlank()) {
                Main.connection!!.post(LoginPacket(email.input, password.input))
            }
        }

    })

    private val register = register(object : ButtonComponent("register", 0f, 0f, 100f, 16f) {

        override fun pressed(mouseX: Float, mouseY: Float, button: Button) {

        }

    })

    override fun update(mouseX: Float, mouseY: Float) {
        email.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f - 70f)
        password.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f - 20f)
        login.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f + 30f)
    }

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        defaultBackground(ctx)
        ctx.text("Login", Main.window.width / 2f, Main.window.height / 2f - 150f, Theme.highlight, "poppins", 30, Alignment.CENTER_MIDDLE)
    }

}