package com.greatfree.testing.coordinator.crawling;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.greatfree.concurrency.Consumable;
import com.greatfree.testing.data.Constants;
import com.greatfree.testing.data.URLValue;
import com.greatfree.testing.message.CrawlLoadNotification;
import com.greatfree.util.Tools;

/*
 * This is a procedure to send crawling load to a particular distributed crawler. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class CrawlLoadSender implements Consumable<CrawlLoad>
{
	@Override
	public void consume(CrawlLoad load)
	{
		try
		{
			// Initialize a notification to take the load. 11/25/2014, Bing Li
			CrawlLoadNotification notification = new CrawlLoadNotification(load.getDCKey(), true);
			// Get all of the load to be sent to the crawler. 11/25/2014, Bing Li
			HashMap<String, URLValue> urls = load.getURLs();
			// Initialize a collection to take the URLs. 11/25/2014, Bing Li
			Map<String, URLValue> assignedURLs = new HashMap<String, URLValue>();
			// Initialize the batchSize, the approximate size of the notification. It is the amount of the batch load dividing the size of a collection when it takes one URL. With the value, it is possible to estimate how many URLs should be added into a notification. It is the way to measure the size of a notification to send the load each time in an approximately balanced burden to the network. 11/25/2014, Bing Li
			double batchSize = 0;
			// Initialize the count which is to be added into a notification. 11/25/2014, Bing Li
			long loadedCount = 0;
			// Initialize whether it is time to estimate the batchSize. It becomes false when it is time to do that. 11/25/2014, Bing Li
			boolean isReady = false;
			// Add the URL load one by one to notifications. 11/25/2014, Bing Li
			for (URLValue url : urls.values())
			{
				// Check whether it is time to estimate the batchSize. 11/25/2014, Bing Li
				if (!isReady)
				{
					// Add one URL to the notification. 11/25/2014, Bing Li
					assignedURLs.put(url.getKey(), url);
					// Increment the load count. 11/25/2014, Bing Li
					loadedCount++;
					// Estimate the batchSize. 11/25/2014, Bing Li
					batchSize = Constants.MEMORY_BATCH_LOAD / Tools.sizeOf(assignedURLs);
					// Set the flag to true. It denotes that the it needs to add URLs to the load notification with the current batchSize. 11/25/2014, Bing Li
					isReady = true;
					// Since the current URL is added to the notification, it is time to add the next one. 11/25/2014, Bing Li
					continue;
				}

				// Add the URL load to the collection. 11/25/2014, Bing Li
				assignedURLs.put(url.getKey(), url);
				// Increment the load count. 11/25/2014, Bing Li
				loadedCount++;
				// Check whether the count of URLs loaded in the collection, loadedCount, exceeds the approximate size of the notification. 11/25/2014, Bing Li 
				if (loadedCount >= batchSize)
				{
					// If the load is large enough, put the URLs into the notification. 11/25/2014, Bing Li
					notification.setURLs(assignedURLs);
					// Send the notification to the crawler. 11/25/2014, Bing Li
					CrawlServerClientPool.COORDINATE().getPool().send(load.getDCKey(), notification);
					// Initialize another notification to send additional URLs. 11/25/2014, Bing Li
					notification = new CrawlLoadNotification(load.getDCKey(), false);
					// Initialize a new collection to take the additional URLs. 11/25/2014, Bing Li
					assignedURLs = new HashMap<String, URLValue>();
					// Reset the loadedCount. 11/25/2014, Bing Li
					loadedCount = 0;
					// It time to estimate the batchSize again. 11/25/2014, Bing Li
					isReady = false;
				}
			}
			// When the loop quits, put the rest URLs into the notification. 11/25/2014, Bing Li
			notification.setURLs(assignedURLs);
			// Set the flag that this is the last notification. 11/25/2014, Bing Li
			notification.setDone();
			// Send the last notification. 11/25/2014, Bing Li
			CrawlServerClientPool.COORDINATE().getPool().send(load.getDCKey(), notification);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
