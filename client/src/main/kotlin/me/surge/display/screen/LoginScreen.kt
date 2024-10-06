package me.surge.display.screen

import me.surge.Main
import me.surge.amalia.handler.Listener
import me.surge.client.Settings
import me.surge.common.packet.LoginPacket
import me.surge.display.components.ButtonComponent
import me.surge.display.components.TextComponent
import me.surge.util.Button
import me.surge.util.InputLayers
import org.nvgu.NVGU
import org.nvgu.util.Alignment
import java.awt.Color

class LoginScreen(previous: Screen?) : Screen(previous) {

    val email = register(TextComponent("Email or Password", 0f, 0f, 300f, 40f, inputLayer = InputLayers.email))
    val password = register(TextComponent("Password", 0f, 0f, 300f, 40f, inputLayer = InputLayers.password, censor = true))

    private val login = register(object : ButtonComponent("Login", 0f, 0f, 300f, 40f) {

        override fun pressed(mouseX: Float, mouseY: Float, button: Button) {
            if (email.input.isNotBlank() && password.input.isNotBlank()) {
                Main.serverConnection!!.send(LoginPacket(email.input, password.input))
            } else {
                message = "All fields must be filled in"
            }
        }

    })

    private val register = register(object : ButtonComponent("Register", 0f, 0f, 100f, 16f) {

        override fun pressed(mouseX: Float, mouseY: Float, button: Button) {
            Main.screen = RegisterScreen(this@LoginScreen)
        }

    }.dull())

    var message = ""

    init {
        Main.bus.subscribe(this)
    }

    override fun update(mouseX: Float, mouseY: Float) {
        email.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f - 70f)
        password.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f - 20f)
        login.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f + 30f)
        register.setBounds(x = Main.window.width / 2f - 50f, y = Main.window.height / 2f + 100f)
    }

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        defaultBackground(ctx)

        ctx.text("Login", Main.window.width / 2f, Main.window.height / 2f - 150f, Settings.theme.onBackground, "poppins", 30, Alignment.CENTER_MIDDLE)
            .text(message, Main.window.width / 2f, Main.window.height - 30f, Color.RED, "poppins", 16, Alignment.CENTER_MIDDLE)
    }

    @Listener
    fun loginResponse(packet: LoginPacket.LoginResponsePacket) {
        Main.logger.info("Login Response: ${packet.response}")

        when (packet.response) {
            LoginPacket.LoginStatus.ACCOUNT_DOESNT_EXIST -> message = "Account with email or password '${email.input}' doesn't exist"
            LoginPacket.LoginStatus.INCORRECT_PASSWORD -> message = "Incorrect Password"

            LoginPacket.LoginStatus.SUCCESS -> {
                Main.account = packet.account!!
                Main.bus.unsubscribe(this)
                Main.screen = MainScreen(this)
            }

            else -> message = "Unknown Error"
        }
    }

}