package me.surge.common.log

import java.text.SimpleDateFormat
import java.util.Date

class Logger(val name: String) {

    private val prefix: String
        get() = "$name @ ${SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date())}"

    fun info(message: String) = println("[$prefix/INFO]: $message")
    fun warn(message: String) = println("[$prefix/WARN]: $message")
    fun error(message: String) = println("[$prefix/ERROR]: $message")

}