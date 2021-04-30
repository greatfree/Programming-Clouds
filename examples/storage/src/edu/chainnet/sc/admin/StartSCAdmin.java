package edu.chainnet.sc.admin;

import java.io.IOException;

import org.greatfree.exceptions.RemoteReadException;

import edu.chainnet.sc.SCConfig;

// Created: 10/29/2020, Bing Li
class StartSCAdmin
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		SCAdmin.BC().init(SCConfig.REGISTRY_SERVER_IP, SCConfig.REGISTRY_SERVER_PORT);
		SCAdmin.BC().start();
		SCAdmin.BC().dispose();

	}

}
