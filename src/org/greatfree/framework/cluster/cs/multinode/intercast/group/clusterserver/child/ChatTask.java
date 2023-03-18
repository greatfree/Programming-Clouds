package org.greatfree.framework.cluster.cs.multinode.intercast.group.clusterserver.child;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.GroupChatApplicationID;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.GroupChatNotification;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.GroupMembersRequest;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.GroupMembersResponse;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.GroupRegistryRequest;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.GroupRegistryResponse;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.GroupSearchRequest;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.GroupSearchResponse;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.InterGroupChatNotification;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.InterGroupMembersRequest;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.InterGroupRegistryRequest;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.InterGroupSearchRequest;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.InterInviteUserNotification;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.InterJoinGroupNotification;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.InterLeaveGroupNotification;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.InterRemoveUserNotification;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.InterUserSearchRequest;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.InviteUserNotification;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.JoinGroupNotification;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.LeaveGroupNotification;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.PollGroupChatRequest;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.PollGroupChatResponse;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.RemoveUserNotification;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.UserRegistryRequest;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.UserRegistryResponse;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.UserSearchRequest;
import org.greatfree.framework.cluster.cs.multinode.intercast.group.message.UserSearchResponse;
import org.greatfree.framework.cs.multinode.server.CSAccount;
import org.greatfree.framework.cs.twonode.server.AccountRegistry;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.message.multicast.container.ClusterRequest;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * Most of code is programmed in the aircraft from Zhuhai to Xi'An, immediately after coming back from Canada, Bing Li
 */

