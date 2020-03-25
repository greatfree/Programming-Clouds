package org.greatfree.app.business.dip.cs.multinode.server;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.util.Tools;

// Created: 12/15/2017, Bing Li
class DoneTransactionDB
{
	// The collection keeps the balances of vendors. 12/15/2017, Bing Li
	private Map<String, Float> balances;
	// The collection keeps the transactions of vendors. The keys represent the vendorKey and the transaction key. 12/15/2017, Bing Li
	private Map<String, Map<String, Transaction>> vendTransactions;
	private Map<String, Map<String, Transaction>> custTransactions;
	
	private DoneTransactionDB()
	{
		this.balances = new ConcurrentHashMap<String, Float>();
		this.vendTransactions = new ConcurrentHashMap<String, Map<String, Transaction>>();
		this.custTransactions = new ConcurrentHashMap<String, Map<String, Transaction>>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static DoneTransactionDB instance = new DoneTransactionDB();
	
	public static DoneTransactionDB CS()
	{
		if (instance == null)
		{
			instance = new DoneTransactionDB();
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
		this.balances.clear();
		this.balances = null;
		this.vendTransactions.clear();
		this.vendTransactions = null;
	}

	/*
	 * Add the balance. 12/15/2017, Bing Li
	 */
	private void addBalance(String vendorKey, float payment)
	{
		if (!this.balances.containsKey(vendorKey))
		{
			this.balances.put(vendorKey, payment);
		}
		else
		{
			this.balances.put(vendorKey, this.balances.get(vendorKey) + payment);
		}
	}

	/*
	 * Save the transaction. 12/15/2017, Bing Li
	 */
	public void saveTransaction(String vendorKey, String vendorName, String customerKey, String customerName, Map<String, Merchandise> mcs, float payment)
	{
		Transaction t = new Transaction(Tools.generateUniqueKey(), vendorName, customerName, mcs, payment, Calendar.getInstance().getTime());
		if (!this.vendTransactions.containsKey(vendorKey))
		{
			this.vendTransactions.put(vendorKey, new HashMap<String, Transaction>());
		}
		this.vendTransactions.get(vendorKey).put(t.getKey(), t);
		if (!this.custTransactions.containsKey(customerKey))
		{
			this.custTransactions.put(customerKey, new HashMap<String, Transaction>());
		}
		this.custTransactions.get(customerKey).put(t.getKey(), t);
		this.addBalance(vendorKey, payment);
	}

	/*
	 * Get the transactions of a vendor. 12/15/2017, Bing Li
	 */
	public Map<String, Transaction> getTransactionsOfVendor(String vendorKey)
	{
		return this.vendTransactions.get(vendorKey);
	}

	/*
	 * Get the transactions of a customer. 12/15/2017, Bing Li
	 */
	public Map<String, Transaction> getTransactionsOfCustomer(String customerKey)
	{
		return this.custTransactions.get(customerKey);
	}

	/*
	 * Get the balance of the vendor. 12/20/2017, Bing Li
	 */
	public float getBalance(String vendorKey)
	{
		if (this.balances.containsKey(vendorKey))
		{
			return this.balances.get(vendorKey);
		}
		return BusinessConfig.NO_BALANCE;
	}
}
