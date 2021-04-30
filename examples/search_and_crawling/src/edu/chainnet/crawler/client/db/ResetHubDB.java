package edu.chainnet.crawler.client.db;

import java.util.List;

import edu.chainnet.crawler.Hub;

// Created: 04/26/2021, Bing Li
class ResetHubDB
{
	public static void main(String[] args)
	{
		HubDB db = new HubDB(DBConfig.HUB_DB_PATH);
		List<Hub> hubs = db.loadRawHubs();
		System.out.println("Hubs are being reset: " + hubs.size());
		db.saveHubs(hubs, false);
		System.out.println("Hubs are reset: " + hubs.size());
		db.dispose();
	}
}

