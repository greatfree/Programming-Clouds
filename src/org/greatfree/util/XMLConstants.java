package org.greatfree.util;

// Created: 12/25/2011, Bing Li
public class XMLConstants
{
	public final static long MAX_MESSAGE_SIZE = 1000000;
	public static final int SIZE_MESSAGE_LENGTH = 96;
	public static final String JAVA = "JV";
	public static final String IOS = "IS";

	public final static String NO_XML = "";
	public static final String ROOT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><MessageRoot/>";
	public final static String MESSAGE_ROOT = "MessageRoot";
	public final static String MESSAGE_KEY = "MessageKey";
	public final static String COMMAND = "Command";
	public final static String LINE_FEED = "\n";
	public final static String NULL = "NULL";
	public final static String NULL1 = "NULL1";
	public final static String NULL2 = "NULL2";

	public static final String SELECT_MESSAGE_KEY = "/MessageRoot/MessageKey";
	public static final String SELECT_PEER_KEY = "/MessageRoot/PeerKey";
	public static final String SELECT_PEER_NAME = "/MessageRoot/PeerName";
	public static final String SELECT_PASSWORD = "/MessageRoot/Password";
	public static final String SELECT_COMMAND = "/MessageRoot/Command";

	// PeerHandle
	public final static String PEER_KEY = "PeerKey";
	public final static String PEER_NAME = "PeerName";
	public final static String PASSWORD = "Password";
	public final static String PRIVATE_IP = "PrivateIP";
	public final static String PUBLIC_IP = "PublicIP";
	
	// SignInResponse
	public final static String STATUS = "Status";
	public final static String GENERATED_PEER_KEY = "GeneratedPeerKey";
	public final static String SIGN_IN_FAILED = "SignInFailed";
	public final static String SIGN_IN_SUCCEEDED = "SignInSucceeded";
	
	// NotifyPort
	public final static String SELECT_PORT = "/MessageRoot/Port";
	
	// GetPeerIPResponse
	public final static String PEER_IP = "PeerIP";
	public final static String PEER_PORT = "PeerPort";
	
	// SizeMessage
	public final static String SIZE_FORMAT = "%07d";
	public final static String OS = "OS";
	public final static String SIZE = "Size";
	public final static String SELECT_SIZE = "/MessageRoot/Size";
	public final static String SELECT_OS = "/MessageRoot/OS";
	
	// NeighborMessage
	public final static String GROUP_NAME = "GroupName";
	public final static String TIMING_SCALE = "TimingScale";
	public final static String NEIGHBOR = "Neighbor";
	public final static String NEIGHBOR_NAME = "NeighborName";
	public final static String NEIGHBOR_KEY = "NeighborKey";
	public final static String NEIGHBOR_TYPE = "NeighborType";
	public final static String CHILD_PEER_KEY = "ChildPeerKey";
	
	// ClickAuthorityMessage
	public final static String SELECT_GROUP_KEY = "/MessageRoot/GroupKey";
	public final static String SELECT_GROUP_NAME = "/MessageRoot/GroupName";
	public final static String SELECT_TOP_GROUP_KEY = "/MessageRoot/TopGroupKey";
	public final static String SELECT_AUTHORITY_KEY = "/MessageRoot/AuthorityKey";
	public final static String SELECT_AUTHORITY_TITLE = "/MessageRoot/AuthorityTitle";
	public final static String SELECT_AUTHORITY_URL = "/MessageRoot/AuthorityURL";
	public final static String SELECT_AUTHORITY_TIME = "/MessageRoot/AuthorityTime";
	public final static String SELECT_AUTHORITY_POINTS = "/MessageRoot/AuthorityPoints";
	
	// NavigateMessage
	public final static String SELECT_FOLLOWEE_KEY = "/MessageRoot/FolloweeKey";
	public final static String SELECT_FOLLOWEE_NAME = "/MessageRoot/FolloweeName";
	public final static String SELECT_TIMING_SCALE = "/MessageRoot/TimingScale";
	
	// GroupMessage
	public final static String GROUP = "Group";
	public final static String GROUP_KEY = "GroupKey";
	public final static String GROUP_DESCRIPTION = "GroupDescription";
	
	// HubMessage
	public final static String HUB = "Hub";
	public final static String HUB_KEY = "HubKey";
	public final static String HUB_TITLE = "HubTitle";
	public final static String HUB_URL = "HubURL";
	
	// SocialStreamMessage
	public final static String FOLLOWEE_KEY = "FolloweeKey";
	public final static String FOLLOWEE_NAME = "FolloweeName";
	public final static String CURRENT_GROUP_KEY = "CurrentGroupKey";
	public final static String CURRENT_GROUP_NAME = "CurrentGroupName";
	public final static String CURRENT_AUTHORITY = "CurrentAuthority";
	public final static String FOLLOWEE_GROUP = "FolloweeGroup";
	public final static String AUTHORITY = "Authority";
	public final static String AUTHORITY_KEY = "AuthorityKey";
	public final static String AUTHORITY_TITLE = "AuthorityTitle";
	public final static String AUTHORITY_URL = "AuthorityURL";
	public final static String AUTHORITY_TIME = "AuthorityTime";
	public final static String AUTHORITY_POINTS = "AuthorityPoints";
	public final static String AUTHORITY_GROUP_KEY = "AuthorityGroupKey";
	public final static String AUTHORITY_GROUP_NAME = "AuthorityGroupName";
	
