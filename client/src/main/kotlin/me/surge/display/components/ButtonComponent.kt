package me.surge.display.components

import me.surge.animation.Animation
import me.surge.animation.Easing
import me.surge.util.Theme
import org.nvgu.NVGU
import org.nvgu.util.Alignment

class TextComponent(val default: String, x: Float, y: Float, width: Float, height: Float) : Component(x, y, width, height) {

    val selected = Animation(100f, false, Easing.LINEAR)
    var input = ""

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        ctx.roundedRectangle(x, y, width, height, Theme.cornerRadius, Theme.accent)
            .text(if (selected.state) input else { if (input.isNotBlank()) input else default }, x + Theme.cornerRadius, y + height / 2, Theme.altOne, "poppins", 16, Alignment.LEFT_MIDDLE)
    }

}