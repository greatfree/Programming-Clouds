package edu.chainnet.sc.client.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.Response;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

import edu.chainnet.sc.SCConfig;
import edu.chainnet.sc.collaborator.child.db.DBConfig;
import edu.chainnet.sc.message.BCNode;
import edu.chainnet.sc.message.BlockChainNodeRegistryRequest;
import edu.chainnet.sc.message.BlockChainNodeRegistryResponse;
import edu.chainnet.sc.message.DSNode;
import edu.chainnet.sc.message.DataLakeNodeRegistryRequest;
import edu.chainnet.sc.message.DataLakeNodeRegistryResponse;
import edu.chainnet.sc.message.OracleNodeRegistryRequest;
import edu.chainnet.sc.message.OracleNodeRegistryResponse;
import edu.chainnet.sc.message.RetrieveBCNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveBCNodeUponIDResponse;
import edu.chainnet.sc.message.RetrieveBCNodeUponNameRequest;
import edu.chainnet.sc.message.RetrieveBCNodeUponNameResponse;
import edu.chainnet.sc.message.RetrieveBCNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveBCNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveBCNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveBCNodesUponServiceResponse;
import edu.chainnet.sc.message.RetrieveBCNodesUponVersionRequest;
import edu.chainnet.sc.message.RetrieveBCNodesUponVersionResponse;
import edu.chainnet.sc.message.RetrieveDLNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveDLNodeUponIDResponse;
import edu.chainnet.sc.message.RetrieveDLNodeUponNameRequest;
import edu.chainnet.sc.message.RetrieveDLNodeUponNameResponse;
import edu.chainnet.sc.message.RetrieveDLNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveDLNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveDLNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveDLNodesUponServiceResponse;
import edu.chainnet.sc.message.RetrieveDSNodesUponVersionRequest;
import edu.chainnet.sc.message.RetrieveDSNodesUponVersionResponse;
import edu.chainnet.sc.message.RetrieveONNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveONNodeUponIDResponse;
import edu.chainnet.sc.message.RetrieveONNodeUponNameRequest;
import edu.chainnet.sc.message.RetrieveONNodeUponNameResponse;
import edu.chainnet.sc.message.RetrieveONNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveONNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveONNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveONNodesUponServiceResponse;
import edu.chainnet.sc.message.RetrieveSCNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveSCNodeUponIDResponse;
import edu.chainnet.sc.message.RetrieveSCNodeUponNameRequest;
import edu.chainnet.sc.message.RetrieveSCNodeUponNameResponse;
import edu.chainnet.sc.message.RetrieveSCNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveSCNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveSCNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveSCNodesUponServiceResponse;
import edu.chainnet.sc.message.SmartContractNodeRegistryRequest;
import edu.chainnet.sc.message.SmartContractNodeRegistryResponse;
import edu.chainnet.sc.message.SynchronizeBCNodeRegistryNotification;
import edu.chainnet.sc.message.SynchronizeDLNodeRegistryNotification;
import edu.chainnet.sc.message.SynchronizeONNodeRegistryNotification;
import edu.chainnet.sc.message.SynchronizeSCNodeRegistryNotification;

/*
 * The APIs to access the collaborator tier are designed in the version. The program plays the role of the client that invokes those APIs. 10/24/2020, Bing Li
 */

// Created: 10/24/2020, Bing Li
public class CollaboratorClient
{
	private IPAddress collaboratorAddress;

	private CollaboratorClient()
	{
	}

	private static CollaboratorClient instance = new CollaboratorClient();
	
