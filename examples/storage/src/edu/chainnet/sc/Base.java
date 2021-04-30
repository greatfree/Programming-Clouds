package edu.chainnet.sc;

import java.util.logging.Logger;

import cn.tdchain.jbcc.Connection;
import cn.tdchain.jbcc.ConnectionFactory;

// Created: 10/13/2020, Bing Li
public abstract class Base
{
	protected final static Logger log = Logger.getLogger("edu.chainnet.sc");

	protected static String[] iptables = new String[]
	{ SCConfig.TD_SERVER_1, SCConfig.TD_SERVER_2, SCConfig.TD_SERVER_3, SCConfig.TD_SERVER_4 };
	protected final static String keystorePath = SCConfig.SC_CONFIG_PATH + SCConfig.CERTIFICATE;
	protected final static String keystorePasswd = SCConfig.PASSWORD;
	protected final static int port = SCConfig.PORT;
	protected final static long timeout = SCConfig.TIMEOUT;
	protected final static String token = SCConfig.TOKEN;
	public static Connection connection = null;
	public static SnowFlake snowFlake = null;

	static
	{
		try
		{
			ConnectionFactory factory = ConnectionFactory.ConnectionConfig.builder()
					.iptables(iptables)
					.port(port)
					.timeout(timeout)
					.token(token)
					.keystorePath(keystorePath)
					.keystorePassword(keystorePasswd)
					.build();

			connection = factory.getConnection();
			snowFlake = new SnowFlake();
		}
		catch (Exception e)
		{
			log.info("Check config ====> error: " + e);
		}
	}

}
