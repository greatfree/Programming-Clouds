package edu.chainnet.sc.client;

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
import edu.chainnet.sc.message.RetrieveBCNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveBCNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveBCNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveBCNodesUponServiceResponse;
import edu.chainnet.sc.message.RetrieveBCNodesUponVersionRequest;
import edu.chainnet.sc.message.RetrieveBCNodesUponVersionResponse;
import edu.chainnet.sc.message.RetrieveDLNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveDLNodeUponIDResponse;
import edu.chainnet.sc.message.RetrieveDLNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveDLNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveDLNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveDLNodesUponServiceResponse;
import edu.chainnet.sc.message.RetrieveDSNodesUponVersionRequest;
import edu.chainnet.sc.message.RetrieveDSNodesUponVersionResponse;
import edu.chainnet.sc.message.RetrieveONNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveONNodeUponIDResponse;
import edu.chainnet.sc.message.RetrieveONNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveONNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveONNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveONNodesUponServiceResponse;
import edu.chainnet.sc.message.RetrieveSCNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveSCNodeUponIDResponse;
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
 * The client is located at the block-chain to interact with the collaborator. 10/20/2020, Bing Li
 */

// Created: 10/20/2020, Bing Li
class BCClient
{
	private IPAddress collaboratorAddress;

	private BCClient()
	{
	}

	/*
	 * Initialize a singleton. 07/09/2020, Bing Li
	 */
	private static BCClient instance = new BCClient();
	
	public static BCClient BC()
	{
		if (instance == null)
		{
			instance = new BCClient();
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

	public void init() throws ClassNotFoundException, RemoteReadException, IOException
	{
		StandaloneClient.CS().init();
		this.collaboratorAddress = StandaloneClient.CS().getIPAddress(SCConfig.REGISTRY_SERVER_IP, SCConfig.REGISTRY_SERVER_PORT, SCConfig.COLLABORATOR_ROOT_KEY);
	}
	
	public boolean registerBCNode() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new BlockChainNodeRegistryRequest(NodeInfo.BC().generateBCNode()));
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
	
	public boolean registerDLNode() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new DataLakeNodeRegistryRequest(NodeInfo.BC().generateDSNode(DBConfig.DL_NODE)));
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
	
	public boolean registerONNode() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new OracleNodeRegistryRequest(NodeInfo.BC().generateDSNode(DBConfig.ON_NODE)));
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
	
	public boolean registerSCNode() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SmartContractNodeRegistryRequest(NodeInfo.BC().generateDSNode(DBConfig.SC_NODE)));
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

	public BCNode getBCNodeUponID() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodeUponIDRequest(NodeInfo.BC().getBCID(), false));
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

	public DSNode getDLNodeUponID() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodeUponIDRequest(NodeInfo.BC().getDLID(), false));
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

	public DSNode getONNodeUponID() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodeUponIDRequest(NodeInfo.BC().getONID(), false));
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

	public DSNode getSCNodeUponID() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodeUponIDRequest(NodeInfo.BC().getSCID(), false));
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

	public List<BCNode> getBCNodesUponDescription() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodesUponDescriptionRequest(NodeInfo.BC().getBCDescription(), false));
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

	public List<DSNode> getDLNodesUponDescription() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodesUponDescriptionRequest(NodeInfo.BC().getDLDescription(), false));
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

	public List<DSNode> getONNodesUponDescription() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodesUponDescriptionRequest(NodeInfo.BC().getONDescription(), false));
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

	public List<DSNode> getSCNodesUponDescription() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodesUponDescriptionRequest(NodeInfo.BC().getSCDescription(), false));
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

	public List<BCNode> getBCNodesUponService() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodesUponServiceRequest(NodeInfo.BC().getService(), false));
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

	public List<DSNode> getDLNodesUponService() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDLNodesUponServiceRequest(NodeInfo.BC().getService(), false));
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

	public List<DSNode> getONNodesUponService() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveONNodesUponServiceRequest(NodeInfo.BC().getService(), false));
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

	public List<DSNode> getSCNodesUponService() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveSCNodesUponServiceRequest(NodeInfo.BC().getService(), false));
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
	
	public List<BCNode> getBCNodesUponVersion() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveBCNodesUponVersionRequest(NodeInfo.BC().getVersion(DBConfig.BC_NODE)));
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

	public List<DSNode> getDSNodesUponVersion(int type) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Response res = (Response)StandaloneClient.CS().read(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new RetrieveDSNodesUponVersionRequest(NodeInfo.BC().getVersion(type), type));
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
	
	public void synchronizeBCNodeRegistry() throws IOException, InterruptedException
	{
		NodeInfo.BC().incrementBCVersion();
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeBCNodeRegistryNotification(NodeInfo.BC().generateBCNode()));
	}
	
	public void synchronizeDLNodeRegistry() throws IOException, InterruptedException
	{
		NodeInfo.BC().incrementDLVersion();
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeDLNodeRegistryNotification(NodeInfo.BC().generateDSNode(DBConfig.DL_NODE)));
	}
	
	public void synchronizeONNodeRegistry() throws IOException, InterruptedException
	{
		NodeInfo.BC().incrementONVersion();
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeONNodeRegistryNotification(NodeInfo.BC().generateDSNode(DBConfig.ON_NODE)));
	}
	
	public void synchronizeSCNodeRegistry() throws IOException, InterruptedException
	{
		NodeInfo.BC().incrementSCVersion();
		StandaloneClient.CS().syncNotify(this.collaboratorAddress.getIP(), this.collaboratorAddress.getPort(), new SynchronizeSCNodeRegistryNotification(NodeInfo.BC().generateDSNode(DBConfig.SC_NODE)));
	}
}
