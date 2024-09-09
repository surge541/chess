package me.surge.display.components

import me.surge.util.Button
import org.nvgu.NVGU

abstract class Component(var x: Float, var y: Float, var width: Float, var height: Float) {

    open fun update(mouseX: Float, mouseY: Float) = Unit

    abstract fun draw(ctx: NVGU, mouseX: Float, mouseY: Float)

    open fun click(mouseX: Float, mouseY: Float, button: Button) = false
    open fun release(mouseX: Float, mouseY: Float, button: Button) = false
    open fun char(char: Char) = false
    open fun key(code: Int) = false

    open fun setBounds(x: Float = this.x, y: Float = this.y, width: Float = this.width, height: Float = this.height) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    protected fun hovered(mouseX: Float, mouseY: Float) = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height

}