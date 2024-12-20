package com.sidpatchy.clairebot.Lang

import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User

data class ContextManager(
    val server: Server?,
    val channel: TextChannel?,
    val author: User?,
    val user: User?,
    val message: Message?,
    private val dynamicData: MutableMap<String, Any> = mutableMapOf()
) {
    // Enum to define known context types
    enum class ContextType {
        POLL,
        SANTA,
        // Add other types as needed
    }

    // Add dynamic data with type safety
    fun addData(type: ContextType, key: String, value: Any) {
        dynamicData["${type.name.lowercase()}.$key"] = value
    }

    // Get dynamic data with type safety
    @Suppress("UNCHECKED_CAST")
    fun <T> getData(type: ContextType, key: String): T? {
        return dynamicData["${type.name.lowercase()}.$key"] as? T
    }

    // Helper functions for specific context types
    fun addPollData(pollId: String, question: String) {
        addData(ContextType.POLL, "id", pollId)
        addData(ContextType.POLL, "question", question)
    }

    fun addSantaData(giftee: User, theme: String) {
        addData(ContextType.SANTA, "giftee", giftee)
        addData(ContextType.SANTA, "theme", theme)
    }
}