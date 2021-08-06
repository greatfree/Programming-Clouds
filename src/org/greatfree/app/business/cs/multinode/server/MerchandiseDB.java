package org.greatfree.app.business.cs.multinode.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
 * This is a contrived DB to keep merchandises in memory/. 12/05/2017, Bing Li
 */

// Created: 12/05/2017, Bing Li
class MerchandiseDB
{
	// The structure contains the keys, i.e., the vendorKey and the merchandiseKey. 12/15/2017, Bing Li
	private Map<String, Map<String, Merchandise>> mcs;
	
	private MerchandiseDB()
	{
		// Define a concurrent map for the consideration of consistency to save merchandises. 12/04/2017, Bing Li
		this.mcs = new ConcurrentHashMap<String, Map<String, Merchandise>>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static MerchandiseDB instance = new MerchandiseDB();
	
	public static MerchandiseDB CS()
	{
		if (instance == null)
		{
			instance = new MerchandiseDB();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the DB. 12/05/2017, Bing Li
	 */
	public void dispose()
	{
		this.mcs.clear();
		this.mcs = null;
	}

	/*
	 * Save the merchandise in the DB. 12/05/2017, Bing Li
	 */
	public boolean saveMerchandise(String vendorKey, Merchandise mc)
	{
		if (!this.mcs.containsKey(vendorKey))
		{
			System.out.println("MerchandiseDB-saveMerchandise(): vendorKey = " + vendorKey);
			this.mcs.put(vendorKey, new HashMap<String, Merchandise>());
			System.out.println("MerchandiseDB-saveMerchandise(): mcKey = " + mc.getKey());
			this.mcs.get(vendorKey).put(mc.getKey(), mc);
		}
		else if (!this.mcs.get(vendorKey).containsKey(mc.getKey()))
		{
			this.mcs.get(vendorKey).put(mc.getKey(), mc);
		}
		else
		{
			Merchandise existingMC = this.mcs.get(vendorKey).get(mc.getKey());
			mc.setCount(existingMC.getCount() + mc.getCount());
			this.mcs.get(vendorKey).put(mc.getKey(), mc);
		}
		return true;
	}

	/*
	 * Get all of the merchandises. 12/05/2017, Bing Li
	 */
	public Map<String, Merchandise> getMerchandise(String vendorKey)
	{
		if (this.mcs.containsKey(vendorKey))
		{
			return this.mcs.get(vendorKey);
		}
		return null;
	}

	/*
	 * Get the merchandise from the DB. 12/05/2017, Bing Li
	 */
	public Merchandise getMerchandise(String vendorKey, String key)
	{
		System.out.println("1) MerchandiseDB-getMerchandise(): vendorKey = " + vendorKey);
		System.out.println("1) MerchandiseDB-getMerchandise(): mcKey = " + key);
		if (this.mcs.containsKey(vendorKey))
		{
			System.out.println("2) MerchandiseDB-getMerchandise(): vendorKey = " + vendorKey);
			System.out.println("2) MerchandiseDB-getMerchandise(): mcKey = " + key);
			if (this.mcs.get(vendorKey).containsKey(key))
			{
				System.out.println("3) MerchandiseDB-getMerchandise(): vendorKey = " + vendorKey);
				System.out.println("3) MerchandiseDB-getMerchandise(): mcKey = " + key);
				return this.mcs.get(vendorKey).get(key);
			}
		}
		return null;
	}
	
	/*
	 * Get the merchandise from the DB. 12/05/2017, Bing Li
	 */
	public Map<String, Merchandise> getMerchandise(String vendorKey, Map<String, Integer> mcCount)
	{
		if (mcCount != null)
		{
			Map<String, Merchandise> mcs = new HashMap<String, Merchandise>();
			Merchandise mc;
			Merchandise mcInCart;
			for (Map.Entry<String, Integer> entry : mcCount.entrySet())
			{
				mc = MerchandiseDB.CS().getMerchandise(vendorKey, entry.getKey());
				mcInCart = new Merchandise(mc.getKey(), mc.getName(), mc.getPrice(), entry.getValue());
				mcs.put(entry.getKey(), mcInCart);
			}
			return mcs;
		}
		return null;
	}

	/*
	 * Get all of the current merchandises. 12/22/2017, Bing Li
	 */
	public Map<String, Map<String, Merchandise>> getMerchandises()
	{
		return this.mcs;
	}

	/*
	 * Get all of the keys of vendors. 12/22/2017, Bing Li
	 */
	public Set<String> getVendorKeys()
	{
		return this.mcs.keySet();
	}
}
