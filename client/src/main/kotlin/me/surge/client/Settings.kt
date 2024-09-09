package me.surge.client

import me.surge.Main
import me.surge.util.Theme
import org.json.JSONObject
import java.io.File

object Settings {

    var theme = Theme.dark

    fun save() {
        val json = JSONObject()

        json.put("theme", theme.id)

        val file = File("settings.json")

        if (!file.exists()) {
            file.createNewFile()
        }

        file.writeText(json.toString(4))
    }

    fun load() {
        val file = File("settings.json")

        if (!file.exists()) {
            Main.logger.warn("Settings file not found!")
            return
        }

        val json = JSONObject(file.readText())

        val newTheme = json.getString("theme")
        val themeObj = Theme.registeredThemes.firstOrNull { it.id == newTheme }

        if (themeObj != null) {
            theme = themeObj
        }
    }

}