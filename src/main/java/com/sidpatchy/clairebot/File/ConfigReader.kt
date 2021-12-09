package com.sidpatchy.clairebot.File

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


class ConfigReader {

    private var yaml = Yaml()

    private fun getConfig(file: String): Map<String, Any> {
        val inputStream: InputStream = FileInputStream(File("config/$file"))
        return yaml.load(inputStream)
    }

    fun getBool(file: String, parameter: String): Boolean {
        val config = getConfig(file)
        return config[parameter] as Boolean
    }

    fun getString(file: String, parameter: String): String? {
        val config = getConfig(file)
        return config[parameter] as String?
    }

    fun getInt(file: String, parameter: String): Int? {
        val config = getConfig(file)
        return config[parameter] as Int?
    }

    fun getFloat(file: String, parameter: String): Float? {
        val config = getConfig(file)
        return config[parameter] as Float?
    }

    fun getObj(file: String, parameter: String): Any? {
        val config = getConfig(file)
        return config[parameter]
    }
}