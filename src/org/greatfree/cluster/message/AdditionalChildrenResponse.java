package org.greatfree.cluster.message;

import java.util.Set;

import org.greatfree.message.ServerMessage;

// Created: 09/12/2020, Bing Li
public class AdditionalChildrenResponse extends ServerMessage
{
	private static final long serialVersionUID = -689676723663994419L;
	
	private Set<String> childrenKeys;

	public AdditionalChildrenResponse(Set<String> childrenKeys)
	{
		super(ClusterMessageType.ADDITIONAL_CHILDREN_RESPONSE);
		this.childrenKeys = childrenKeys;
	}

	public Set<String> getChildrenKeys()
	{
		return this.childrenKeys;
	}
}
