package edu.chainnet.crawler;

import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * Now the crawling does not use Solr. But the class is named in the same way as the previous version. After testing, it is recommended to fix the issue. 04/24/2021, Bing Li
 *
 */

// Created: 04/24/2021, Bing Li
public class LinkKeySolrHandle implements Comparable<LinkKeySolrHandle>
{
	public String LinkKey;
	
	public String HubURLKey;
	
	public String LinkURL;
	
	public String LinkText;
	
	public String Content;
	
	public LinkKeySolrHandle()
	{
	}
	
	public LinkKeySolrHandle(String linkURL, String hubURLKey, String linkText, String content)
	{
		this.LinkKey = Tools.getHash(linkURL);
		this.HubURLKey = hubURLKey;
		this.LinkURL = linkURL;
		this.LinkText = linkText;
		this.Content = content;
	}

	@Override
	public int compareTo(LinkKeySolrHandle remoteObject)
	{
		if (this.LinkKey.equals(remoteObject.LinkKey))
		{
			return 0;
		}
		else
		{
			return 1;
		}
	}

}
