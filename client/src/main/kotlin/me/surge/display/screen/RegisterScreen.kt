package me.surge.display.screen

import me.surge.Main
import me.surge.amalia.handler.Listener
import me.surge.client.Settings
import me.surge.common.packet.RegisterPacket
import me.surge.display.components.ButtonComponent
import me.surge.display.components.TextComponent
import me.surge.util.Button
import me.surge.util.InputLayers
import org.nvgu.NVGU
import org.nvgu.util.Alignment
import java.awt.Color

class RegisterScreen(previous: Screen?) : Screen(previous) {

    val username = register(TextComponent("Username", 0f, 0f, 300f, 40f, inputLayer = InputLayers.email)).also { it.input = "TestUsername" }
    val email = register(TextComponent("Email", 0f, 0f, 300f, 40f, inputLayer = InputLayers.email)).also { it.input = "test.email@gmail.com" }
    val password = register(TextComponent("Password", 0f, 0f, 300f, 40f, inputLayer = InputLayers.password, censor = true)).also { it.input = "TestPassword" }
    val confirmPassword = register(TextComponent("Confirm Password", 0f, 0f, 300f, 40f, inputLayer = InputLayers.password, censor = true)).also { it.input = "TestPassword" }

    private val login = register(object : ButtonComponent("Register & Login", 0f, 0f, 300f, 40f) {

        override fun pressed(button: Button) {
            if (email.input.isNotBlank() && password.input.isNotBlank()) {
                if (password.input == confirmPassword.input) {
                    Main.serverConnection!!.send(RegisterPacket(username.input, email.input, password.input))
                } else {
                    message = "Password and Confirm Password fields must match"
                }
            } else {
                message = "All fields must be filled in"
            }
        }

    })

    var message = ""

    init {
        Main.bus.subscribe(this)
    }

    override fun update(mouseX: Float, mouseY: Float) {
        username.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f - 120f)
        email.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f - 70f)
        password.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f - 20f)
        confirmPassword.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f + 30f)
        login.setBounds(x = Main.window.width / 2f - 150f, y = Main.window.height / 2f + 80f)
    }

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        defaultBackground(ctx)

        ctx.text("Register Account", Main.window.width / 2f, Main.window.height / 2f - 150f, Settings.theme.onBackground, "poppins", 30, Alignment.CENTER_MIDDLE)
            .text(message, Main.window.width / 2f, Main.window.height - 30f, Color.RED, "poppins", 16, Alignment.CENTER_MIDDLE)
    }

    @Listener
    fun registrationResponse(packet: RegisterPacket.RegistrationResponsePacket) {
        when (packet.response) {
            RegisterPacket.RegistrationStatus.EMAIL_ALREADY_EXISTS -> {
                message = "Account with email ${email.input} already exists"
            }

            RegisterPacket.RegistrationStatus.USERNAME_ALREADY_EXISTS -> {
                message = "Account with username ${username.input} already exists"
            }

            else -> {
                Main.account = packet.accountDetails!!
                Main.bus.unsubscribe(this)
                Main.screen = MainScreen(this)

                Main.signedIn = true
            }
        }
    }

}