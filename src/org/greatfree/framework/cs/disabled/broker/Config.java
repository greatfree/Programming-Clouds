package org.greatfree.framework.cs.disabled.broker;

import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 * 03/06/2023
 *
 */
public final class Config
{
	public final static String BROKER_ROOT = "BrokerRoot";
	public final static String BROKER_ROOT_KEY = Tools.getHash(BROKER_ROOT);
	public final static int BROKER_PORT = 8900;
}
