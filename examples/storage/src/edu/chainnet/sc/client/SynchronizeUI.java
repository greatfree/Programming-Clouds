package edu.chainnet.sc.client;

import java.io.IOException;
import java.util.Scanner;

// Created: 10/20/2020, Bing Li
class SynchronizeUI
{
	private Scanner in = new Scanner(System.in);

	private SynchronizeUI()
	{
	}

	private static SynchronizeUI instance = new SynchronizeUI();
	
	public static SynchronizeUI BC()
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
				BCClient.BC().synchronizeBCNodeRegistry();
				break;

			case SynchronizeOptions.SYNCHRONIZE_DATA_LAKE_NODE_REGISTRY:
				BCClient.BC().synchronizeDLNodeRegistry();
				break;

			case SynchronizeOptions.SYNCHRONIZE_SMART_CONTRACT_NODE_REGISTRY:
				BCClient.BC().synchronizeSCNodeRegistry();
				break;

			case SynchronizeOptions.SYNCHRONIZE_ORACLE_NODE_REGISTRY:
				BCClient.BC().synchronizeONNodeRegistry();
				break;
		}
	}
}
