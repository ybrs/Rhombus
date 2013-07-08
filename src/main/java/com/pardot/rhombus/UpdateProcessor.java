package com.pardot.rhombus;

import com.pardot.rhombus.cobject.CObjectCQLGenerator;
import com.pardot.rhombus.cobject.CQLStatement;

import java.util.Map;
import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 7/5/13
 */
public class UpdateProcessor {

	private ObjectMapper objectMapper;
	private UUID lastProcessed;

	public UpdateProcessor(ObjectMapper om){
		this.objectMapper = om;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void process(){

	}




}
