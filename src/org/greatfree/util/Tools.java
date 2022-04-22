package org.greatfree.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Mac;

/*
 * 
 * The code is updated today. The project, the N3W, has not been updated for more than eight months! 04/10/2021, Bing Li
 * 
 * The class contains some methods that provide other classes with some generic services. 07/30/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public final class Tools
{
	private Tools()
	{
	}

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

	private static String byteArrayToHex(byte[] a)
	{
		int hn, ln, cx;
		StringBuffer buf = new StringBuffer(a.length * 2);
		for (cx = 0; cx < a.length; cx++)
		{
			hn = ((int) (a[cx]) & 0x00ff) / 16;
			ln = ((int) (a[cx]) & 0x000f);
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
		// return (new Integer(ipPort)).intValue();
		return Integer.valueOf(ipPort);
	}

	/*
	 * Get the local IP address of the current node. 05/01/2017, Bing Li
	 */
	public static String getLocalIP() throws SocketException
	{
		// return InetAddress.getLocalHost().getHostAddress();
		Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
		String address;
		NetworkInterface e;
		Enumeration<InetAddress> a;
		InetAddress addr;
		for (; n.hasMoreElements();)
		{
			e = n.nextElement();
			a = e.getInetAddresses();
			for (; a.hasMoreElements();)
			{
				addr = a.nextElement();
				address = addr.getHostAddress();
				if (!address.equals(UtilConfig.LOCAL_IP) && address.indexOf(UtilConfig.COLON) < 0)
				{
					return address;
				}
			}
		}
		return UtilConfig.NO_IP;
	}

	public static IPAddress getLocalIP(int port) throws SocketException
	{
		return new IPAddress(getLocalIP(), port);
	}

	/*
	 * Create a hash string upon the IP address and the port number. 07/30/2014,
	 * Bing Li
	 */
	public static String getKeyOfFreeClient(String ip, int port)
	{
		return Tools.getHash(ip + port);
	}

	/*
	 * Create a hash string upon the IP address. 07/30/2014, Bing Li
	 */
	public static String getKeyOfFreeClient(String ip)
	{
		return Tools.getHash(ip);
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
	 * Select the key from the set of keys that is most closed to the source key in
	 * terms of the their key distance. 11/28/2014, Bing Li
	 */
	public static String getClosestKey(String sourceKey, Set<String> keys)
	{
		// Initialize a collection. 11/28/2014, Bing Li
		Map<String, StringObj> keyMap = new HashMap<String, StringObj>();
		// Put the source key and its instance of StringObj into the collection.
		// 11/28/2014, Bing Li
		keyMap.put(sourceKey, new StringObj(sourceKey));
		// Put each of the keys and its instance of StringObj into the same collection.
		// 11/28/2014, Bing Li
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
		// Check whether the index is lower than the last one of the list, i.e., check
		// whether the index of the source key is the last one of the list. 11/28/2014,
		// Bing Li
		if (index < keyList.size() - 1)
		{
			// If the index of the source key is not the last one, then the one that is
			// immediately greater than the source key is believed to be the one that is the
			// most closed to the source key. 11/28/2014, Bing Li
			return keyList.get(index + 1);
		}
		else
		{
			// If the index of the source key is the last one, then the one that is
			// immediately less than the source key is believed to be the one that is the
			// most closed to the source key. 11/28/2014, Bing Li
			return keyList.get(index - 1);
		}
	}

	/*
	 * Select the key from the list of keys that is most closed to the source key in
	 * terms of the their key distance. 11/28/2014, Bing Li
	 */
	public static String getClosestKey(String sourceKey, List<String> keys)
	{
		// Initialize a collection. 11/28/2014, Bing Li
		Map<String, StringObj> keyMap = new HashMap<String, StringObj>();
		// Put the source key and its instance of StringObj into the collection.
		// 11/28/2014, Bing Li
		keyMap.put(sourceKey, new StringObj(sourceKey));
		// Put each of the keys and its instance of StringObj into the same collection.
		// 11/28/2014, Bing Li
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
		// Check whether the index is lower than the last one of the list, i.e., check
		// whether the index of the source key is the last one of the list. 11/28/2014,
		// Bing Li
		if (index < keyList.size() - 1)
		{
			// If the index of the source key is not the last one, then the one that is
			// immediately greater than the source key is believed to be the one that is the
			// most closed to the source key. 11/28/2014, Bing Li
			return keyList.get(index + 1);
		}
		else
		{
			// If the index of the source key is the last one, then the one that is
			// immediately less than the source key is believed to be the one that is the
			// most closed to the source key. 11/28/2014, Bing Li
			return keyList.get(index - 1);
		}
	}

	/*
	 * Get the random key from a set. 11/25/2016, Bing Li
	 */
	public static String getRandomSetElement(Set<String> set)
	{
		if (set.size() > 0)
		{
			int size = set.size();
			int item = new Random().nextInt(size);

			int i = 0;
			for (String obj : set)
			{
				if (i == item)
				{
					return obj;
				}
				i++;
			}
		}
		return UtilConfig.EMPTY_STRING;
	}

	/*
	 * Get the random key from a list. 11/25/2016, Bing Li
	 */
	public static String getRandomListElement(List<String> list)
	{
		if (list.size() > 0)
		{
			int size = list.size();
			int item = new Random().nextInt(size);

			int i = 0;
			for (String obj : list)
			{
				if (i == item)
				{
					return obj;
				}
				i++;
			}
		}
		return UtilConfig.EMPTY_STRING;
	}

	/*
	 * Get the random key from a list but the specified one must be excluded.
	 * 11/25/2016, Bing Li
	 */
	public static String getRandomSetElementExcept(Set<String> set, String elementKey)
	{
		// The reason to initialize a new set to keep the set is to avoid updating the
		// input set. Otherwise, it is possible to affect other code. 08/01/2015, Bing
		// Li
		Set<String> backupSet = Sets.newHashSet();
		backupSet.addAll(set);
		backupSet.remove(elementKey);
		return getRandomSetElement(backupSet);
	}

	/*
	 * Get the random key from a list but the specified ones must be excluded.
	 * 11/25/2016, Bing Li
	 */
	public static String getRandomSetElementExcept(Set<String> set, Set<String> elementKeys)
	{
		// The reason to initialize a new set to keep the set is to avoid updating the
		// input set. Otherwise, it is possible to affect other code. 08/01/2015, Bing
		// Li
		Set<String> backupSet = Sets.newHashSet();
		backupSet.addAll(set);
		return getRandomSetElement(Sets.difference(backupSet, elementKeys));
	}

	/*
	 * Convert a parent list to one list which contains the subclass. 08/26/2018,
	 * Bing Li
	 */
	public static <T> List<T> filter(List<?> listA, Class<T> c)
	{
		List<T> listB = new ArrayList<T>();
		for (Object a : listA)
		{
			if (c.isInstance(a))
			{
				listB.add(c.cast(a));
			}
		}
		return listB;
	}

	/*
	 * Convert a parent list to one list which contains the subclass. 08/26/2018,
	 * Bing Li
	 */
	public static <T> Map<Integer, T> filter(Map<Integer, ?> listA, Class<T> c)
	{
		Map<Integer, T> listB = new HashMap<Integer, T>();
		for (int i = 0; i < listA.size(); i++)
		{
			if (c.isInstance(listA.get(i)))
			{
				listB.put(i, c.cast(listA.get(i)));
			}
		}
		return listB;
	}

	public static byte[] serialize(Object obj) throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(out);
		os.writeObject(obj);
		return out.toByteArray();
	}

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException, EOFException
	{
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is = new ObjectInputStream(in);
		return is.readObject();
	}
}
