package edu.chainnet.crawler;

import java.io.Serializable;
import java.util.Date;

import org.greatfree.util.UtilConfig;

/**
 * 
 * @author libing
 * 
 * Solr is not in the version. But the class is still named by Solr. Later, I will update it. 04/24/2021, Bing Li
 *
 */

// Created: 04/24/2021, Bing Li
public class AuthoritySolrValue implements Serializable
{
	private static final long serialVersionUID = 7898935680536020340L;
	
	public String authorityKey;
	public String url;
	public String title;
	public Date time;
	public String hostHubKey;
	public String hostHubURL;
	public String content;
	public String imageURL;

	public AuthoritySolrValue(String key, String url, String title, Date time, String hostHubKey, String hostHubURL, String content, String imageURL)
	{
		this.authorityKey = key;
		this.url = url;
		this.title = title;
		this.time = time;
		this.hostHubKey = hostHubKey;
		this.hostHubURL = hostHubURL;
		this.content = content;
		this.imageURL = imageURL;
	}
	
	public String toString()
	{
		return this.url + UtilConfig.BAR + this.title + UtilConfig.BAR + this.content + UtilConfig.BAR + this.hostHubURL;
	}
}