	public static CollaboratorClient CHAIN()
	{
		if (instance == null)
		{
			instance = new CollaboratorClient();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws IOException, InterruptedException
	{
		StandaloneClient.CS().dispose();
	}

	public void init(String registryIP, int registryPort) throws ClassNotFoundException, RemoteReadException, IOException
	{
		StandaloneClient.CS().init();
		this.collaboratorAddress = StandaloneClient.CS().getIPAddress(registryIP, registryPort, SCConfig.COLLABORATOR_ROOT_KEY);
	}

	/*
	 * The below methods register various nodes to the collaborator tier. 10/26/2020, Bing Li
	 */
	public boolean registerBlockChainNode(String name, String ip, int port, String serviceName, List<String> partners, String description) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new BlockChainNodeRegistryRequest(new BCNode(name, new IPAddress(ip, port), serviceName, partners, description)));
		List<BlockChainNodeRegistryResponse> bcnrs = Tools.filter(res.getResponses(), BlockChainNodeRegistryResponse.class);
		for (BlockChainNodeRegistryResponse entry : bcnrs)
		{
			if (!entry.isSucceeded())
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean registerBlockChainNode(BCNode node) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new BlockChainNodeRegistryRequest(node));
		List<BlockChainNodeRegistryResponse> bcnrs = Tools.filter(res.getResponses(), BlockChainNodeRegistryResponse.class);
		for (BlockChainNodeRegistryResponse entry : bcnrs)
		{
			if (!entry.isSucceeded())
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean registerDataLakeNode(String name, String ip, int port, String serviceName, String description) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new DataLakeNodeRegistryRequest(new DSNode(name, new IPAddress(ip, port), serviceName, description)));
		List<DataLakeNodeRegistryResponse> dlnrs = Tools.filter(res.getResponses(), DataLakeNodeRegistryResponse.class);
		for (DataLakeNodeRegistryResponse entry : dlnrs)
		{
			if (!entry.isSucceeded())
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean registerDataLakeNode(DSNode node) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new DataLakeNodeRegistryRequest(node));
		List<DataLakeNodeRegistryResponse> dlnrs = Tools.filter(res.getResponses(), DataLakeNodeRegistryResponse.class);
		for (DataLakeNodeRegistryResponse entry : dlnrs)
		{
			if (!entry.isSucceeded())
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean registerOracleNode(String name, String ip, int port, String serviceName, String description) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new OracleNodeRegistryRequest(new DSNode(name, new IPAddress(ip, port), serviceName, description)));
		List<OracleNodeRegistryResponse> onrrs = Tools.filter(res.getResponses(), OracleNodeRegistryResponse.class);
		for (OracleNodeRegistryResponse entry : onrrs)
		{
			if (!entry.isSucceeded())
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean registerOracleNode(DSNode node) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new OracleNodeRegistryRequest(node));
		List<OracleNodeRegistryResponse> onrrs = Tools.filter(res.getResponses(), OracleNodeRegistryResponse.class);
		for (OracleNodeRegistryResponse entry : onrrs)
		{
			if (!entry.isSucceeded())
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean registerSmartContractNode(String name, String ip, int port, String serviceName, String description) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SmartContractNodeRegistryRequest(new DSNode(name, new IPAddress(ip, port), serviceName, description)));
		List<SmartContractNodeRegistryResponse> scnrs = Tools.filter(res.getResponses(), SmartContractNodeRegistryResponse.class);
		for (SmartContractNodeRegistryResponse entry : scnrs)
		{
			if (!entry.isSucceeded())
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean registerSmartContractNode(DSNode node) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SmartContractNodeRegistryRequest(node));
		List<SmartContractNodeRegistryResponse> scnrs = Tools.filter(res.getResponses(), SmartContractNodeRegistryResponse.class);
		for (SmartContractNodeRegistryResponse entry : scnrs)
		{
			if (!entry.isSucceeded())
			{
				return false;
			}
		}
		return true;
	}

