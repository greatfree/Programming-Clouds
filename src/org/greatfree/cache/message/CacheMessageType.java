package org.greatfree.cache.message;

/*
 * The class defines the constants which represent the messages for distributed caches. 07/01/2017, Bing Li
 */

// Created: 07/01/2017, Bing Li
public class CacheMessageType
{
	public final static int PUT_NOTIFICATION = 301;
	
	public final static int UNI_GET_REQUEST = 302;
	public final static int UNI_GET_RESPONSE = 303;
	
//	public final static int PUT_ALL_NOTIFICATION = 304;
	
	public final static int BROAD_GET_REQUEST = 304;
	public final static int BROAD_GET_RESPONSE = 305;
	
	public final static int BROAD_SIZE_REQUEST = 306;
	public final static int BROAD_SIZE_RESPONSE = 307;
	
	public final static int BROAD_KEYS_REQUEST = 308;
	public final static int BROAD_KEYS_RESPONSE = 309;
	
	public final static int BROAD_VALUES_REQUEST = 310;
	public final static int BROAD_VALUES_RESPONSE = 311;
	
	public final static int REMOVE_KEYS_NOTIFICATION = 312;
	public final static int REMOVE_KEY_NOTIFICATION = 313;
	
	public final static int CLEAR_NOTIFICATION = 314;
	
	public final static int CLOSE_NOTIFICATION = 315;
	
	public final static int JOIN_MAP_NOTIFICATION = 316;
}
