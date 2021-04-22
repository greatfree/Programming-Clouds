package org.greatfree.framework.streaming.message;

// Created: 03/18/2020, Bing Li
public class StreamMessageType
{
	public final static int ADD_STREAM_NOTIFICATION = 10000;
	public final static int REMOVE_STREAM_NOTIFICATION = 10001;
	
	public final static int SUBSCRIBE_STREAM_REQUEST = 10002;
	public final static int SUBSCRIBE_STREAM_RESPONSE = 10003;
	
	public final static int UNSUBSCRIBE_STREAM_NOTIFICATION = 10004;
	
	public final static int STREAM_NOTIFICATION = 10005;
	
	public final static int SHUTDOWN_PUBSUB_NOTIFICATION = 10006;
	
	public final static int SUBSCRIBERS_REQUEST = 10008;
	public final static int SUBSCRIBERS_RESPONSE = 10009;
	
	public final static int STREAM_REQUEST = 10010;
	public final static int STREAM_RESPONSE = 10011;
	
	public final static int PUBLISH_STREAM_NOTIFICATION = 10012;
	
	public final static int SEARCH_REQUEST = 10013;
	public final static int SEARCH_RESPONSE = 10014;
	
	public final static int SUBSCRIBE_NOTIFICATION = 10015;
	public final static int UNSUBSCRIBE_NOTIFICATION = 10016;
	
	public final static int SUBSCRIBER_REQUEST = 10017;
	public final static int SUBSCRIBER_RESPONSE = 10018;
}
