/**
 * @Date : 2024. 08. 23.
 * @author : jieun(je-pa)
 */
package com.ecommerce.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
@EnableRedisRepositories
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Bean
  public RedisConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(host, port);
  }

  @Bean(name = "integerRedisTemplate")
  public RedisTemplate<String, Integer> longRedisTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    return createRedisTemplate(redisConnectionFactory, Integer.class);
  }

  private <T> RedisTemplate<String, T> createRedisTemplate(
      RedisConnectionFactory redisConnectionFactory, Class<T> type) {
    return createRedisTemplate(redisConnectionFactory, type, new ObjectMapper());
  }

  private <T> RedisTemplate<String, T> createRedisTemplate(
      RedisConnectionFactory redisConnectionFactory, Class<T> type, ObjectMapper objectMapper) {
    RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();
    redisTemplate.setKeySerializer(new StringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    return redisTemplate;
  }
}