	// FolloweeStreamMessage
	public final static String FOLLOWEE_AUTHORITY = "FolloweeAuthority";
	
	// Post
	public final static String SELECT_POST_KEY = "/MessageRoot/PostKey";
	public final static String SELECT_POST_TITLE = "/MessageRoot/PostTitle";
	public final static String SELECT_POST_CONTENT = "/MessageRoot/PostContent";
	public final static String SELECT_POST_TIME = "/MessageRoot/PostTime";
	public final static String SELECT_POST_POINTS = "/MessageRoot/PostPoints";
	public final static String POST_TITLE = "PostTitle";
	public final static String POST_CONTENT = "PostContent";
	
	// ForwardHubPage
	public final static String SELECT_NEW_GROUP_KEY = "/MessageRoot/NewGroupKey";
	public final static String SELECT_ORIGINAL_GROUP_KEY = "/MessageRoot/OriginalGroupKey";
	
	// Comment
	public final static String SELECT_PARENT_COMMENT_KEY = "/MessageRoot/ParentCommentKey";
	public final static String SELECT_COMMENT = "/MessageRoot/Comment";
	public final static String COMMENT = "Comment";
	public final static String COMMENT_KEY = "CommentKey";
	public final static String COMMENT_PARENT_KEY = "CommentParentKey";
	public final static String COMMENT_AUTHOR_KEY = "CommentAuthorKey";
	public final static String COMMENT_AUTHOR = "CommentAuthor";
	public final static String COMMENT_TIME = "CommentTime";
	public final static String COMMENT_POINTS = "CommentPoints";
	public final static String COMMENT_CONTENT = "CommentContent";
	
	// SocialComment
	public final static String CURRENT_GROUP_COMMENT = "CurrentGroupComment";
	public final static String FOLLOWEE_GROUP_COMMENT = "FolloweeGroupComment";
	public final static String FOLLOWEE_GROUP_KEY = "FolloweeGroupKey";
	public final static String FOLLOWEE_GROUP_NAME = "FolloweeGroupName";
	public final static String NEIGHBOR_COMMENT = "NeighborComment";
	
	// ContentStream
	public final static String CONTENT_KEY = "ContentKey";
	public final static String CONTENT_TITLE = "ContentTitle";
	public final static String CONTENT = "Content";
	public final static String NEIGHBOR_PARENT_KEY = "NeighborParentKey";
	public final static String NEIGHBOR_PARENT_NAME = "NeighborParentName";
	
	// CreateGroupStream
	public final static String SELECT_GROUP_DESCRIPTION = "/MessageRoot/GroupDescription";
	
	// The XPath is used to retrieve data from VertualGroupNeighbor.xml. 11/25/2012, Bing Li
	public final static String SELECT_ALL_TOP_GROUP_NAMES = "/GroupGraph/TopGroup/TopGroupName/text()";
	public final static String SELECT_STRONG_TIES = "/GroupGraph/TopGroup[TopGroupName=\"NULL\"]/StrongTie/Neighbor/text()";
	public final static String SELECT_HOST_GROUPS = "/GroupGraph/TopGroup[TopGroupName=\"NULL\"]/GroupNeighbor/HostGroup/text()";
	public final static String SELECT_TIGHT_TIES = "/GroupGraph/TopGroup[TopGroupName=\"NULL1\"]/GroupNeighbor[HostGroup=\"NULL2\"]/TightTie/Neighbor/text()";
	public final static String SELECT_WEAK_TIES = "/GroupGraph/TopGroup[TopGroupName=\"NULL1\"]/GroupNeighbor[HostGroup=\"NULL2\"]/WeakTie/Neighbor/text()";
	
	// CConfig.xml, the values are loaded into the class, Profile, after running. 02/11/2014, Bing Li
	public final static String SELECT_CSSERVER_TYPE = "/CConfig/CSServerType/text()";
	public final static String SELECT_MSERVER_COUNT = "/CConfig/MServerCount/text()";
	public final static String SELECT_NRKSERVER_COUNT = "/CConfig/NRKServerCount/text()";
	public final static String SELECT_ARKSERVER_COUNT = "/CConfig/ARKServerCount/text()";
	public final static String SELECT_CRAWLSERVER_COUNT = "/CConfig/CrawlServerCount/text()";
	public final static String SELECT_ISERVER_COUNT = "/CConfig/IServerCount/text()";
	public final static String SELECT_NRTSERVER_COUNT = "/CConfig/NRTServerCount/text()";
	
	// CellProfile.xml, the values are loaded into the class, CellProfile, after running. 07/09/2014, Bing Li
	public final static String SELECT_USERNAME = "/CellProfile/Username/text()";
	public final static String SELECT_USER_PASSWORD = "/CellProfile/Password/text()";
}
