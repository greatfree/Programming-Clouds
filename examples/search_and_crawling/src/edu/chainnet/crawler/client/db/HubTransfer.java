package edu.chainnet.crawler.client.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.greatfree.util.Tools;

import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.Hub;

// Created: 04/24/2021, Bing Li
class HubTransfer
{
	public static void main(String[] args)
	{
		File file = new File(DBConfig.RAW_HUBS_PATH);
		String line;
		HubDB db = new HubDB(DBConfig.HUB_DB_PATH);
		try (BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			int index;
			String url;
			String title;
//			int updatingPeriod;
			int seq = 0;
			while ((line = br.readLine()) != null)
			{
				index = line.indexOf(CrawlConfig.BAR);
				url = line.substring(0, index);
				line = line.substring(index + 1);
				index = line.indexOf(CrawlConfig.BAR);
				title = line.substring(0, index);
				line = line.substring(index + 1);
//				index = line.indexOf(CrawlConfig.RETURN);
//				updatingPeriod = new Integer(line.substring(index));
//				updatingPeriod = new Integer(line);
//				System.out.println(url + ", " + title + ", " + updatingPeriod);
				System.out.println(++seq + ") " + url + ", " + title);
				db.saveHub(new Hub(Tools.getHash(url), url, title, 0), false);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		db.dispose();
	}
}

