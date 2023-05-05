package com.ggardet.sse.broker.config

import com.ggardet.sse.broker.service.RedisMessageSubscriber
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter

@Profile("redis")
@Configuration
class RedisConfiguration {
    @Bean
    @ConditionalOnMissingBean(name = ["redisTemplate"])
    @Primary
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        return StringRedisTemplate(redisConnectionFactory)
    }

    @Bean
    fun messageListener(): MessageListenerAdapter {
        return MessageListenerAdapter(RedisMessageSubscriber())
    }

    @Bean
    fun redisContainer(
        redisConnectionFactory: RedisConnectionFactory,
        messageListener: MessageListenerAdapter
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory)
        container.addMessageListener(messageListener, topic())
        return container
    }

    @Bean
    fun topic(): ChannelTopic {
        return ChannelTopic("MESSAGES")
    }
}
