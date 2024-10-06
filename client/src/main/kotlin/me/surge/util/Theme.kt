package me.surge.util

import me.surge.Main
import me.surge.animation.Animation
import me.surge.animation.Easing
import me.surge.client.Settings
import me.surge.common.chess.Piece
import java.awt.Color

object Theme {

    @JvmField val dark = Option(
        "dark",
        "Dark",
        Color(2, 6, 23),
        Color(30, 41, 59),
        Color(51, 65, 85),
        Color(15, 23, 42),

        Color(203, 213, 225),
        Color(226, 232, 240),
        Color(241, 245, 249),
        Color(241, 245, 249),

        Color(51, 65, 85),
        Color(15, 23, 42),
        Color(0, 0, 0, 120),

        10f
    )

    val registeredThemes = listOf(
        dark
    )

    @JvmField val easing = Easing.EXPO_IN_OUT

    val typing: String
        get() = if (System.currentTimeMillis() % 800 > 400) "|" else ""

    operator fun Animation.unaryPlus() = (1f + (0.03f * this.animationFactor)).toFloat()
    operator fun Animation.unaryMinus() = (1f - (0.03f * this.animationFactor)).toFloat()

    val Color.boardAlternate: Color
        get() = if (this == Settings.theme.boardOne) Settings.theme.boardTwo else Settings.theme.boardOne

    data class Option(
        val id: String,
        val name: String,

        val background: Color,
        val surface: Color,
        val primary: Color,
        val secondary: Color,

        val onBackground: Color,
        val onSurface: Color,
        val onPrimary: Color,
        val onSecondary: Color,

        val boardOne: Color,
        val boardTwo: Color,
        val boardHighlight: Color,

        val cornerRadius: Float
    ) {

        fun loadTextures() {
            Piece.entries.forEach {
                if (it == Piece.EMPTY) {
                    return@forEach
                }

                val streamWhite = this::class.java.getResourceAsStream("/$id/${it.name.lowercase()}.png")
                val streamBlack = this::class.java.getResourceAsStream("/$id/${it.name.lowercase()}-black.png")

                Main.nvgu.createTexture("$id.${it.name.lowercase()}.white", streamWhite)
                Main.nvgu.createTexture("$id.${it.name.lowercase()}.black", streamBlack)
            }
        }

    }

}