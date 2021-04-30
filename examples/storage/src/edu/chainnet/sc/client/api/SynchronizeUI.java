package edu.chainnet.sc.client.api;

import java.io.IOException;
import java.util.Scanner;

import edu.chainnet.sc.client.NodeInfo;
import edu.chainnet.sc.client.SynchronizeMenu;
import edu.chainnet.sc.client.SynchronizeOptions;
import edu.chainnet.sc.collaborator.child.db.DBConfig;

// Created: 10/29/2020, Bing Li
class SynchronizeUI
{
	private Scanner in = new Scanner(System.in);

	private SynchronizeUI()
	{
	}

	private static SynchronizeUI instance = new SynchronizeUI();
	
	public static SynchronizeUI API()
	{
		if (instance == null)
		{
			instance = new SynchronizeUI();
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
		System.out.println(SynchronizeMenu.MENU_HEAD);
		
		System.out.println(SynchronizeMenu.SYNCHRONIZE_BLOCK_CHAIN_NODE_REGISTRY);
		System.out.println(SynchronizeMenu.SYNCHRONIZE_DATA_LAKE_NODE_REGISTRY);
		System.out.println(SynchronizeMenu.SYNCHRONIZE_SMART_CONTRACT_NODE_REGISTRY);
		System.out.println(SynchronizeMenu.SYNCHRONIZE_ORACLE_NODE_REGISTRY);
		
		System.out.println(SynchronizeMenu.QUIT);
		System.out.println(SynchronizeMenu.MENU_TAIL);
		System.out.println(SynchronizeMenu.INPUT_PROMPT);
	}
	
	public void send(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case SynchronizeOptions.SYNCHRONIZE_BLOCK_CHAIN_NODE_REGISTRY:
				NodeInfo.BC().incrementBCVersion();
				CollaboratorClient.CHAIN().synchronizeBlockNodeRegistry(NodeInfo.BC().generateBCNode());
				break;

			case SynchronizeOptions.SYNCHRONIZE_DATA_LAKE_NODE_REGISTRY:
				NodeInfo.BC().incrementDLVersion();
				CollaboratorClient.CHAIN().synchronizeDataLakeRegistry(NodeInfo.BC().generateDSNode(DBConfig.DL_NODE));
				break;

			case SynchronizeOptions.SYNCHRONIZE_SMART_CONTRACT_NODE_REGISTRY:
				NodeInfo.BC().incrementONVersion();
				CollaboratorClient.CHAIN().synchronizeSmartContractRegistry(NodeInfo.BC().generateDSNode(DBConfig.SC_NODE));
				break;

			case SynchronizeOptions.SYNCHRONIZE_ORACLE_NODE_REGISTRY:
				NodeInfo.BC().incrementSCVersion();
				CollaboratorClient.CHAIN().synchronizeOracleNodeRegistry(NodeInfo.BC().generateDSNode(DBConfig.ON_NODE));
				break;
		}
	}
}
