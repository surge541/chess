package me.surge.display.components

import me.surge.animation.Animation
import me.surge.client.Settings
import me.surge.util.Button
import me.surge.util.Theme
import me.surge.util.Theme.unaryPlus
import org.nvgu.NVGU
import org.nvgu.util.Alignment

class ListComponent<T : Enum<*>>(default: T, x: Float, y: Float, width: Float, height: Float) : Component(x, y, width, height) {

    private val defaultEntries = default.javaClass.enumConstants.map { entry -> Entry(entry, x, y, 0f, 0f) }.toMutableList()
    private var entries = defaultEntries

    var selected = default
        set(value) {
            field = value

            entries = defaultEntries
            val selection = entries.first { it.value == value }
            entries.remove(selection)
            entries.addFirst(selection)
        }

    val expanded = Animation(100f, false, Theme.easing)

    init {
        entries = defaultEntries
        val selection = entries.first { it.value == selected }
        entries.remove(selection)
        entries.addFirst(selection)
    }

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        ctx.roundedRectangle(x, y, width, height(), Settings.theme.cornerRadius, Settings.theme.surface)

        ctx.scissor(x - 2, y - 2, width + 4, height() + 2) {
            var offset = y

            entries.forEach { entry ->
                entry.setBounds(x, offset, width, height)
                entry.draw(ctx, mouseX, mouseY)
                offset += height
            }
        }
    }

    override fun click(mouseX: Float, mouseY: Float, button: Button): Boolean {
        if (hovered(mouseX, mouseY)) {
            expanded.state = !expanded.state
            return true
        }

        if (expanded.state) {
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height()) {
                entries.forEach {
                    if (it.click(mouseX, mouseY, button)) {
                        return true
                    }
                }
            }
        }

        return false
    }

    fun height(): Float {
        return (height + (height * (entries.size - 1)) * expanded.animationFactor).toFloat()
    }

    inner class Entry(val value: T, x: Float, y: Float, width: Float, height: Float) : Component(x, y, width, height) {

        private val hovered = Animation(200f, false, Theme.easing)

        override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
            hovered.state = hovered(mouseX, mouseY)

            ctx.save()
                .scale(x + width / 2f, y + height / 2f, +hovered)
                .roundedRectangle(x, y, width, height, Settings.theme.cornerRadius, if (selected == value) Settings.theme.primary else Settings.theme.surface)
                .text(value.name, x + Settings.theme.cornerRadius, y + height / 2f, if (selected == value) Settings.theme.onPrimary else Settings.theme.onSurface, "poppins", 16, Alignment.LEFT_MIDDLE)
                .restore()
        }

        override fun click(mouseX: Float, mouseY: Float, button: Button): Boolean {
            if (button == Button.LEFT && hovered(mouseX, mouseY)) {
                selected = value
                expanded.state = false
                return true
            }

            return false
        }

    }

}