package me.surge.client

import me.surge.Main
import me.surge.util.Theme
import org.json.JSONObject
import java.io.File

object Settings {

    // current colour scheme of the client UI
    var theme = Theme.dark

    fun save() {
        val json = JSONObject()

        // add all settings to the map
        json.put("theme", theme.id)

        // will be created in the workspace
        // TODO: use documents folder, or maybe AppData?
        val file = File("settings.json")

        if (!file.exists()) {
            file.createNewFile()
        }

        // write with an indent factor of 4, for easier reading
        file.writeText(json.toString(4))
    }

    fun load() {
        val file = File("settings.json")

        if (!file.exists()) {
            Main.logger.warn("Settings file not found!")
            return
        }

        // parse file to JSON
        val json = JSONObject(file.readText())

        val newTheme = json.getString("theme")
        val themeObj = Theme.registeredThemes.firstOrNull { it.id == newTheme }

        if (themeObj != null) {
            theme = themeObj
        }
    }

}