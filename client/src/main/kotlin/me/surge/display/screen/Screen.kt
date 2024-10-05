package me.surge.display.screen

import me.surge.Main
import me.surge.client.Settings
import me.surge.display.components.Component
import me.surge.util.Button
import me.surge.util.subscribeThis
import me.surge.util.unsubscribeThis
import org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
import org.nvgu.NVGU
import java.awt.Rectangle

abstract class Screen(val previous: Screen?) {

    protected val bounds = Rectangle(Main.window.width, Main.window.height)

    val components = mutableListOf<Component>()
    var prioritised: Component? = null

    open fun update(mouseX: Float, mouseY: Float) = Unit
    abstract fun draw(ctx: NVGU, mouseX: Float, mouseY: Float)

    open fun click(mouseX: Float, mouseY: Float, button: Button) = false
    open fun char(char: Char) = false
    open fun key(code: Int) = false

    fun acceptDraw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        draw(ctx, mouseX, mouseY)

        components.filter { it != prioritised }.forEach { it.update(mouseX, mouseY) }
        components.filter { it != prioritised }.forEach { it.draw(ctx, mouseX, mouseY) }

        // whilst not fully intuitive having the prioritised component over others, this means it is drawn over other components
        prioritised?.update(mouseX, mouseY)
        prioritised?.draw(ctx, mouseX, mouseY)
    }

    fun acceptClick(mouseX: Float, mouseY: Float, button: Button): Boolean {
        if (prioritised?.click(mouseX, mouseY, button) == true) {
            return true
        }

        components.filter { it != prioritised }.forEach {
            if (it.click(mouseX, mouseY, button)) {
                return true
            }
        }

        return click(mouseX, mouseY, button)
    }

    fun acceptChar(char: Char): Boolean {
        if (prioritised?.char(char) == true) {
            return true
        }

        components.filter { it != prioritised }.forEach {
            if (it.char(char)) {
                return true
            }
        }

        return char(char)
    }

    fun acceptKey(code: Int): Boolean {
        if (prioritised?.key(code) == true) {
            return true
        }

        components.filter { it != prioritised }.forEach {
            if (it.key(code)) {
                return true
            }
        }

        if (key(code)) {
            return true
        }

        if (code == GLFW_KEY_ESCAPE && previous != null) {
            Main.screen = previous
            return true
        }

        return false
    }

    fun unsubscribe() {
        components.forEach { it.unsubscribeThis() }
    }

    protected fun <T : Component> register(component: T) = component.also {
        components.add(it.subscribeThis())
    }

    protected fun defaultBackground(ctx: NVGU) {
        ctx.rectangle(0f, 0f, Main.window.width.toFloat(), Main.window.height.toFloat(), Settings.theme.background)
    }

}