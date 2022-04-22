package org.greatfree.app.business.cs.multinode.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Created: 12/09/2017, Bing Li
public class Businesses
{
	/*
	 * Post merchandises to sell. 12/09/2017, Bing Li
	 */
	public static boolean postMerchandise(String vendorKey, Merchandise mc)
	{
		// Save the merchandise. 12/05/2017, Bing Li
		return MerchandiseDB.CS().saveMerchandise(vendorKey, mc);
	}

	/*
	 * Check merchandises for customers. 12/09/2017, Bing Li
	 */
	public static Map<String, Merchandise> checkMerchandise(String vendorKey)
	{
		return MerchandiseDB.CS().getMerchandise(vendorKey);
	}

	/*
	 * Check the current sales for customers. 12/22/2017, Bing Li
	 */
	public static Map<String, Map<String, Merchandise>> getSales()
	{
		return MerchandiseDB.CS().getMerchandises();
	}
	
	/*
	 * Put merchandises into the cart of the customer. 12/09/2017, Bing Li
	 */
	public static Map<String, Merchandise> putIntoCart(String customerKey, String vendorKey, String merchandiseKey, int count)
	{
		System.out.println("Businesses-putIntoCart(): Customer = " + getUserName(customerKey));
		System.out.println("Businesses-putIntoCart(): vendor = " + getUserName(vendorKey));
		System.out.println("Businesses-putIntoCart(): merchandiseKey = " + merchandiseKey);
		System.out.println("Businesses-putIntoCart(): merchandise = " + MerchandiseDB.CS().getMerchandise(vendorKey, merchandiseKey));
		// Save the merchandises into the cart. 12/06/2017, Bing Li
		CartDB.CS().putIntoCart(customerKey, vendorKey, merchandiseKey, count);
		// Save the merchandises to be bought in the cart. 12/10/2018, Bing Li
		PendingOrderDB.CS().saveOrder(vendorKey, customerKey, merchandiseKey, count);
		// Get merchandises' count. 12/06/2017, Bing Li 
		Map<String, Integer> mcCount = CartDB.CS().getFromCart(customerKey, vendorKey);
		// Get the updated merchandises. 12/06/2017, Bing Li
		return MerchandiseDB.CS().getMerchandise(vendorKey, mcCount);
	}

	/*
	 * Remove merchandises from the cart. 12/09/2017, Bing Li
	 */
	public synchronized static Map<String, Merchandise> removeFromCart(String customerKey, String vendorKey, String merchandiseKey, int count)
	{
		// Remove the merchandises from the cart. 12/07/2017, Bing Li
		CartDB.CS().removeFromCart(customerKey, vendorKey, merchandiseKey, count);
		// Remove the merchandises from the pending orders of the vendor. 12/15/2017, Bing Li
		PendingOrderDB.CS().removeOrder(vendorKey, customerKey, merchandiseKey, count);
		// Get merchandises' count. 12/06/2017, Bing Li 
		Map<String, Integer> mcCount = CartDB.CS().getFromCart(customerKey, vendorKey);
		// Get the updated merchandises. 12/06/2017, Bing Li
		return MerchandiseDB.CS().getMerchandise(vendorKey, mcCount);
	}

	/*
	 * Get all of the names of vendors. 12/22/2017, Bing Li
	 */
	public static Map<String, String> getVendorNames()
	{
		Set<String> vendorKeys = MerchandiseDB.CS().getVendorKeys();
		if (vendorKeys.size() > 0)
		{
			Map<String, String> names = new HashMap<String, String>();
			for (String key : vendorKeys)
			{
				names.put(key, getUserName(key));
			}
			return names;
		}
		return null;
	}

	/*
	 * Get the name of the customer. 12/09/2017, Bing Li
	 */
	private static String getUserName(String customerKey)
	{
		return AccountRegistry.CS().getAccount(customerKey).getUserName();
	}

	/*
	 * Get the merchandises in the cart of a customer. 12/09/2017, Bing Li
	 */
	public static Map<String, Merchandise> getMerchandises(String vendorKey, String customerKey)
	{
		return MerchandiseDB.CS().getMerchandise(vendorKey, CartDB.CS().getFromCart(customerKey, vendorKey));
	}

	/*
	 * Calculate the payment of one order. 12/09/2017, Bing Li
	 */
	private static float getPayment(String vendorKey, String customerKey)
	{
		float payment = -1;
		Merchandise mc;
		Map<String, Integer> mcInCart = CartDB.CS().getFromCart(customerKey, vendorKey);
		if (mcInCart != null)
		{
			for (Map.Entry<String, Integer> entry : mcInCart.entrySet())
			{
				mc = MerchandiseDB.CS().getMerchandise(vendorKey, entry.getKey());
				payment += mc.getPrice() * entry.getValue();
			}
		}
		return payment;
	}

	/*
	 * Place an order. 12/15/2017, Bing Li
	 */
	public static void placeOrder(String vendorKey, String customerKey)
	{
		float payment = getPayment(vendorKey, customerKey);
		if (payment > 0)
		{
			Map<String, Merchandise> mcs = getMerchandises(vendorKey, customerKey);
			DoneTransactionDB.CS().saveTransaction(vendorKey, getUserName(vendorKey), customerKey, getUserName(customerKey), mcs, payment);
		}
	}

	/*
	 * Check the pending orders for one vendor. 12/15/2017, Bing Li
	 */
	public static Map<String, Map<String, Merchandise>> getMerchandises(String vendorKey)
	{
		Map<String, Map<String, Integer>> pendingCounts = PendingOrderDB.CS().getOrders(vendorKey);
		if (pendingCounts != null)
		{
			Map<String, Map<String, Merchandise>> mcs = new HashMap<String, Map<String, Merchandise>>();
			for (Map.Entry<String, Map<String, Integer>> entry : pendingCounts.entrySet())
			{
				mcs.put(entry.getKey(), MerchandiseDB.CS().getMerchandise(vendorKey, entry.getValue()));
			}
			return mcs;
		}
		return null;
	}

	/*
	 * Get the customers' names for one vendor. 12/15/2017, Bing Li
	 */
	public static Map<String, String> getCustomerNames(String vendorKey)
	{
		Map<String, Map<String, Integer>> pendingCounts = PendingOrderDB.CS().getOrders(vendorKey);
		if (pendingCounts != null)
		{
			Map<String, String> names = new HashMap<String, String>();
			for (Map.Entry<String, Map<String, Integer>> entry : pendingCounts.entrySet())
			{
				names.put(entry.getKey(), getUserName(entry.getKey()));
			}
			return names;
		}
		return null;
	}

	/*
	 * Get the transactions of a vendor. 12/20/2017, Bing Li
	 */
	public static Map<String, MyTransaction> getVendorTransactions(String vendorKey)
	{
		return DoneTransactionDB.CS().getTransactionsOfVendor(vendorKey);
	}

	/*
	 * Get the transactions of a customer. 12/20/2017, Bing Li
	 */
	public static Map<String, MyTransaction> getCustomerTransactions(String customerKey)
	{
		return DoneTransactionDB.CS().getTransactionsOfCustomer(customerKey);
	}
}
