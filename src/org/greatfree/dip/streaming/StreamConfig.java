package org.greatfree.dip.streaming;

import java.util.List;
import java.util.Map;

import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

// Created: 03/20/2020, Bing Li
public class StreamConfig
{
	public final static String PUBSUB_SERVER_NAME = "PubSubServer";
	public final static int PUBSUB_SERVER_PORT = 8900;
	
	public final static List<String> NO_SUBSCRIBERS = null;
	public final static Map<String, IPAddress> NO_IPS = null;
	public final static String TEST_DATA = "/home/libing/Temp/text_sample.txt";
	public final static long TIMEOUT = 2000;
	public final static String NO_SUBSCRIBER = UtilConfig.EMPTY_STRING;
	
	public final static String ONE_SUBSCRIBER_LEAVE = "\n**** One subscriber leaves ... ****\n";
}
