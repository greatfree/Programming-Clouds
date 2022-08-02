package org.greatfree.framework.container.multicast;

import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 05/09/2022
 *
 */
public final class MultiConfig
{
	public final static int ROOT_PORT = 8001;
	public final static String ROOT_NAME = "ROOT";
	public final static String ROOT_KEY = Tools.getHash(ROOT_NAME);

	public final static String REGISTRY_SERVER_IP = "192.168.1.18";
	public final static int REGISTRY_SERVER_PORT = 8941;
}
