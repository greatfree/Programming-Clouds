package org.greatfree.framework.threading.mrtc;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.Rand;
import org.greatfree.util.Tools;

import com.google.common.collect.Sets;

// Created: 10/01/2019, Bing Li
public class NodeIDs
{
	private String localName;
	private String localKey;
	private Set<String> allSlaveKeys;
//	private Map<String, String> allSlaveKeys;
	private Map<String, Set<String>> allThreadKeys;
	private Map<String, String> allSlaveNames;
	
	private NodeIDs()
	{
		this.localName = MRConfig.DT_PREFIX + Rand.getRandomUpperString(MRConfig.ID_LENGTH);
		this.localKey = Tools.getHash(this.localName);
		this.allThreadKeys = new ConcurrentHashMap<String, Set<String>>();
		this.allSlaveNames = new ConcurrentHashMap<String, String>();
	}
	
	private static NodeIDs instance = new NodeIDs();
	
	public static NodeIDs ID()
	{
		if (instance == null)
		{
			instance = new NodeIDs();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public String getLocalName()
	{
		return this.localName;
	}
	
	public String getLocalKey()
	{
		return this.localKey;
	}

	public void setAllSlaveIDs(Set<String> allIDs)
	{
		this.allSlaveKeys = allIDs;
	}
	
	public void addSlaveName(String key, String name)
	{
		this.allSlaveNames.put(key, name);
	}
	
	public Map<String, String> getAllSlaveNames()
	{
		return this.allSlaveNames;
	}
	
	public void setSlaveNames(Map<String, String> names)
	{
		this.allSlaveNames = names;
	}
	
	public String getSlaveName(String slaveKey)
	{
		return this.allSlaveNames.get(slaveKey);
	}

	/*
	 * Select one slave randomly as the rendezvous point (RP) from the slaves. 01/08/2020, Bing Li
	 */
	public String getRandomSlaveKey()
	{
		return Rand.getRandomSetElementExcept(this.allSlaveKeys, this.localKey);
	}
	
	public Set<String> getAllSlaves()
	{
		return this.allSlaveKeys;
	}

	/*
	 * As a common sense, the minimum number of slaves for a concurrency game is 2. Select the random number, which should be NOT less than 2, of slaves from the available slaves. 01/08/2020, Bing Li
	 */
	public Set<String> getInitMRSlaves()
	{
		if (this.allSlaveKeys.size() <= MRConfig.MINIMUM_MUTLI_SLAVE_SIZE)
		{
			return Sets.newHashSet(this.allSlaveKeys);
		}
//		return Rand.getRandomSet(this.allSlaveKeys, Rand.getRandom(this.allSlaveKeys.size()) + MRConfig.MINIMUM_MUTLI_SLAVE_SIZE);
		Set<String> keys = Sets.newHashSet();
		do
		{
			keys.addAll(Rand.getRandomSet(this.allSlaveKeys, Rand.getRandom(this.allSlaveKeys.size())));
		}
		while (keys.size() < MRConfig.MINIMUM_MUTLI_SLAVE_SIZE);
		return keys;
	}
	
	public Set<String> getSlavesExceptLocal()
	{
		Set<String> keys = Sets.newHashSet(this.allSlaveKeys);
		if (this.allSlaveKeys.size() <= MRConfig.MINIMUM_MUTLI_SLAVE_SIZE)
		{
			keys.remove(this.localKey);
			return keys;
		}
//		return Rand.getRandomSetElementExcept(this.allSlaveKeys, this.localKey);
//		Set<String> keys = Rand.getRandomSet(this.allSlaveKeys, Rand.getRandom(this.allSlaveKeys.size()) + MRConfig.MINIMUM_MUTLI_SLAVE_SIZE);
		keys.remove(this.localKey);
		return Rand.getRandomSet(keys, Rand.getRandom(keys.size()));
	}
	
	public String getSlaveExceptFrom(Set<String> slaveKeys)
	{
//		return Rand.getRandomStringInSet(slaveKeys);
		return Rand.getRandomSetElementExcept(this.allSlaveKeys, slaveKeys);
	}
	
	public int getSlaveSize()
	{
		return this.allSlaveKeys.size();
	}
	
	public void setThreadKeys(Map<String, Set<String>> allThreadKeys)
	{
		this.allThreadKeys = allThreadKeys;
	}
	
	public Map<String, Set<String>> getAllThreadKeys()
	{
		return this.allThreadKeys;
	}
	
	public String getThreadKey(String slaveKey)
	{
		return Rand.getRandomStringInSet(this.allThreadKeys.get(slaveKey));
	}
	
	public int getThreadSize(String slaveKey)
	{
		return this.allThreadKeys.get(slaveKey).size();
	}
	
	public Set<String> getThreadKeys(String slaveKey, int size)
	{
		Set<String> keys = Sets.newHashSet();
		if (this.allThreadKeys.get(slaveKey).size() > size)
		{
			for (String entry : this.allThreadKeys.get(slaveKey))
			{
				keys.add(entry);
				if (keys.size() >= size)
				{
					return keys;
				}
			}
		}
		return this.allThreadKeys.get(slaveKey);
	}
}
