package com.dlabs.redisdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
	
	@Autowired
	GreetService greetService;
	
	@GetMapping(path = "/greet", produces = MediaType.APPLICATION_JSON_VALUE)
	public Greeting getData(@RequestParam(name ="fname") String fname, @RequestParam(name ="lname") String lname ) {
		
		return greetService.getGreeting(fname, lname);
		
	}
	
	@PutMapping(path = "/greet", produces = MediaType.APPLICATION_JSON_VALUE)
	public Greeting updateData(@RequestParam(name ="fname") String fname, @RequestParam(name ="lname") String lname) {
		
		return greetService.updateGreeting(fname, lname);
	}

}
