package org.greatfree.framework.container.p2p.message;

import java.util.Set;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Request;

// Created: 09/13/2019, Bing Li
public class PartnersRequest extends Request
{
	private static final long serialVersionUID = 8943791071582921443L;
	
	private Set<String> partnerKeys;

	public PartnersRequest(Set<String> partnerKeys)
	{
		super(SystemMessageType.PARTNERS_REQUEST);
		this.partnerKeys = partnerKeys;
	}

	public Set<String> getPartnerKeys()
	{
		return this.partnerKeys;
	}
}
