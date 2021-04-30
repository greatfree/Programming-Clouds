package edu.chainnet.sc.message;

import java.util.List;

import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

// Created: 10/18/2020, Bing Li
public class BCNode extends DSNode
{
	private static final long serialVersionUID = 7964160400007029823L;

	private List<String> partners;

	public BCNode(String name, IPAddress ip, String serviceName, List<String> partners, String description)
	{
		super(name, ip, serviceName, description);
		this.partners = partners;
	}
	
	public List<String> getPartners()
	{
		return this.partners;
	}
	
	public String toString()
	{
		String partners = UtilConfig.EMPTY_STRING;
		for (String entry : this.partners)
		{
			partners += entry;
		}
		return super.toString() + UtilConfig.NEW_LINE + partners;
	}
}
