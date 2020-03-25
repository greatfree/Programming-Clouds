package org.greatfree.dip.threading.mrtc;

// Created: 09/19/2019, Bing Li
public class MRConfig
{
	public final static int ID_LENGTH = 3;
	public final static int MAX_CHILDREN_SIZE = 8;
	public final static int THREAD_COUNT_PER_SLAVE = 4;
//	public final static int THREAD_COUNT_PER_SLAVE = 10;
	public final static String DT_PREFIX = "D-";
	public final static String THREAD_PREFIX = "T-";
	public final static int MINIMUM_SLAVE_SIZE = 1;
	public final static int MINIMUM_MUTLI_SLAVE_SIZE = 2;
	public final static String MAP_TASK = "MAP_TASK: ";
	public final static String REDUCE_TASK = "REDUCE_TASK: ";
}
