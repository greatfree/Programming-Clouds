package edu.chainnet.crawler.message;

import java.util.List;

import org.greatfree.message.ServerMessage;

import edu.chainnet.crawler.AuthoritySolrValue;

// Created: 04/24/2021, Bing Li
public class CrawledAuthoritiesNotification extends ServerMessage
{
	private static final long serialVersionUID = -3048749695288674550L;

	private List<AuthoritySolrValue> authorities;

	public CrawledAuthoritiesNotification(List<AuthoritySolrValue> authorities)
	{
		super(CrawlingApplicationID.SOLR_AUTHORITIES_NOTIFICATION);
		this.authorities = authorities;
	}

	public List<AuthoritySolrValue> getAuthorities()
	{
		return this.authorities;
	}
}

