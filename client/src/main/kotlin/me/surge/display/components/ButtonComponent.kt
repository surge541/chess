package me.surge.display.components

import me.surge.animation.Animation
import me.surge.animation.Easing
import me.surge.client.Settings
import me.surge.util.Button
import me.surge.util.Theme
import org.nvgu.NVGU
import org.nvgu.util.Alignment

abstract class ButtonComponent(val text: String, x: Float, y: Float, width: Float, height: Float) : Component(x, y, width, height) {

    private var dull = false
    val hovered = Animation(200f, false, Easing.EXPO_IN_OUT)

    override fun update(mouseX: Float, mouseY: Float) {
        hovered.state = hovered(mouseX, mouseY)
    }

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        ctx.save()
            .scale(x + width / 2f, y + height / 2f, (0.97f + (0.03f * (1 - (hovered.animationFactor)))).toFloat())

        if (dull) {
            ctx.text(text, x + width / 2f, y + height / 2f, Settings.theme.onPrimary, "poppins", 16, Alignment.CENTER_MIDDLE)

            return
        }

        ctx.roundedRectangle(x, y, width, height, Settings.theme.cornerRadius, Settings.theme.primary)
            .text(text, x + width / 2f, y + height / 2f, Settings.theme.onPrimary, "poppins", 16, Alignment.CENTER_MIDDLE)

        ctx.restore()
    }

    override fun click(mouseX: Float, mouseY: Float, button: Button): Boolean {
        if (button == Button.LEFT && hovered.state) {
            pressed(mouseX, mouseY, button)

            return true
        }

        return false
    }

    abstract fun pressed(mouseX: Float, mouseY: Float, button: Button)

    fun dull(): ButtonComponent {
        this.dull = true
        return this
    }

}