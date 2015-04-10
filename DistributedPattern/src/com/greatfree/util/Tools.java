package com.greatfree.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Mac;

/*
 * The class contains some methods that provide other classes with some generic services. 07/30/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public class Tools
{
	/*
	 * Create a unique key. 07/30/2014, Bing Li
	 */
	public static String generateUniqueKey()
	{
		return UUID.randomUUID().toString();
	}

	/*
	 * Create a hash string upon the input string. 07/30/2014, Bing Li
	 */
	public static String getHash(String input)
	{
		try
		{
			byte[] keyBytes = UtilConfig.PRIVATE_KEY.getBytes();
			Key key = new SecretKeySpec(keyBytes, 0, keyBytes.length, UtilConfig.HMAC_MD5); 
			Mac mac = Mac.getInstance(UtilConfig.HMAC_MD5);
			mac.init(key); 
			return UtilConfig.A + byteArrayToHex(mac.doFinal(input.getBytes()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return UtilConfig.EMPTY_STRING;
		}
	}
	
	protected static String byteArrayToHex(byte [] a)
	{
		int hn, ln, cx;
		StringBuffer buf = new StringBuffer(a.length * 2);
		for(cx = 0; cx < a.length; cx++)
		{
			hn = ((int)(a[cx]) & 0x00ff) / 16;
			ln = ((int)(a[cx]) & 0x000f);
			buf.append(UtilConfig.HEX_DIGIT_CHARS.charAt(hn));
			buf.append(UtilConfig.HEX_DIGIT_CHARS.charAt(ln));
		}
		return buf.toString();
	}

	/*
	 * Create a hash string upon TCP Socket. 07/30/2014, Bing Li
	 */
	public static String getClientIPPortKey(Socket clientSocket)
	{
		return Tools.getHash(clientSocket.getRemoteSocketAddress().toString());
	}

	/*
	 * Get the IP address upon a Socket. 07/30/2014, Bing Li
	 */
	public static String getClientIPAddress(Socket clientSocket)
	{
		String ipAddress = clientSocket.getRemoteSocketAddress().toString();
		ipAddress = ipAddress.substring(0, ipAddress.indexOf(Symbols.COLON));
		ipAddress = ipAddress.substring(ipAddress.indexOf(Symbols.FORWARD_SLASH) + 1);
		return ipAddress;
	}

	/*
	 * Get the port number upon a Socket. 07/30/2014, Bing Li
	 */
	public static int getClientIPPort(Socket clientSocket)
	{
		String ipPort = clientSocket.getRemoteSocketAddress().toString();
		ipPort = ipPort.substring(ipPort.indexOf(Symbols.COLON) + 1);
		return (new Integer(ipPort)).intValue();
	}

	/*
	 * Create a hash string upon the IP address and the port number. 07/30/2014, Bing Li
	 */
	public static String getKeyOfFreeClient(String ip, int port)
	{
		return Tools.getHash(ip + port);
	}

	/*
	 * Estimate the byte size of an object. 11/25/2014, Bing Li
	 */
	public static long sizeOf(Object original) throws IOException
	{
		ObjectOutputStream oos;
		ByteArrayOutputStream baos;
		baos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(baos);
		oos.writeObject(original);
		oos.close();
		return baos.toByteArray().length;
	}

	/*
	 * Select the key from the set of keys that is most closed to the source key in terms of the their key distance. 11/28/2014, Bing Li
	 */
	public static String getClosestKey(String sourceKey, Set<String> keys)
	{
		// Initialize a collection. 11/28/2014, Bing Li
		Map<String, StringObj> keyMap = new HashMap<String, StringObj>();
		// Put the source key and its instance of StringObj into the collection. 11/28/2014, Bing Li
		keyMap.put(sourceKey, new StringObj(sourceKey));
		// Put each of the keys and its instance of StringObj  into the same collection. 11/28/2014, Bing Li
		for (String key : keys)
		{
			keyMap.put(key, new StringObj(key));
		}
		// Sort the collection in an ascendant order by their keys. 11/28/2014, Bing Li
		keyMap = CollectionSorter.sortByValue(keyMap);
		// Put the sorted keys into a list. 11/28/2014, Bing Li
		List<String> keyList = new LinkedList<String>(keyMap.keySet());
		// Get the index of the source key. 11/28/2014, Bing Li
		int index = keyList.indexOf(sourceKey);
		// Check whether the index is lower than the last one of the list, i.e., check whether the index of the source key is the last one of the list. 11/28/2014, Bing Li
		if (index < keyList.size() - 1)
		{
			// If the index of the source key is not the last one, then the one that is immediately greater than the source key is believed to be the one that is the most closed to the source key. 11/28/2014, Bing Li
			return keyList.get(index + 1);
		}
		else
		{
			// If the index of the source key is the last one, then the one that is immediately less than the source key is believed to be the one that is the most closed to the source key. 11/28/2014, Bing Li
			return keyList.get(index - 1);
		}
	}

	/*
	 * Select the key from the list of keys that is most closed to the source key in terms of the their key distance. 11/28/2014, Bing Li
	 */
	public static String getClosestKey(String sourceKey, List<String> keys)
	{
		// Initialize a collection. 11/28/2014, Bing Li
		Map<String, StringObj> keyMap = new HashMap<String, StringObj>();
		// Put the source key and its instance of StringObj into the collection. 11/28/2014, Bing Li
		keyMap.put(sourceKey, new StringObj(sourceKey));
		// Put each of the keys and its instance of StringObj  into the same collection. 11/28/2014, Bing Li
		for (String key : keys)
		{
			keyMap.put(key, new StringObj(key));
		}
		// Sort the collection in an ascendant order by their keys. 11/28/2014, Bing Li
		keyMap = CollectionSorter.sortByValue(keyMap);
		// Put the sorted keys into a list. 11/28/2014, Bing Li
		List<String> keyList = new LinkedList<String>(keyMap.keySet());
		// Get the index of the source key. 11/28/2014, Bing Li
		int index = keyList.indexOf(sourceKey);
		// Check whether the index is lower than the last one of the list, i.e., check whether the index of the source key is the last one of the list. 11/28/2014, Bing Li
		if (index < keyList.size() - 1)
		{
			// If the index of the source key is not the last one, then the one that is immediately greater than the source key is believed to be the one that is the most closed to the source key. 11/28/2014, Bing Li
			return keyList.get(index + 1);
		}
		else
		{
			// If the index of the source key is the last one, then the one that is immediately less than the source key is believed to be the one that is the most closed to the source key. 11/28/2014, Bing Li
			return keyList.get(index - 1);
		}
	}
}
