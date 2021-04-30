package edu.chainnet.crawler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

// Created: 04/24/2021, Bing Li
public class CurrentLinkKeys
{
	private List<LinkKeySolrHandle> newLinkKeys;
	private Set<String> currentKeys;
	private String pageHash;
	private Map<String, String> linkImages;
	
	public CurrentLinkKeys(List<LinkKeySolrHandle> linkKeys, String pageHash, Map<String, String> linkImages)
	{
		this.newLinkKeys = linkKeys;
		this.currentKeys = Sets.newHashSet();
		for (LinkKeySolrHandle link : linkKeys)
		{
			this.currentKeys.add(link.LinkKey);
		}
		this.pageHash = pageHash;
		this.linkImages = linkImages;
	}
	
	public List<LinkKeySolrHandle> getNewLinkKeys()
	{
		return this.newLinkKeys;
	}
	
	public Set<String> getCurrentKeys()
	{
		return this.currentKeys;
	}
	
	public String getPageHash()
	{
		return this.pageHash;
	}
	
	public Map<String, String> getLinkImages()
	{
		return this.linkImages;
	}

}