// Created: 04/07/2019, Bing Li
class ChatTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.cluster.cs.multinode.intercast.group.clusterserver.child");

	@Override
	public void processNotification(ClusterNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case GroupChatApplicationID.JOIN_GROUP_NOTIFICATION:
				InterJoinGroupNotification ijgn = (InterJoinGroupNotification)notification;
				AccountRegistry.CS().add(ijgn.getAccount());
				JoinGroupNotification jgn = (JoinGroupNotification)ijgn.getIntercastNotification();
				log.info(jgn.getSenderName() + ": processNotification(): JOIN_GROUP_NOTIFICATION received @" + Calendar.getInstance().getTime());
				GroupRegistry.CS().addMember(jgn.getGroupKey(), jgn.getUserKey());
				break;
				
			case GroupChatApplicationID.LEAVE_GROUP_NOTIFICATION:
				InterLeaveGroupNotification ilgn = (InterLeaveGroupNotification)notification;
				LeaveGroupNotification lgn = (LeaveGroupNotification)ilgn.getIntercastNotification();
				log.info(lgn.getSenderName() + ": processNotification(): LEAVE_GROUP_NOTIFICATION received @" + Calendar.getInstance().getTime());
				GroupRegistry.CS().removeMember(lgn.getGroupKey(), lgn.getUserKey());
				break;
				
			case GroupChatApplicationID.INVITE_USER_NOTIFICATION:
				InterInviteUserNotification iiun = (InterInviteUserNotification)notification;
				InviteUserNotification iun = (InviteUserNotification)iiun.getIntercastNotification();
				log.info(iun.getSenderName() + ": processNotification(): INVITE_USER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				GroupRegistry.CS().addMember(iun.getGroupKey(), iun.getUserKey());
				break;
				
			case GroupChatApplicationID.REMOVE_USER_NOTIFICATION:
				InterRemoveUserNotification irun = (InterRemoveUserNotification)notification;
				RemoveUserNotification run = (RemoveUserNotification)irun.getIntercastNotification();
				log.info(run.getSenderName() + ": processNotification(): REMOVE_USER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				GroupRegistry.CS().removeMember(run.getGroupKey(), run.getUserKey());
				break;
				
			case GroupChatApplicationID.GROUP_CHAT_NOTIFICATION:
				InterGroupChatNotification igcn = (InterGroupChatNotification)notification;
				GroupChatNotification gcn = (GroupChatNotification)igcn.getIntercastNotification();
				log.info(gcn.getSenderName() + ": processNotification(): GROUP_CHAT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				System.out.println(AccountRegistry.CS().getUserName(gcn.getSenderKey()) + " says, " + gcn.getChatMessage());
				PublicChatSessions.HUNGARY().addMessage(gcn.getGroupKey(), gcn.getSenderKey(), gcn.getChatMessage());
				break;

			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				log.info("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ChatChild.GROUP().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;

			case ClusterApplicationID.STOP_ONE_CHILD_ON_CLUSTER_NOTIFICATION:
				log.info("STOP_ONE_CHILD_ON_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					ChatChild.GROUP().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException | RemoteIPNotExistedException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public MulticastResponse processRequest(ClusterRequest request)
	{
		switch (request.getApplicationID())
		{
			/*
			case GroupChatApplicationID.GROUP_REGISTRY_REQUEST:
				GroupRegistryRequest grr = (GroupRegistryRequest)request;
				System.out.println(grr.getSenderName() + ": processRequest(): GROUP_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				GroupRegistry.CS().add(new GroupAccount(grr.getGroupKey(), grr.getGroupName(), grr.getDescription()));
				return new GroupRegistryResponse(grr.getCollaboratorKey(), true);
			*/
				
			case GroupChatApplicationID.USER_REGISTRY_REQUEST:
				UserRegistryRequest urr = (UserRegistryRequest)request;
				System.out.println(urr.getSenderName() + ": processRequest(): USER_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				AccountRegistry.CS().add(new CSAccount(urr.getUserKey(), urr.getUserName(), urr.getDescription()));
				return new UserRegistryResponse(urr.getCollaboratorKey(), true);
				
			case GroupChatApplicationID.POLL_GROUP_CHAT_REQUEST:
				PollGroupChatRequest pgcr = (PollGroupChatRequest)request;
				System.out.println("1) " + pgcr.getSenderName() + ": processRequest(): POLL_GROUP_CHAT_REQUEST received @" + Calendar.getInstance().getTime());
				if (AccountRegistry.CS().isAccountExisted(pgcr.getUserKey()))
				{
//					System.out.println("ChatTask-processRequest(): user key = " + pgcr.getUserKey() + " is polling the group, " + pgcr.getGroupKey());
					if (GroupRegistry.CS().isMemberExisted(pgcr.getGroupKey(), pgcr.getUserKey()))
					{
						System.out.println("ChatTask-processRequest(): user name = " + AccountRegistry.CS().getUserName(pgcr.getUserKey()) + " is polling the group, " + GroupRegistry.CS().getAccount(pgcr.getGroupKey()).getGroupName());
					}
					else
					{
						System.out.println("ChatTask-processRequest(): polling user = " + AccountRegistry.CS().getUserName(pgcr.getUserKey()) + ", does NOT join the group ...");
					}
				}
				else
				{
					System.out.println("ChatTask-processRequest(): polling user does NOT exist in AccountRegistry ...");
				}
				if (GroupRegistry.CS().isMemberExisted(pgcr.getGroupKey(), pgcr.getUserKey()))
				{
					System.out.println("2) " + pgcr.getSenderName() + ": processRequest(): POLL_GROUP_CHAT_REQUEST received @" + Calendar.getInstance().getTime());
					return new PollGroupChatResponse(PublicChatSessions.HUNGARY().getMessages(pgcr.getGroupKey(), pgcr.getMessageCount(), pgcr.getTime()), request.getCollaboratorKey());
				}
				else
				{
					System.out.println("3) " + pgcr.getSenderName() + ": processRequest(): POLL_GROUP_CHAT_REQUEST received @" + Calendar.getInstance().getTime());
					return new PollGroupChatResponse(null, request.getCollaboratorKey());
				}
		}
		return null;
	}

	/*
	 * The source child needs to call the method for the intercast notification. 06/18/2022, Bing Li
	 */
	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		switch (notification.getApplicationID())
		{
			case GroupChatApplicationID.JOIN_GROUP_NOTIFICATION:
				/*
				 * The user and the group to be joined must be saved in different children of the cluster. So it is necessary to perform the intercasting for the consistency. 06/18/2022, Bing Li
				 */
				JoinGroupNotification jgn = (JoinGroupNotification)notification;
				System.out.println(jgn.getSenderName() + ": prepareNotification(): JOIN_GROUP_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// The below code is not necessarily asynchronous. The operation should be consistent with that in the destination child. If the below code is executed concurrently, it is not easy to manage the consistency. In the cluster programming, I hope developers can be avoided to consider the issue of concurrency programming. 04/19/2019, Bing Li
				System.out.println("ChatTask-prepareNotification(): user name = " + AccountRegistry.CS().getUserName(jgn.getUserKey()) + " is joining the group, " + GroupRegistry.CS().getAccount(jgn.getGroupKey()).getGroupName());
				GroupRegistry.CS().addMember(jgn.getGroupKey(), jgn.getUserKey());
				/*
				 * The account info is attached with the InterChildNotification. That is the goal of the preparation. 06/18/2022, Bing Li
				 */
				return new InterJoinGroupNotification(jgn, AccountRegistry.CS().getAccount(jgn.getUserKey()));
				
			case GroupChatApplicationID.LEAVE_GROUP_NOTIFICATION:
				LeaveGroupNotification lgn = (LeaveGroupNotification)notification;
				System.out.println(lgn.getSenderName() + ": prepareNotification(): LEAVE_GROUP_NOTIFICATION received @" + Calendar.getInstance().getTime());
				GroupRegistry.CS().removeMember(lgn.getGroupKey(), lgn.getUserKey());
				return new InterLeaveGroupNotification(lgn);
				
			case GroupChatApplicationID.INVITE_USER_NOTIFICATION:
				InviteUserNotification ign = (InviteUserNotification)notification;
				System.out.println(ign.getSenderName() + ": prepareNotification(): INVITE_USER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				GroupRegistry.CS().addMember(ign.getGroupKey(), ign.getUserKey());
				return new InterInviteUserNotification(ign);
				
			case GroupChatApplicationID.REMOVE_USER_NOTIFICATION:
				RemoveUserNotification run = (RemoveUserNotification)notification;
				System.out.println(run.getSenderName() + ": prepareNotification(): REMOVE_USER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				GroupRegistry.CS().removeMember(run.getGroupKey(), run.getUserKey());
				return new InterRemoveUserNotification(run);
				
			case GroupChatApplicationID.GROUP_CHAT_NOTIFICATION:
				GroupChatNotification gcn = (GroupChatNotification)notification;
				System.out.println(gcn.getSenderName() + ": prepareNotification(): GROUP_CHAT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				System.out.println(AccountRegistry.CS().getUserName(gcn.getSenderKey()) + " says, " + gcn.getChatMessage());
				System.out.println("destination keys' size = " + gcn.getDestinationKeys().size());
				PublicChatSessions.HUNGARY().addMessage(gcn.getGroupKey(), gcn.getSenderKey(), gcn.getChatMessage());
				return new InterGroupChatNotification(gcn);
		}
		
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		switch (request.getApplicationID())
		{
			case GroupChatApplicationID.GROUP_REGISTRY_REQUEST:
				GroupRegistryRequest grr = (GroupRegistryRequest)request;
				System.out.println(grr.getSenderName() + ": prepareRequest(): GROUP_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				GroupRegistry.CS().add(new GroupAccount(grr.getGroupKey(), grr.getGroupName(), grr.getDescription()));
//				System.out.println(grr.getSenderName() + ": prepareRequest(): GROUP_REGISTRY_REQUEST, groupKey = " + grr.getGroupKey() + ", creatorKey = " + grr.getCreatorKey());
				GroupRegistry.CS().addMember(grr.getGroupKey(), grr.getCreatorKey());
//				System.out.println(grr.getSenderName() + ": prepareRequest(): isGroupMemberExisted() = " + GroupRegistry.CS().isMemberExisted(grr.getGroupKey(), grr.getCreatorKey()));
				return new InterGroupRegistryRequest(grr);
		
			case GroupChatApplicationID.GROUP_SEARCH_REQUEST:
				GroupSearchRequest gsr = (GroupSearchRequest)request;
				System.out.println(gsr.getSenderName() + ": prepareRequest(): GROUP_SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				return new InterGroupSearchRequest(gsr);
				
			case GroupChatApplicationID.USER_SEARCH_REQUEST:
				UserSearchRequest usr = (UserSearchRequest)request;
				System.out.println(usr.getSenderName() + ": prepareRequest(): USER_SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				return new InterUserSearchRequest(usr);
				
			case GroupChatApplicationID.GROUP_MEMBERS_REQUEST:
				GroupMembersRequest gmr = (GroupMembersRequest)request;
				System.out.println(gmr.getSenderName() + ": prepareRequest(): GROUP_MEMBERS_REQUEST received @" + Calendar.getInstance().getTime());
				return new InterGroupMembersRequest(gmr);
		}
		
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		switch (request.getApplicationID())
		{
			case GroupChatApplicationID.GROUP_REGISTRY_REQUEST:
				InterGroupRegistryRequest igrr = (InterGroupRegistryRequest)request;
				GroupRegistryRequest grr = (GroupRegistryRequest)igrr.getIntercastRequest();
				System.out.println(grr.getSenderName() + ": processRequest(InterChildrenRequest): INTER_GROUP_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				GroupRegistry.CS().add(new GroupAccount(grr.getGroupKey(), grr.getGroupName(), grr.getDescription()));
				GroupRegistry.CS().addMember(grr.getGroupKey(), grr.getCreatorKey());
				System.out.println("creatorKey = " + grr.getCreatorKey());
				System.out.println("creatorName = " + grr.getCreatorName());
				AccountRegistry.CS().add(new CSAccount(grr.getCreatorKey(), grr.getCreatorName(), grr.getCreatorDescription()));
				return new GroupRegistryResponse(request.getCollaboratorKey(), true);
		
			case GroupChatApplicationID.GROUP_SEARCH_REQUEST:
				InterGroupSearchRequest igsr = (InterGroupSearchRequest)request;
				GroupSearchRequest gsr = (GroupSearchRequest)igsr.getIntercastRequest();
				System.out.println(gsr.getSenderName() + ": processRequest(InterChildrenRequest): INTER_GROUP_SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				if (GroupRegistry.CS().isAccountExisted(gsr.getGroupKey()))
				{
					GroupAccount ga = GroupRegistry.CS().getAccount(gsr.getGroupKey());
					return new GroupSearchResponse(ga.getGroupKey(), ga.getGroupName(), ga.getDescription(), request.getCollaboratorKey());
				}
				else
				{
					return new GroupSearchResponse(UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, request.getCollaboratorKey());
				}
				
			case GroupChatApplicationID.USER_SEARCH_REQUEST:
				InterUserSearchRequest iusr = (InterUserSearchRequest)request;
				UserSearchRequest usr = (UserSearchRequest)iusr.getIntercastRequest();
				System.out.println(usr.getSenderName() + ": processRequest(InterChildrenRequest): INTER_USER_SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				if (AccountRegistry.CS().isAccountExisted(usr.getUserKey()))
				{
					CSAccount ca = AccountRegistry.CS().getAccount(usr.getUserKey());
					return new UserSearchResponse(ca.getUserKey(), ca.getUserName(), ca.getDescription(), request.getCollaboratorKey());
				}
				else
				{
					return new UserSearchResponse(UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, request.getCollaboratorKey());
				}
				
			case GroupChatApplicationID.GROUP_MEMBERS_REQUEST:
				InterGroupMembersRequest igmr = (InterGroupMembersRequest)request;
				GroupMembersRequest gmr = (GroupMembersRequest)igmr.getIntercastRequest();
				System.out.println(gmr.getSenderName() + ": processRequest(InterChildrenRequest): INTER_GROUP_MEMBERS_REQUEST received @" + Calendar.getInstance().getTime());
				Set<String> members = GroupRegistry.CS().getGroupMembers(gmr.getGroupKey());
				for (String entry : members)
				{
					System.out.println("userKey = " + entry);
					System.out.println("isAccountExisted = " + AccountRegistry.CS().isAccountExisted(entry));
					System.out.println("userName = " + AccountRegistry.CS().getUserName(entry));
				}
				return new GroupMembersResponse(gmr.getGroupKey(), AccountRegistry.CS().getAccounts(GroupRegistry.CS().getGroupMembers(gmr.getGroupKey())), request.getCollaboratorKey());
		}
		return null;
	}

	@Override
	public void processResponse(CollectedClusterResponse response)
	{
		switch (response.getType())
		{
			case GroupChatApplicationID.GROUP_REGISTRY_REQUEST:
				System.out.println("processResponse(Response): GROUP_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				List<GroupRegistryResponse> grrs = Tools.filter(response.getResponses(), GroupRegistryResponse.class);
				for (GroupRegistryResponse entry : grrs)
				{
					System.out.println("Group registry done: " + entry.isSucceeded());
				}
				break;
				
//			case GroupChatApplicationID.GROUP_SEARCH_RESPONSE:
			case GroupChatApplicationID.GROUP_SEARCH_REQUEST:
				System.out.println("processResponse(Response): GROUP_SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				List<GroupSearchResponse> gsrs = Tools.filter(response.getResponses(), GroupSearchResponse.class);
				for (GroupSearchResponse entry : gsrs)
				{
					if (!GroupRegistry.CS().isAccountExisted(entry.getGroupKey()))
					{
						GroupRegistry.CS().add(new GroupAccount(entry.getGroupKey(), entry.getGroupName(), entry.getDescription()));
					}
				}
				break;
				
//			case GroupChatApplicationID.USER_SEARCH_RESPONSE:
			case GroupChatApplicationID.USER_SEARCH_REQUEST:
				System.out.println("processResponse(Response): USER_SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				List<UserSearchResponse> usrs = Tools.filter(response.getResponses(), UserSearchResponse.class);
				for (UserSearchResponse entry : usrs)
				{
					if (!AccountRegistry.CS().isAccountExisted(entry.getUserKey()))
					{
						AccountRegistry.CS().add(new CSAccount(entry.getUserKey(), entry.getUserName(), entry.getDescription()));
					}
				}
				break;
				
//			case GroupChatApplicationID.GROUP_MEMBERS_RESPONSE:
			case GroupChatApplicationID.GROUP_MEMBERS_REQUEST:
				System.out.println("processResponse(Response): GROUP_MEMBERS_REQUEST received @" + Calendar.getInstance().getTime());
				List<GroupMembersResponse> gmrs = Tools.filter(response.getResponses(), GroupMembersResponse.class);
				for (GroupMembersResponse entry : gmrs)
				{
					AccountRegistry.CS().add(entry.getAccounts());
					GroupRegistry.CS().addMembers(entry.getGroupKey(), entry.getAccounts());
				}
				break;
		}
	}
}
