package org.greatfree.concurrency.threading;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.framework.threading.mrtc.MRConfig;
import org.greatfree.util.Rand;
import org.greatfree.util.Tools;

import com.google.common.collect.Sets;

// Created: 09/19/2019, Bing Li
class DistributerIDs
{
	private String nickName;
	private String nickKey;
	private Set<String> allSlaveKeys;
//	private Map<String, String> allSlaveKeys;
	private Map<String, Set<String>> allThreadKeys;
	private Map<String, String> allSlaveNames;
	
	private DistributerIDs()
	{
		this.nickName = MRConfig.DT_PREFIX + Rand.getRandomUpperString(MRConfig.ID_LENGTH);
		this.nickKey = Tools.getHash(this.nickName);
		this.allThreadKeys = new ConcurrentHashMap<String, Set<String>>();
		this.allSlaveNames = new ConcurrentHashMap<String, String>();
	}
	
	private static DistributerIDs instance = new DistributerIDs();
	
	public static DistributerIDs ID()
	{
		if (instance == null)
		{
			instance = new DistributerIDs();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public String getNickName()
	{
		return this.nickName;
	}
	
	public String getNickKey()
	{
		return this.nickKey;
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

	public String getRandomSlaveKey()
	{
		return Rand.getRandomSetElementExcept(this.allSlaveKeys, this.nickName);
	}
	
	public Set<String> getAllSlaves()
	{
		return this.allSlaveKeys;
	}
	
	public Set<String> getInitMRSlaves(int size)
	{
		if (this.allSlaveKeys.size() <= size)
		{
			return Sets.newHashSet(this.allSlaveKeys);
		}
//		return Rand.getRandomSet(this.allSlaveKeys, Rand.getRandom(this.allSlaveKeys.size()) + MRConfig.MINIMUM_MUTLI_SLAVE_SIZE);
		Set<String> keys = Sets.newHashSet();
		do
		{
			keys.addAll(Rand.getRandomSet(this.allSlaveKeys, Rand.getRandom(this.allSlaveKeys.size())));
		}
		while (keys.size() < size);
		return keys;
	}
	
	public Set<String> getSlavesExceptLocal(int size)
	{
		Set<String> keys = Sets.newHashSet(this.allSlaveKeys);
		if (this.allSlaveKeys.size() <= size)
		{
			keys.remove(this.nickKey);
			return keys;
		}
//		return Rand.getRandomSetElementExcept(this.allSlaveKeys, this.localKey);
//		Set<String> keys = Rand.getRandomSet(this.allSlaveKeys, Rand.getRandom(this.allSlaveKeys.size()) + MRConfig.MINIMUM_MUTLI_SLAVE_SIZE);
		keys.remove(this.nickKey);
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
	
	public Set<String> getThreadKeys(String slaveKey)
	{
		return this.allThreadKeys.get(slaveKey);
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
