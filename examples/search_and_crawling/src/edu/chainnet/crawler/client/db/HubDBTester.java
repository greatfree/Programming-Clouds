package edu.chainnet.crawler.client.db;

import java.util.List;

import edu.chainnet.crawler.Hub;

// Created: 04/24/2021, Bing Li
class HubDBTester
{
	public static void main(String[] args)
	{
		HubDB db = new HubDB(DBConfig.HUB_DB_PATH);
		List<Hub> hubs = db.loadRawHubs();
		System.out.println("hubs size = " + hubs.size());
		int index = 0;
		for (Hub entry : hubs)
		{
			System.out.println(++index + ") " + entry.getHubURL() + ", " + entry.getHubTitle());
		}
		db.dispose();
	}
}
