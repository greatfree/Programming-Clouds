package org.greatfree.framework.cluster.cs.multinode.unifirst.message;

import org.greatfree.framework.cluster.cs.multinode.wurb.message.ChatApplicationID;
import org.greatfree.message.multicast.container.ClusterRequest;

// Created: 02/10/2019, Bing Li
public class ChatPartnerRequest extends ClusterRequest
{
	private static final long serialVersionUID = -4492032909633460360L;

	private String localUserKey;
	private String userKey;

	public ChatPartnerRequest(String localUserKey, String userKey)
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

		/*
		 * 	To save resources, the broadcasting can be changed to unicasting. So when searching one registered user, it needs to perform the partner-unicasting approach. 02/24/2019, Bing Li
		 * 
		 * Since the registry is broadcast, it is fine to send the request to the source child. 02/15/2019, Bing Li
		 */
//		super(userKey, MulticastMessageType.UNICAST_REQUEST, ChatApplicationID.CHAT_PARTNER_REQUEST);
//		super(localUserKey, MulticastMessageType.UNICAST_REQUEST, ChatApplicationID.CHAT_PARTNER_REQUEST);
		super(localUserKey, ChatApplicationID.CHAT_PARTNER_REQUEST);
		this.localUserKey = localUserKey;
		this.userKey = userKey;
	}
	
	public String getLocalUserKey()
	{
		return this.localUserKey;
	}

	public String getUserKey()
	{
		return this.userKey;
	}
}
