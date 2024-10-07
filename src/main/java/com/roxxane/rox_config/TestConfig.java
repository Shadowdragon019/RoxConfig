package com.roxxane.rox_config;

public class TestConfig extends Config {
	public TestConfig() {
		super("rox_config_test");

		addPart("WOOO",
			object -> test = object.get("test").getAsString(),
			object -> object.addProperty("test", "AHHHHHHHHH")
		);
	}

	public String test;
}