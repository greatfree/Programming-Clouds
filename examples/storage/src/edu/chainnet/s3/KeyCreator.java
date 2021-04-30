package edu.chainnet.s3;

import org.greatfree.util.Tools;

/*
 * All of the keys to save data in caches or memory are created through the program. 07/12/2020, Bing Li
 */

// Created: 07/12/2020, Bing Li
public class KeyCreator
{
	public static String getServerID()
	{
		return Tools.generateUniqueKey();
	}
	
	public static String createSessionKey()
	{
		return Tools.generateUniqueKey();
	}
	
	public static String getFileNameKey(String fileName)
	{
		return Tools.getHash(fileName);
	}

	public static String getSliceStateKey(String sessionKey, int ebID, int position)
	{
		return Tools.getHash(sessionKey + ebID + position);
	}
}
