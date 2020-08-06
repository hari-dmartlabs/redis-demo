package com.dlabs.redisdemo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Greeting implements Serializable{
	
	public String greeting;

}
