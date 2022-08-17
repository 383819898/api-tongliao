package com.shiku.mianshi.utils.sendpay.sdk.util;

import java.io.IOException;

public class ConfigurationManager extends com.netflix.config.ConfigurationManager {
	public static void loadProperties(String[] configNames) throws IOException {
		for (int i = 0; i < configNames.length; i++) {
			loadAppOverrideProperties(configNames[i]);
		}
	}
}
