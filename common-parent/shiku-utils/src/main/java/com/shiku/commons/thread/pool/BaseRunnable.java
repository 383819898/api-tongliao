package com.shiku.commons.thread.pool;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lidaye
 *
 */
public abstract class BaseRunnable implements Runnable{

	/**
	 * 被循环执行的次数
	 */
	public AtomicInteger loopCount = new AtomicInteger();
	
	
	
	
	
}