	/*
	 * The below methods retrieved nodes from the collaborator tier. 10/25/2020, Bing Li
	 */
	public BCNode retrieveBlockChainNodeByID(String id) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodeUponIDRequest(id, false));
		List<RetrieveBCNodeUponIDResponse> reses = Tools.filter(res.getResponses(), RetrieveBCNodeUponIDResponse.class);
		for (RetrieveBCNodeUponIDResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				return entry.getNode();
			}
		}
		return null;
	}

	public List<BCNode> retrieveBlockChainNodeByIDInHistory(String id) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodeUponIDRequest(id, true));
		List<RetrieveBCNodeUponIDResponse> reses = Tools.filter(res.getResponses(), RetrieveBCNodeUponIDResponse.class);
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (RetrieveBCNodeUponIDResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				nodes.add(entry.getNode());
			}
		}
		return nodes;
	}
	
	public BCNode retrieveBlockChainNodeByName(String name) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodeUponNameRequest(name, false));
		List<RetrieveBCNodeUponNameResponse> reses = Tools.filter(res.getResponses(), RetrieveBCNodeUponNameResponse.class);
		for (RetrieveBCNodeUponNameResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				return entry.getNode();
			}
		}
		return null;
	}

	public List<BCNode> retrieveBlockChainNodeByNameInHistory(String name) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodeUponNameRequest(name, true));
		List<RetrieveBCNodeUponNameResponse> reses = Tools.filter(res.getResponses(), RetrieveBCNodeUponNameResponse.class);
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (RetrieveBCNodeUponNameResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				nodes.add(entry.getNode());
			}
		}
		return nodes;
	}

	public DSNode retrieveDataLakeNodeByID(String id) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodeUponIDRequest(id, false));
		List<RetrieveDLNodeUponIDResponse> reses = Tools.filter(res.getResponses(), RetrieveDLNodeUponIDResponse.class);
		for (RetrieveDLNodeUponIDResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				return entry.getNode();
			}
		}
		return null;
	}

	public List<DSNode> retrieveDataLakeNodeByIDInHistory(String id) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodeUponIDRequest(id, true));
		List<RetrieveDLNodeUponIDResponse> reses = Tools.filter(res.getResponses(), RetrieveDLNodeUponIDResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveDLNodeUponIDResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				nodes.add(entry.getNode());
			}
		}
		return nodes;
	}
	
	public DSNode retrieveDataLakeNodeByName(String name) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodeUponNameRequest(name, false));
		List<RetrieveDLNodeUponNameResponse> reses = Tools.filter(res.getResponses(), RetrieveDLNodeUponNameResponse.class);
		for (RetrieveDLNodeUponNameResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				return entry.getNode();
			}
		}
		return null;
	}

	public List<DSNode> retrieveDataLakeNodeByNameInHistory(String name) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodeUponNameRequest(name, true));
		List<RetrieveDLNodeUponNameResponse> reses = Tools.filter(res.getResponses(), RetrieveDLNodeUponNameResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveDLNodeUponNameResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				nodes.add(entry.getNode());
			}
		}
		return nodes;
	}
	
	public DSNode retrieveOracleNodeByID(String id) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodeUponIDRequest(id, false));
		List<RetrieveONNodeUponIDResponse> reses = Tools.filter(res.getResponses(), RetrieveONNodeUponIDResponse.class);
		for (RetrieveONNodeUponIDResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				return entry.getNode();
			}
		}
		return null;
	}
	
	public List<DSNode> retrieveOracleNodeByIDInHistory(String id) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodeUponIDRequest(id, true));
		List<RetrieveONNodeUponIDResponse> reses = Tools.filter(res.getResponses(), RetrieveONNodeUponIDResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveONNodeUponIDResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				nodes.add(entry.getNode());
			}
		}
		return nodes;
	}
	
	public DSNode retrieveOracleNodeByName(String name) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodeUponNameRequest(name, false));
		List<RetrieveONNodeUponNameResponse> reses = Tools.filter(res.getResponses(), RetrieveONNodeUponNameResponse.class);
		for (RetrieveONNodeUponNameResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				return entry.getNode();
			}
		}
		return null;
	}
	
	public List<DSNode> retrieveOracleNodeByNameInHistory(String name) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodeUponNameRequest(name, true));
		List<RetrieveONNodeUponNameResponse> reses = Tools.filter(res.getResponses(), RetrieveONNodeUponNameResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveONNodeUponNameResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				nodes.add(entry.getNode());
			}
		}
		return nodes;
	}
	
	public DSNode retrieveSmartContractNodeByID(String id) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodeUponIDRequest(id, false));
		List<RetrieveSCNodeUponIDResponse> reses = Tools.filter(res.getResponses(), RetrieveSCNodeUponIDResponse.class);
		for (RetrieveSCNodeUponIDResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				return entry.getNode();
			}
		}
		return null;
	}
	
	public List<DSNode> retrieveSmartContractNodeByIDInHistory(String id) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodeUponIDRequest(id, true));
		List<RetrieveSCNodeUponIDResponse> reses = Tools.filter(res.getResponses(), RetrieveSCNodeUponIDResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveSCNodeUponIDResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				nodes.add(entry.getNode());
			}
		}
		return nodes;
	}

	public DSNode retrieveSmartContractNodeByName(String name) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodeUponNameRequest(name, false));
		List<RetrieveSCNodeUponNameResponse> reses = Tools.filter(res.getResponses(), RetrieveSCNodeUponNameResponse.class);
		for (RetrieveSCNodeUponNameResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				return entry.getNode();
			}
		}
		return null;
	}
	
	public List<DSNode> retrieveSmartContractNodeByNameInHistory(String name) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodeUponNameRequest(name, true));
		List<RetrieveSCNodeUponNameResponse> reses = Tools.filter(res.getResponses(), RetrieveSCNodeUponNameResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveSCNodeUponNameResponse entry : reses)
		{
			if (entry.getNode() != null)
			{
				nodes.add(entry.getNode());
			}
		}
		return nodes;
	}

	public List<BCNode> retrieveBlockChainNodesByDescription(String keyword) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodesUponDescriptionRequest(keyword, false));
		List<RetrieveBCNodesUponDescriptionResponse> reses = Tools.filter(res.getResponses(), RetrieveBCNodesUponDescriptionResponse.class);
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (RetrieveBCNodesUponDescriptionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}

	public List<BCNode> retrieveBlockChainNodesByDescriptionInHistory(String keyword) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodesUponDescriptionRequest(keyword, true));
		List<RetrieveBCNodesUponDescriptionResponse> reses = Tools.filter(res.getResponses(), RetrieveBCNodesUponDescriptionResponse.class);
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (RetrieveBCNodesUponDescriptionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}

	public List<DSNode> retrieveDataLakeNodesByDescription(String keyword) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodesUponDescriptionRequest(keyword, false));
		List<RetrieveDLNodesUponDescriptionResponse> reses = Tools.filter(res.getResponses(), RetrieveDLNodesUponDescriptionResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveDLNodesUponDescriptionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}

	public List<DSNode> retrieveDataLakeNodesByDescriptionInHistory(String keyword) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodesUponDescriptionRequest(keyword, true));
		List<RetrieveDLNodesUponDescriptionResponse> reses = Tools.filter(res.getResponses(), RetrieveDLNodesUponDescriptionResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveDLNodesUponDescriptionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveOracleNodesByDescription(String keyword) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodesUponDescriptionRequest(keyword, false));
		List<RetrieveONNodesUponDescriptionResponse> reses = Tools.filter(res.getResponses(), RetrieveONNodesUponDescriptionResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveONNodesUponDescriptionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveOracleNodesByDescriptionInHistory(String keyword) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodesUponDescriptionRequest(keyword, true));
		List<RetrieveONNodesUponDescriptionResponse> reses = Tools.filter(res.getResponses(), RetrieveONNodesUponDescriptionResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveONNodesUponDescriptionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveSmartContractNodesByDescription(String keyword) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodesUponDescriptionRequest(keyword, false));
		List<RetrieveSCNodesUponDescriptionResponse> reses = Tools.filter(res.getResponses(), RetrieveSCNodesUponDescriptionResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveSCNodesUponDescriptionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveSmartContractNodesByDescriptionInHistory(String keyword) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodesUponDescriptionRequest(keyword, true));
		List<RetrieveSCNodesUponDescriptionResponse> reses = Tools.filter(res.getResponses(), RetrieveSCNodesUponDescriptionResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveSCNodesUponDescriptionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<BCNode> retrieveBlockChainNodesByService(String serviceName) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodesUponServiceRequest(serviceName, false));
		List<RetrieveBCNodesUponServiceResponse> reses = Tools.filter(res.getResponses(), RetrieveBCNodesUponServiceResponse.class);
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (RetrieveBCNodesUponServiceResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<BCNode> retrieveBlockChainNodesByServiceInHistory(String serviceName) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodesUponServiceRequest(serviceName, true));
		List<RetrieveBCNodesUponServiceResponse> reses = Tools.filter(res.getResponses(), RetrieveBCNodesUponServiceResponse.class);
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (RetrieveBCNodesUponServiceResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}

	public List<DSNode> retrieveDataLakeNodesByService(String serviceName) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodesUponServiceRequest(serviceName, false));
		List<RetrieveDLNodesUponServiceResponse> reses = Tools.filter(res.getResponses(), RetrieveDLNodesUponServiceResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveDLNodesUponServiceResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}

	public List<DSNode> retrieveDataLakeNodesByServiceInHistory(String serviceName) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodesUponServiceRequest(serviceName, true));
		List<RetrieveDLNodesUponServiceResponse> reses = Tools.filter(res.getResponses(), RetrieveDLNodesUponServiceResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveDLNodesUponServiceResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveOracleNodesByService(String serviceName) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodesUponServiceRequest(serviceName, false));
		List<RetrieveONNodesUponServiceResponse> reses = Tools.filter(res.getResponses(), RetrieveONNodesUponServiceResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveONNodesUponServiceResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveOracleNodesByServiceInHistory(String serviceName) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodesUponServiceRequest(serviceName, true));
		List<RetrieveONNodesUponServiceResponse> reses = Tools.filter(res.getResponses(), RetrieveONNodesUponServiceResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveONNodesUponServiceResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveSmartContractNodesByService(String serviceName) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodesUponServiceRequest(serviceName, false));
		List<RetrieveSCNodesUponServiceResponse> reses = Tools.filter(res.getResponses(), RetrieveSCNodesUponServiceResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveSCNodesUponServiceResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveSmartContractNodesByServiceInHistory(String serviceName) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodesUponServiceRequest(serviceName, true));
		List<RetrieveSCNodesUponServiceResponse> reses = Tools.filter(res.getResponses(), RetrieveSCNodesUponServiceResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveSCNodesUponServiceResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<BCNode> retrieveBlockChainNodesByVersion(int version) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodesUponVersionRequest(version));
		List<RetrieveBCNodesUponVersionResponse> reses = Tools.filter(res.getResponses(), RetrieveBCNodesUponVersionResponse.class);
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (RetrieveBCNodesUponVersionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveDataLakeNodesByVersion(int version) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDSNodesUponVersionRequest(version, DBConfig.DL_NODE));
		List<RetrieveDSNodesUponVersionResponse> reses = Tools.filter(res.getResponses(), RetrieveDSNodesUponVersionResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveDSNodesUponVersionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveOracleNodesByVersion(int version) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDSNodesUponVersionRequest(version, DBConfig.ON_NODE));
		List<RetrieveDSNodesUponVersionResponse> reses = Tools.filter(res.getResponses(), RetrieveDSNodesUponVersionResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveDSNodesUponVersionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public List<DSNode> retrieveSmartContractNodesByVersion(int version) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDSNodesUponVersionRequest(version, DBConfig.SC_NODE));
		List<RetrieveDSNodesUponVersionResponse> reses = Tools.filter(res.getResponses(), RetrieveDSNodesUponVersionResponse.class);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (RetrieveDSNodesUponVersionResponse entry : reses)
		{
			if (entry.getNodes() != null)
			{
				nodes.addAll(entry.getNodes());
			}
		}
		return nodes;
	}
	
	public void synchronizeBlockNodeRegistry(String name, String ip, int port, String serviceName, List<String> partners, String description) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeBCNodeRegistryNotification(new BCNode(name, new IPAddress(ip, port), serviceName, partners, description)));
	}
	
	public void synchronizeBlockNodeRegistry(BCNode node) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeBCNodeRegistryNotification(node));
	}
	
	public void synchronizeDataLakeRegistry(String name, String ip, int port, String serviceName, String description) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeDLNodeRegistryNotification(new DSNode(name, new IPAddress(ip, port), serviceName, description)));
	}
	
	public void synchronizeDataLakeRegistry(DSNode node) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeDLNodeRegistryNotification(node));
	}
	
	public void synchronizeOracleNodeRegistry(String name, String ip, int port, String serviceName, String description) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeONNodeRegistryNotification(new DSNode(name, new IPAddress(ip, port), serviceName, description)));
	}
	
	public void synchronizeOracleNodeRegistry(DSNode node) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeONNodeRegistryNotification(node));
	}
	
	public void synchronizeSmartContractRegistry(String name, String ip, int port, String serviceName, String description) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeSCNodeRegistryNotification(new DSNode(name, new IPAddress(ip, port), serviceName, description)));
	}
	
	public void synchronizeSmartContractRegistry(DSNode node) throws IOException, InterruptedException
	{
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeSCNodeRegistryNotification(node));
	}
}
