package com.dlabs.redisdemo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;


@Component
@Profile("default")
@Slf4j
public class WarmupService implements ApplicationRunner {

	@Autowired
	Jedis jedis;

	@Value("${com.dlabs.redis.cachens}")
	String nameSpace;

	public void run(ApplicationArguments args) throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();

		//check if the cache needs to be rebuilt.
		//The logic here is to check the time but it can be more intelligent.
		String cachedDateStr = jedis.get("cache::datetime");
		if (cachedDateStr != null) {
			log.debug("Checking if the cache could be stale");
			LocalDateTime cachedDate = LocalDateTime.parse(cachedDateStr, formatter);
			if (cachedDate.plusSeconds(10).isAfter(now)) {
				log.info("cache already warm..wont burn it now..");
				return;
			}
		}

		log.info("Cold cache.....warming up");

		// get keys and vals from db or elsewhere.
		// typically this is done in a loop

		String key1 = nameSpace + "::johndoe";
		Greeting value1 = new Greeting("Hello johndoer");

		String key2 = nameSpace + "::johnconor";
		Greeting value2 = new Greeting("Hello johnconor");

		RedisSerializer<Object> szr = RedisSerializer.java();
		jedis.set(key1.getBytes(), szr.serialize(value1));
		jedis.set(key2.getBytes(), szr.serialize(value2));

		jedis.set("cache::datetime", new String(formatter.format(LocalDateTime.now())));

	}

}
