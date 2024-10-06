package me.surge.util

object InputLayers {

    @JvmField val integerDigits = Layer("0123456789")
    @JvmField val normal = Layer("abcdefghijklmnopqrstuvwxyz1234567890-_@',.\"!£$£%^&*()_+*-/")
    @JvmField val address = Layer("abcdefghijklmnopqrstuvwxyz1234567890.")
    @JvmField val email = Layer("abcdefghijklmnopqrstuvwxyz0123456789!#\$%&*+-/=?^_`{|}~.(),:;<>@[\\]")
    @JvmField val password = Layer("abcdefghijklmnopqrstuvwxyz0123456789!#\$%&*+-/=?^_`{|}~.(),:;<>@[\\]")

    class Layer(private val characters: String, private val caseSensitive: Boolean = false) {

        fun isAllowed(char: Char): Boolean = characters.contains(if (!caseSensitive) char.lowercaseChar() else char)

    }

}