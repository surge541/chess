package me.surge.common.managers

import kotlin.concurrent.thread

object ThreadManager {

    private val threads = mutableListOf<Thread>()

    fun submit(name: String, block: () -> Unit): Thread {
        return thread(
            name = name,
            block = block
        ).also {
            threads.add(it)
        }
    }

    fun submit(thread: Thread): Thread {
        return thread.also {
            threads.add(it)
        }
    }

    fun loopingThread(name: String, block: () -> Unit): Thread = submit(object : Thread(name) {
        override fun run() {
            while (this.isAlive && !this.isInterrupted) {
                block()
            }
        }
    }.also { it.start() })

    fun destroy() {
        threads.forEach {
            it.interrupt()
        }
    }

}