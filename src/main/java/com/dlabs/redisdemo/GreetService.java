package com.dlabs.redisdemo;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GreetService {
	
	//Cacheable will not call the method if the key if found in the cache.
	@Cacheable(value = "users", key = "#fname.concat(#lname)", unless = "#fname.equals('bypass') || #result == null" )
	public Greeting getGreeting (String fname, String lname) {
		log.info("In the get service method");
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(fname.length() == 0 && lname.length() == 0) return null;
		
		return new Greeting("Hello " + fname + " " + lname);
	}
	
	
	//CachePut will always call the method and then store the value in the cache
	@CachePut(value = "users", key = "#fname.concat(#lname)", unless = "#fname.equals('bypass')")
	public Greeting updateGreeting (String fname, String lname) {
		log.info("In the update service method");
		return new Greeting("Good Morning " + fname + " " + lname);
	}
			   

}
