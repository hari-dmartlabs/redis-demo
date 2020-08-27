package com.dlabs.redisdemo;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisPoolingClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnProperty(name = "com.dlabs.redis.enabled", havingValue = "true")
@Profile("default")
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport implements CachingConfigurer {


	@Value("${spring.redis.pool.max-active}")
	private int REDIS_POOL_MAX_ACTIVE;

	@Value("${spring.redis.pool.max-idle}")
	private int REDIS_POOL_MAX_IDLE;

	@Value("${spring.redis.pool.min-idle}")
	private int REDIS_POOL_MIN_IDLE;

	@Value("${spring.redis.pool.max-wait}")
	private long REDIS_POOL_TIMEOUT;

	@Value("${spring.redis.timeout}")
	private int REDIS_TIMEOUT;

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheManager cacheManager = RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory).build();
		return cacheManager;
	}

	@Bean
	RedisConnectionFactory redisConnectionFactory() {

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		// Maximum number of active connections that can be allocated from this pool at
		// the same time
		poolConfig.setMaxTotal(REDIS_POOL_MAX_ACTIVE);
		// Number of connections to Redis that just sit there and do nothing
		poolConfig.setMaxIdle(REDIS_POOL_MAX_IDLE);
		// Minimum number of idle connections to Redis - these can be seen as always
		// open and ready to serve
		poolConfig.setMinIdle(REDIS_POOL_MIN_IDLE);
		// The maximum number of milliseconds that the pool will wait (when there are no
		// available connections) for a connection to be returned before throwing an
		// exception
		poolConfig.setMaxWaitMillis(REDIS_POOL_TIMEOUT);
		// The minimum amount of time an object may sit idle in the pool before it is
		// eligible for eviction by the idle object evictor
		poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
		// The minimum amount of time a connection may sit idle in the pool before it is
		// eligible for eviction by the idle connection evictor
		poolConfig.setSoftMinEvictableIdleTimeMillis(Duration.ofSeconds(10).toMillis());
		// Idle connection checking period
		poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(5).toMillis());
		// Maximum number of connections to test in each idle check
		poolConfig.setNumTestsPerEvictionRun(3);
		// Tests whether connection is dead when connection retrieval method is called
		poolConfig.setTestOnBorrow(true);
		// Tests whether connection is dead when returning a connection to the pool
		poolConfig.setTestOnReturn(true);
		// Tests whether connections are dead during idle periods
		poolConfig.setTestWhileIdle(true);
		poolConfig.setBlockWhenExhausted(true);
		
		JedisConnectionFactory connectionFactory = new JedisConnectionFactory(poolConfig);
		
		return connectionFactory;

	}
	
	@Bean
	Jedis getJedis() {
		return new Jedis();
	}
	
	

	@Override
	public CacheErrorHandler errorHandler() {
		return new RedisCacheErrorHandler();
	}

	@Slf4j
	public static class RedisCacheErrorHandler implements CacheErrorHandler {

		@Override
		public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
			log.error("Unable to get from cache " + cache.getName() + " : " + exception.getMessage());
		}

		@Override
		public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
			log.error("Unable to put into cache " + cache.getName() + " : " + exception.getMessage());
		}

		@Override
		public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
			log.error("Unable to evict from cache " + cache.getName() + " : " + exception.getMessage());
		}

		@Override
		public void handleCacheClearError(RuntimeException exception, Cache cache) {
			log.error("Unable to clean cache " + cache.getName() + " : " + exception.getMessage());
		}
	}

}
