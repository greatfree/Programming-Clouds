package edu.chainnet.sc;

import org.greatfree.server.container.PeerContainer;

// Created: 10/12/2020, Bing Li
public class SCConfig
{
	public final static String SC_HOME = "/home/libing/Wind/Collaboration/ChainNet/SmartContract/";
	public final static String SC_CONFIG_PATH = SC_HOME + "Config/";
	public final static String TOKEN = "32581b83-705d-40ef-9f62-1f9a4118fbe1";
	public final static String CERTIFICATE = "0x7679fb9366f371ecd4a3657c3f132daf.pfx";
	public final static String PASSWORD = "11111111";
	public final static int PORT = 18088;
	public final static long TIMEOUT = 3000;
	
	public final static String TD_SERVER_1 = "open-tdcb-node1.tdchain.cn";
	public final static String TD_SERVER_2 = "open-tdcb-node2.tdchain.cn";
	public final static String TD_SERVER_3 = "open-tdcb-node3.tdchain.cn";
	public final static String TD_SERVER_4 = "open-tdcb-node4.tdchain.cn";
	
	public final static String COLLABORATOR_ROOT_NAME = "BLOCK_CHAIN_COLLABORATOR_ROOT";
	public final static String COLLABORATOR_ROOT_KEY = PeerContainer.getPeerKey(COLLABORATOR_ROOT_NAME);
	
	public final static int COLLABORATOR_ROOT_PORT = 8001;
	
	public final static String REGISTRY_SERVER_IP = "192.168.1.14";
	public final static int REGISTRY_SERVER_PORT = 8941;
	
	public final static String CHILD_PATHS = "ChildPaths/";
	public final static int CHILD_PATH_CACHE_SIZE = 100;
	public final static int CHILD_PATH_OFFHEAP_SIZE_IN_MB = 20;
	public final static int CHILD_PATH_DISK_SIZE_IN_MB = 80;
	public final static String CHILD = "Child";	
}
