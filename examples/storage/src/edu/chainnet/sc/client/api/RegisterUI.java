package edu.chainnet.sc.client.api;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

import org.greatfree.exceptions.RemoteReadException;

import edu.chainnet.sc.client.NodeInfo;
import edu.chainnet.sc.client.RegisterMenu;
import edu.chainnet.sc.client.RegisterOptions;
import edu.chainnet.sc.collaborator.child.db.DBConfig;

// Created: 10/29/2020, Bing Li
class  RegisterUI
{
	private final static Logger log = Logger.getLogger("edu.chainnet.sc.collaborator.child");

	private Scanner in = new Scanner(System.in);

	private RegisterUI()
	{
	}

	private static RegisterUI instance = new RegisterUI();
	
	public static RegisterUI API()
	{
		if (instance == null)
		{
			instance = new RegisterUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
	}

	public void printMenu()
	{
		System.out.println(RegisterMenu.MENU_HEAD);
		System.out.println(RegisterMenu.REGISTER_BLOCK_CHAIN_NODE);
		System.out.println(RegisterMenu.REGISTER_SMART_CONTRACT_NODE);
		System.out.println(RegisterMenu.REGISTER_ORACLE_NODE);
		System.out.println(RegisterMenu.REGISTER_DATA_LAKE_NODE);
		System.out.println(RegisterMenu.QUIT);
		System.out.println(RegisterMenu.MENU_TAIL);
		System.out.println(RegisterMenu.INPUT_PROMPT);
	}
	
	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException
	{
		switch (option)
		{
			case RegisterOptions.REGISTER_BLOCK_CHAIN_NODE:
				if (!CollaboratorClient.CHAIN().registerBlockChainNode(NodeInfo.BC().generateBCNode()))
				{
					log.info("registerBCNode() is failed!");
				}
				else
				{
					log.info("registerBCNode() is succeeded!");
				}
				break;
				
			case RegisterOptions.REGISTER_DATA_LAKE_NODE:
				if (!CollaboratorClient.CHAIN().registerDataLakeNode(NodeInfo.BC().generateDSNode(DBConfig.DL_NODE)))
				{
					log.info("registerDLNode() is failed!");
				}
				else
				{
					log.info("registerDLNode() is succeeded!");
				}
				break;
				
			case RegisterOptions.REGISTER_ORACLE_NODE:
				if (!CollaboratorClient.CHAIN().registerOracleNode(NodeInfo.BC().generateDSNode(DBConfig.ON_NODE)))
				{
					log.info("registerONNode() is failed!");
				}
				else
				{
					log.info("registerONNode() is succeeded!");
				}
				break;
				
			case RegisterOptions.REGISTER_SMART_CONTRACT_NODE:
				if (!CollaboratorClient.CHAIN().registerSmartContractNode(NodeInfo.BC().generateDSNode(DBConfig.SC_NODE)))
				{
					log.info("registerSCNode() is failed!");
				}
				else
				{
					log.info("registerSCNode() is succeeded!");
				}
				break;
				
			case RegisterOptions.QUIT_REGISTER:
				break;
		}
	}

}
