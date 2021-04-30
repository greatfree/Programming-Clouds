package edu.chainnet.crawler.child.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.util.Tools;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.CurrentLinkKeys;
import edu.chainnet.crawler.LinkKeySolrHandle;

// Created: 04/24/2021, Bing Li
class URLAccessor
{
	public static CurrentLinkKeys getNewLinks(String hubKey, String hubURL, String oldHash, Set<String> oldLinkKeys, int timeout) throws IOException
	{
		Document doc = JsoupTools.getDocIgnoreContentType(hubURL, timeout);
		if (doc != null)
		{
			String hash = Tools.getHash(doc.html());
			if (!hash.equals(oldHash))
			{
				List<LinkKeySolrHandle> newLinks = new ArrayList<LinkKeySolrHandle>();
				Elements links = doc.getElementsByTag(CrawlConfig.TAG_A);
				String linkHref;
				String linkKey;
				Map<String, String> urls = new HashMap<String, String>();
				for (Element link : links)
				{
					linkHref = link.attr(CrawlConfig.HREF);
					linkKey = Tools.getHash(linkHref);
					
					if (!oldLinkKeys.contains(linkKey))
					{
						newLinks.add(new LinkKeySolrHandle(linkHref, hubKey, link.text(), doc.body().text()));
						urls.put(linkKey, linkHref);
					}
				}
				return new CurrentLinkKeys(newLinks, hash, CrawlConfig.NO_IMAGE_LINKS);
			}
			else
			{
				return CrawlConfig.NO_CURRENT_LINK_KEYS;
			}
		}
		return CrawlConfig.NO_CURRENT_LINK_KEYS;
	}

	public static CurrentLinkKeys getNewLinks(String hubKey, String hubURL, int timeout) throws IOException
	{
		Document doc = JsoupTools.getDocIgnoreContentType(hubURL, timeout);
		if (doc != null)
		{
			String hash = Tools.getHash(doc.html());
			List<LinkKeySolrHandle> newLinks = new ArrayList<LinkKeySolrHandle>();
			Elements links = doc.getElementsByTag(CrawlConfig.TAG_A);
			String linkHref;
			Map<String, String> urls = new HashMap<String, String>();
			LinkKeySolrHandle solrHandle;
			for (Element link : links)
			{
				linkHref = link.attr(CrawlConfig.HREF);
				solrHandle = new LinkKeySolrHandle(linkHref, hubKey, link.text(), doc.body().text());
				newLinks.add(solrHandle);
				urls.put(solrHandle.LinkKey, linkHref);
			}
			return new CurrentLinkKeys(newLinks, hash, CrawlConfig.NO_IMAGE_LINKS);
		}
		return CrawlConfig.NO_CURRENT_LINK_KEYS;
	}
}
