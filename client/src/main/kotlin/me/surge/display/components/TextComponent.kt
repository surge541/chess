package me.surge.display.components

import me.surge.animation.Animation
import me.surge.animation.Easing
import me.surge.client.Settings
import me.surge.common.times
import me.surge.util.Button
import me.surge.util.InputLayers
import me.surge.util.Theme
import me.surge.util.Theme.unaryMinus
import org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE
import org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER
import org.nvgu.NVGU
import org.nvgu.util.Alignment

class TextComponent(
    val default: String,
    x: Float,
    y: Float,
    width: Float,
    height: Float,
    val inputLayer: InputLayers.Layer = InputLayers.normal,
    var censor: Boolean = false
) : Component(x, y, width, height) {

    val hovered = Animation(200f, false, Easing.EXPO_IN_OUT)
    var input = ""
    var listening = false

    override fun update(mouseX: Float, mouseY: Float) {
        hovered.state = hovered(mouseX, mouseY)
    }

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        ctx.save()
            .scale(x + width / 2f, y + height / 2f, -hovered)

        ctx.roundedRectangle(x, y, width, height, Settings.theme.cornerRadius, Settings.theme.primary)
            .text(
                if (listening) {
                    "${if (censor) {
                        "*" * input.length
                    } else {
                        input
                    }}${Theme.typing}"
                } else {
                    if (input.isNotBlank()) {
                        if (censor) {
                            "*" * input.length
                        } else {
                            input
                        }
                    } else {
                        default
                    }
                }, x + Settings.theme.cornerRadius, y + height / 2, Settings.theme.onPrimary, "poppins", 16, Alignment.LEFT_MIDDLE)

        ctx.restore()
    }

    override fun click(mouseX: Float, mouseY: Float, button: Button): Boolean {
        if (button == Button.LEFT && hovered(mouseX, mouseY)) {
            listening = !listening
            return true
        }

        listening = false

        return false
    }

    override fun char(char: Char): Boolean {
        if (listening && inputLayer.isAllowed(char)) {
            input += char
        }

        return false
    }

    override fun key(code: Int): Boolean {
        if (listening) {
            when (code) {
                GLFW_KEY_BACKSPACE -> {
                    if (input.isNotEmpty()) {
                        input = input.substring(0, input.length - 1)
                    }
                }

                GLFW_KEY_ENTER -> {
                    listening = false
                }
            }
        }

        return false
    }

}