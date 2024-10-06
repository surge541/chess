package me.surge.display.components

import me.surge.Main
import me.surge.client.Settings
import me.surge.util.Button
import org.nvgu.NVGU
import org.nvgu.util.Alignment

class EnumComponent<T : Enum<*>>(private val text: String, default: T, x: Float, y: Float, width: Float, height: Float) : Component(x, y, width, height) {

    private val list = ListComponent(default, 0f, y + 5f, 0f, height - 10f)

    val selected: T
        get() = list.selected

    override fun update(mouseX: Float, mouseY: Float) {
        if (list.expanded.animationFactor > 0f) {
            Main.screen.prioritised = this
        } else if (Main.screen.prioritised == this) {
            Main.screen.prioritised = null
        }
    }

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        ctx.roundedRectangle(x, y, width, height, Settings.theme.cornerRadius, Settings.theme.surface)
            .text("$text: ", x + Settings.theme.cornerRadius, y + height / 2f, Settings.theme.onSurface, "poppins", 16, Alignment.LEFT_MIDDLE)

        val textWidth = ctx.textWidth("$text:", "poppins", 16) + 10

        list.x = x + Settings.theme.cornerRadius + textWidth
        list.width = width - (Settings.theme.cornerRadius + textWidth) - Settings.theme.cornerRadius / 2f

        list.draw(ctx, mouseX, mouseY)
    }

    override fun click(mouseX: Float, mouseY: Float, button: Button): Boolean {
        return list.click(mouseX, mouseY, button)
    }

}