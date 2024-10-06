package me.surge.util

import me.surge.Main
import java.awt.Color

fun <T : Any> T.subscribeThis(): T {
    Main.bus.subscribe(this)
    return this
}

fun <T : Any> T.unsubscribeThis(): T {
    Main.bus.unsubscribe(this)
    return this
}

fun Color.integrateAlpha(alpha: Float) = Color(this.red / 255f, this.green / 255f, this.blue / 255f, alpha)