package com.sidpatchy.clairebot.Util.Cache

import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.Message

data class MessageCacheEntry (
    val channel: TextChannel,
    val timeAdded: Long,
    val messages: List<Message>,
)