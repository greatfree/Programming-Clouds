package edu.chainnet.sc.testing;

import java.util.logging.Logger;

import cn.tdchain.jbcc.Connection;
import cn.tdchain.jbcc.ConnectionFactory;
import edu.chainnet.sc.SCConfig;

/*
 * My user name: greatfree
 * 
 * Certificate PW: 11111111
 * 
 */

// Created: 10/12/2020, Bing Li
class CreateConnection
{
	protected final static Logger log = Logger.getLogger("edu.chainnet.sc.testing");

	protected static String[] iptables = new String[] { SCConfig.TD_SERVER_1, SCConfig.TD_SERVER_2, SCConfig.TD_SERVER_3, SCConfig.TD_SERVER_4 };
	protected final static int port = SCConfig.PORT;
	protected final static long timeout = SCConfig.TIMEOUT;
//	protected final static String token = "JFB13JBS7Z7NMZRSII643JF3TPI2MNBR66";
	
	protected final static String token = SCConfig.TOKEN;
//	protected final static String keystorePath = "D:\\keys\\rsa\\rsa_tiande_client.pfx";
	protected final static String keystorePath = SCConfig.SC_CONFIG_PATH + SCConfig.CERTIFICATE;
//	protected final static String keystorePasswd = "123456";
	protected final static String keystorePasswd = SCConfig.PASSWORD;

	public static void main(String[] args)
	{
		ConnectionFactory.ConnectionConfig.Builder builder = ConnectionFactory.ConnectionConfig.builder();
		ConnectionFactory factory = builder
				.iptables(iptables)
				.port(port)
				.timeout(timeout)
				.token(token)
				.keystorePath(keystorePath)
				.keystorePassword(keystorePasswd)
				.build();

		Connection connection = factory.getConnection();
		log.info("connection is " + connection);
		connection = factory.getConnection();
		log.info("connection is " + connection);

		factory = builder
				.iptables(iptables)
				.port(port)
				.timeout(timeout)
				.token(token)
				.keystorePath(keystorePath)
				.keystorePassword(keystorePasswd)
				.build();

		connection = factory.getConnection();
		log.info("connection is " + connection);
	}
}
