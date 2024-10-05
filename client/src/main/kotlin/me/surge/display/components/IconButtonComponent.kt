package me.surge.display.components

import me.surge.animation.Animation
import me.surge.animation.Easing
import me.surge.client.Settings
import me.surge.util.Button
import org.nvgu.NVGU

abstract class IconButtonComponent(val icon: String, x: Float, y: Float, width: Float, height: Float) : Component(x, y, width, height) {

    val hovered = Animation(200f, false, Easing.EXPO_IN_OUT)

    override fun update(mouseX: Float, mouseY: Float) {
        hovered.state = hovered(mouseX, mouseY)
    }

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        ctx.save()
            .scale(x + width / 2f, y + height / 2f, (0.97f + (0.03f * (1 - (hovered.animationFactor)))).toFloat())

        ctx.roundedRectangle(x, y, width, height, Settings.theme.cornerRadius, Settings.theme.primary)
            .texturedRectangle(x + 8f, y + 8f, width - 16f, height - 16f, icon)

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

}