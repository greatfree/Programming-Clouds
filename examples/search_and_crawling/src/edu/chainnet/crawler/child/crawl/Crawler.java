package edu.chainnet.crawler.child.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import edu.chainnet.crawler.AuthoritySolrValue;
import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.CurrentLinkKeys;
import edu.chainnet.crawler.HubSource;
import edu.chainnet.crawler.LinkKeySolrHandle;

// Created: 04/24/2021, Bing Li
class Crawler
{
	public static void crawl(HubSource hub, int timeout) throws IOException, InstantiationException, IllegalAccessException
	{
		CurrentLinkKeys currentKeys = CrawlConfig.NO_CURRENT_LINK_KEYS;
		if (!hub.getHash().equals(CrawlConfig.NO_HASH))
		{
			if (hub.getLinkKeys() != CrawlConfig.NO_LINK_KEYS)
			{
				currentKeys = getLinks(hub, hub.getLinkKeys(), timeout);
				if (currentKeys != CrawlConfig.NO_CURRENT_LINK_KEYS)
				{
					HubRegistry.WWW().updateLastModified(hub.getHubKey(), Calendar.getInstance().getTime());
					HubRegistry.WWW().decrementPeriod(hub.getHubKey());
					HubRegistry.WWW().setLinkKeys(hub.getHubKey(), currentKeys.getCurrentKeys());
					HubRegistry.WWW().setHash(hub.getHubKey(), currentKeys.getPageHash());
					HubRegistry.WWW().clearPassedTime(hub.getHubKey());
				}
				else
				{
					HubRegistry.WWW().incrementTime(hub.getHubKey());
				}
			}
			else
			{
				currentKeys = getLinks(hub, timeout);
				if (currentKeys != CrawlConfig.NO_CURRENT_LINK_KEYS)
				{
					HubRegistry.WWW().updateLastModified(hub.getHubKey(), Calendar.getInstance().getTime());
					HubRegistry.WWW().decrementPeriod(hub.getHubKey());
					HubRegistry.WWW().setLinkKeys(hub.getHubKey(), currentKeys.getCurrentKeys());
					HubRegistry.WWW().setHash(hub.getHubKey(), currentKeys.getPageHash());
					HubRegistry.WWW().clearPassedTime(hub.getHubKey());
				}
				else
				{
					HubRegistry.WWW().incrementTime(hub.getHubKey());
				}
			}
		}
		else
		{
			currentKeys = getLinks(hub, timeout);
			if (currentKeys != CrawlConfig.NO_CURRENT_LINK_KEYS)
			{
				HubRegistry.WWW().updateLastModified(hub.getHubKey(), Calendar.getInstance().getTime());
				HubRegistry.WWW().decrementPeriod(hub.getHubKey());
				HubRegistry.WWW().initHash(hub.getHubKey(), currentKeys.getCurrentKeys(), currentKeys.getPageHash());
				HubRegistry.WWW().clearPassedTime(hub.getHubKey());
			}
			else
			{
				HubRegistry.WWW().incrementTime(hub.getHubKey());
			}
		}
		CrawlStates.CRAWL().done(hub.getHubKey());
	}

	private static CurrentLinkKeys getLinks(HubSource hub, int timeout) throws IOException, InstantiationException, IllegalAccessException
	{
		CrawlMonitor.MILNER().incrementStuck();
		CurrentLinkKeys currentKeys = URLAccessor.getNewLinks(hub.getHubKey(), hub.getHubURL(), timeout);
		CrawlMonitor.MILNER().decrementStuck();
		if (currentKeys != CrawlConfig.NO_CURRENT_LINK_KEYS)
		{
			List<AuthoritySolrValue> authorityURLs = new ArrayList<AuthoritySolrValue>();
			for (LinkKeySolrHandle entry : currentKeys.getNewLinkKeys())
			{
				if (!CrawledPageFilter.FREE().isTrashAuthorityTitle(entry.LinkText))
				{
//					authorityURLs.add(new AuthoritySolrValue(linkKeySolr.LinkKey, CrawledPageFilter.FREE().polishURL(linkKeySolr.LinkURL), CrawledPageFilter.FREE().polishTitle(linkKeySolr.LinkText), Calendar.getInstance().getTime(), hub.getHubKey(), hub.getHubURL(), UtilConfig.EMPTY_STRING, CrawledPageFilter.FREE().polishURL(CrawlConfig.NO_IMAGE_URL)));
					authorityURLs.add(new AuthoritySolrValue(entry.LinkKey, CrawledPageFilter.FREE().polishURL(entry.LinkURL), CrawledPageFilter.FREE().polishTitle(entry.LinkText), Calendar.getInstance().getTime(), hub.getHubKey(), hub.getHubURL(), entry.Content, CrawledPageFilter.FREE().polishURL(CrawlConfig.NO_IMAGE_URL)));
				}
			}
			SolrAuthorityPersister.CRAWL().send(authorityURLs);
		}
		return currentKeys;
	}
	
	private static CurrentLinkKeys getLinks(HubSource hub, Set<String> oldLinkKeys, int timeout) throws IOException, InstantiationException, IllegalAccessException
	{
		CurrentLinkKeys currentKeys = URLAccessor.getNewLinks(hub.getHubKey(), hub.getHubURL(), hub.getHash(), oldLinkKeys, timeout);
		if (currentKeys != CrawlConfig.NO_CURRENT_LINK_KEYS)
		{
			List<AuthoritySolrValue> authorityURLs = new ArrayList<AuthoritySolrValue>();
			for (LinkKeySolrHandle entry : currentKeys.getNewLinkKeys())
			{
				if (!CrawledPageFilter.FREE().isTrashAuthorityTitle(entry.LinkText))
				{
//					authorityURLs.add(new AuthoritySolrValue(entry.LinkKey, CrawledPageFilter.FREE().polishURL(entry.LinkURL), CrawledPageFilter.FREE().polishTitle(entry.LinkText), Calendar.getInstance().getTime(), hub.getHubKey(), hub.getHubURL(), UtilConfig.EMPTY_STRING, CrawledPageFilter.FREE().polishURL(CrawlConfig.NO_IMAGE_URL)));
					authorityURLs.add(new AuthoritySolrValue(entry.LinkKey, CrawledPageFilter.FREE().polishURL(entry.LinkURL), CrawledPageFilter.FREE().polishTitle(entry.LinkText), Calendar.getInstance().getTime(), hub.getHubKey(), hub.getHubURL(), entry.Content, CrawledPageFilter.FREE().polishURL(CrawlConfig.NO_IMAGE_URL)));
				}
			}
			SolrAuthorityPersister.CRAWL().send(authorityURLs);
		}
		return currentKeys;
	}
}

