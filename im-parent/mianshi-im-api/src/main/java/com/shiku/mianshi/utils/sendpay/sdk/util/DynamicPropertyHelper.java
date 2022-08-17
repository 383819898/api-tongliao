package com.shiku.mianshi.utils.sendpay.sdk.util;

import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

public class DynamicPropertyHelper {
	private static final DynamicPropertyFactory dynamicPropertyFactory;

	public static DynamicPropertyFactory getDynamicPropertyFactory() {
		return DynamicPropertyHelper.dynamicPropertyFactory;
	}

	public static DynamicStringProperty getStringProperty(final String propName) {
		return getDynamicPropertyFactory().getStringProperty(propName, "");
	}

	public static DynamicStringProperty getStringProperty(final String propName, final String defaultValue) {
		return getDynamicPropertyFactory().getStringProperty(propName, defaultValue);
	}

	public static DynamicIntProperty getIntProperty(final String propName, final int defaultValue) {
		return getDynamicPropertyFactory().getIntProperty(propName, defaultValue);
	}

	public static DynamicBooleanProperty getBooleanProperty(final String propName, final boolean defaultValue) {
		return getDynamicPropertyFactory().getBooleanProperty(propName, defaultValue);
	}

	static {
		dynamicPropertyFactory = DynamicPropertyFactory.getInstance();
	}
}
