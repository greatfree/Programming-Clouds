package org.greatfree.framework.cluster.cs.multinode.intercast.group.message;

// Created: 04/02/2019, Bing Li
public class GroupChatApplicationID
{
	public final static int GROUP_REGISTRY_REQUEST = 80001;
	public final static int GROUP_REGISTRY_RESPONSE = 80002;

	public final static int USER_REGISTRY_REQUEST = 80003;
	public final static int USER_REGISTRY_RESPONSE = 80004;
	
	public final static int GROUP_SEARCH_REQUEST = 80005;
	public final static int GROUP_SEARCH_RESPONSE = 80006;
	
	public final static int USER_SEARCH_REQUEST = 80007;
	public final static int USER_SEARCH_RESPONSE = 80008;
	
	public final static int JOIN_GROUP_NOTIFICATION = 80009;
	
	public final static int LEAVE_GROUP_NOTIFICATION = 80010;
	
	public final static int INVITE_USER_NOTIFICATION = 80011;

	public final static int REMOVE_USER_NOTIFICATION = 80012;
	
	public final static int GROUP_MEMBERS_REQUEST = 80013;
	public final static int GROUP_MEMBERS_RESPONSE = 80014;
	
	public final static int GROUP_CHAT_NOTIFICATION = 80015;
	
	public final static int POLL_GROUP_CHAT_REQUEST = 80016;
	public final static int POLL_GROUP_CHAT_RESPONSE = 80017;
}
