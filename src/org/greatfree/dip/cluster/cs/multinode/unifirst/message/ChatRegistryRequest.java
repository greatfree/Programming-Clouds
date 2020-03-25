package org.greatfree.dip.cluster.cs.multinode.unifirst.message;

import org.greatfree.dip.cluster.cs.multinode.wurb.message.ChatApplicationID;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Request;

// Created: 02/15/2019, Bing Li
public class ChatRegistryRequest extends Request
{
	private static final long serialVersionUID = 9027572619056937516L;

	// The user key of the account. 04/15/2017, Bing Li
	private String userKey;
	// The user name of the account. 04/15/2017, Bing Li
	private String userName;
	// The description about the user. 04/16/2017, Bing Li
	private String description;

	public ChatRegistryRequest(String userKey, String userName, String description)
	{
		/*
		 * 02/24/2019, Bing Li
		 * 
		 * The previous updates to the messages, ChatRegistryRequest/ChatRegistryResponse and ChatPartnerRequest/ChatPartnerResponse are not correct.

		ChatRegistryRequest/ChatRegistryResponse needs to be broadcast such that each child has all of the users' information.
		
		ChatPartnerRequest/ChatPartnerResponse is restored to be performed in the way of 1st principle.
		
		In short, the original solution is restored.
		
		To optimize further, the only issue is whether ChatRegistryRequest/ChatRegistryResponse can be performed in the way of unicasting, i.e., the 1st principle or the 2nd one.
		
		To do that, it is necessary to take into account intercasting, i.e., the 3rd principle.
		 */
		
		// To save resources, the broadcasting can be changed to unicasting. So when searching one registered user, it needs to perform the partner-unicasting approach. 02/24/2019, Bing Li
//		super(userKey, MulticastMessageType.UNICAST_REQUEST, ChatApplicationID.CHAT_REGISTRY_REQUEST);
		super(MulticastMessageType.BROADCAST_REQUEST, ChatApplicationID.CHAT_REGISTRY_REQUEST);
		this.userKey = userKey;
		this.userName = userName;
		this.description = description;
	}

	public String getUserKey()
	{
		return this.userKey;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public String getDescription()
	{
		return this.description;
	}
}
