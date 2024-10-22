package me.surge.gui

import me.surge.auth.AuthorisationHandler
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.DefaultListModel
import javax.swing.JFrame
import javax.swing.JList
import javax.swing.JScrollPane

object GUI : JFrame("Chess Server") {

    private fun readResolve(): Any = GUI

    private val onlineUsersModel = DefaultListModel<String>()
    private val onlineUsersList = JList(onlineUsersModel)

    fun display() {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(400, 200)

        layout = BorderLayout()

        val scrollPane = JScrollPane(onlineUsersList)
        add(scrollPane, BorderLayout.CENTER)

        update()

        isVisible = true
    }

    fun update() {
        onlineUsersModel.clear()
        AuthorisationHandler.getOnlineUsers().forEach { onlineUsersModel.addElement(it.public.username) } // Re-populate
    }

}