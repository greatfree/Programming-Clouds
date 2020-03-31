package edu.greatfree.container.p2p.registry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

// Created: 01/16/2019, Bing Li
class Clusters
{
	private Map<String, Set<String>> clusterChildren;
	
	private Clusters()
	{
		this.clusterChildren = new ConcurrentHashMap<String, Set<String>>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static Clusters instance = new Clusters();

	public static Clusters SYSTEM()
	{
		if (instance == null)
		{
			instance = new Clusters();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose()
	{
		this.clusterChildren.clear();
		this.clusterChildren = null;
	}

	public void joinCluster(String rootKey, String childKey)
	{
//		System.out.println("Clusters-joinCluster(): rootKey = " + rootKey);
		if (!this.clusterChildren.containsKey(rootKey))
		{
			Set<String> children = Sets.newHashSet();
			this.clusterChildren.put(rootKey, children);
		}
		this.clusterChildren.get(rootKey).add(childKey);
	}
	
	public void leaveCluster(String rootKey, String childKey)
	{
		if (this.clusterChildren.containsKey(rootKey))
		{
			this.clusterChildren.get(rootKey).remove(childKey);
		}
	}
	
	public Set<String> getChildKeys(String rootKey)
	{
//		System.out.println("Clusters-getChildKeys(): rootKey = " + rootKey);
		if (this.clusterChildren.containsKey(rootKey))
		{
			return this.clusterChildren.get(rootKey);
		}
		return UtilConfig.NO_KEYS;
	}
}
