package org.greatfree.framework.threading;

import org.greatfree.util.Tools;

// Created: 09/13/2019, Bing Li
public class TaskConfig
{
	public final static String PRINT_TASK_KEY = Tools.getHash("Print");
	public final static String PING_TASK_KEY = Tools.getHash("Ping");
	public final static String PONG_TASK_KEY = Tools.getHash("Pong");
	
	public final static String MAP_TASK_KEY = Tools.getHash("Map");
	public final static String REDUCE_TASK_KEY = Tools.getHash("Reduce");
	
	public final static String ADD_TASK_KEY = Tools.getHash("Add");

	public final static String MULTIPLY_TASK_KEY = Tools.getHash("Multiply");
}
