package me.surge.common

val Int.boolean: Boolean
    get() = this == 1

val Boolean.int: Int
    get() = if (this) 1 else 0

val String.title: String
    get() = "${this@title[0].uppercase()}${this@title.substring(1..<length).lowercase()}"

val Int.chessColumn: String
    get() = "abcdefgh"[this].toString()

val Int.chessRow: Int
    get() = this + 1

operator fun String.times(amount: Int): String {
    var result = ""

    for (i in 0 until amount) {
        result += this
    }

    return result
}

fun Thread.background(list: MutableList<Thread>): Thread {
    list.add(this)
    return this
}