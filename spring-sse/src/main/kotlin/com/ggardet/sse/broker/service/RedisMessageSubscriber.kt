package com.ggardet.sse.broker.service

import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service

@Service
class RedisMessageSubscriber : MessageListener {
    var handlers = ArrayList<String>()
    override fun onMessage(message: Message, pattern: ByteArray?) {
        handlers.add(message.toString())
    }
}
