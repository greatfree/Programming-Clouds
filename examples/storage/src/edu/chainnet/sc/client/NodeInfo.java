package edu.chainnet.sc.client;

import java.util.ArrayList;
import java.util.List;

import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

import edu.chainnet.sc.collaborator.child.db.DBConfig;
import edu.chainnet.sc.message.BCNode;
import edu.chainnet.sc.message.DSNode;

/*
 * Since I cannot access the block-chain side, I have to implement a node generation approach to interact with the collaborator. 10/20/2020, Bing Li
 */

// Created: 10/20/2020, Bing Li
public class NodeInfo
{
	private final List<String> names;
	private final static int MAX_IP_VALUE = 256;
	private final static int MAX_PORT_VALUE = 50000;
	private final List<String> services;
	private final static int MAX_PARTNER_COUNT = 10;
	private final List<String> descriptions;
//	private int currentMaxVersion = 0;

	// The list keeps all of the existing IDs of BC nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private List<String> existingBCIDs;
	// The list keeps all of the existing IDs of DL nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private List<String> existingDLIDs;
	// The list keeps all of the existing IDs of ON nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private List<String> existingONIDs;
	// The list keeps all of the existing IDs of SC nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private List<String> existingSCIDs;
	
	private List<String> existingBCNames;
	private List<String> existingDLNames;
	private List<String> existingONNames;
	private List<String> existingSCNames;

	// The list keeps all of the existing descriptions of BC nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private List<String> existingBCDescriptions;
	// The list keeps all of the existing descriptions of DL nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private List<String> existingDLDescriptions;
	// The list keeps all of the existing descriptions of ON nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private List<String> existingONDescriptions;
	// The list keeps all of the existing descriptions of SC nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private List<String> existingSCDescriptions;
	
	// The list keeps all of the existing version numbers of BC nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private int currentBCVersions = 1;
	// The list keeps all of the existing version numbers of DL nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private int currentDLVersions = 1;
	// The list keeps all of the existing version numbers of ON nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private int currentONVersions = 1;
	// The list keeps all of the existing version numbers of SC nodes such that those nodes can be retrieved from the collaborator. 10/20/2020, Bing Li
	private int currentSCVersions = 1;

	private NodeInfo()
	{
		this.names = new ArrayList<String>();
		this.names.add("apple.com");
		this.names.add("google.com");
		this.names.add("microsoft.com");
		this.names.add("usatoday.com");
		this.names.add("cnn.com");
		this.names.add("amazon.com");
		this.names.add("yahoo.com");
		
		this.services = new ArrayList<String>();
		this.services.add("trans");
		this.services.add("account");
		this.services.add("business");
		
		this.descriptions = new ArrayList<String>();
		this.descriptions.add("The version is updated for the scalability");
		this.descriptions.add("The replication-based ClusterServerContainer is updated");
		this.descriptions.add("The selected child is excluded from the children list of the root of the pool cluster");
		this.descriptions.add("This is a new update after a long term interruption");
		this.descriptions.add("One difference from Actor is that players do not exist on the slave side");
		this.descriptions.add("It proves that GreatFree/Wind is a paradigm of the asynchronous messaging on the thread-level");
		this.descriptions.add("Another progress is made. That is, the ClusterServerContainer, is used as the Search Entry successfully");
		this.descriptions.add("This project might be the unique one as open source in China's universities at present");
		this.descriptions.add("When the message is to be forwarded, the IP addresses should be retained in a map");
		this.descriptions.add("That is, when a child joins and leaves, the system needs to process them");
		this.descriptions.add("Now I need to upgrade the multicasting to resolve the issue of rendezvous point");
		this.descriptions.add("It is necessary to draw an architecture for the DOP for the BA");
		this.descriptions.add("Another solution is to make a new combination of DistributedStackStore and a terminal list");
		this.descriptions.add("The terminal cache has an upper limit to save data such that the limit can be used as the one to lower the load of sorting");
		
		this.existingBCNames = new ArrayList<String>();
		this.existingDLNames = new ArrayList<String>();
		this.existingONNames = new ArrayList<String>();
		this.existingSCNames = new ArrayList<String>();
		
		this.existingBCIDs = new ArrayList<String>();
		this.existingDLIDs = new ArrayList<String>();
		this.existingONIDs = new ArrayList<String>();
		this.existingSCIDs = new ArrayList<String>();
		
		this.existingBCDescriptions = new ArrayList<String>();
		this.existingDLDescriptions = new ArrayList<String>();
		this.existingONDescriptions = new ArrayList<String>();
		this.existingSCDescriptions = new ArrayList<String>();
	}

	private static NodeInfo instance = new NodeInfo();
	
	public static NodeInfo BC()
	{
		if (instance == null)
		{
			instance = new NodeInfo();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void incrementBCVersion()
	{
		this.currentBCVersions++;
	}
	
	public void incrementDLVersion()
	{
		this.currentDLVersions++;
	}
	
	public void incrementONVersion()
	{
		this.currentONVersions++;
	}
	
	public void incrementSCVersion()
	{
		this.currentSCVersions++;
	}
	
	public BCNode generateBCNode()
	{
		BCNode node = new BCNode(this.getName(), this.getIP(), this.getService(), this.getPartners(), this.getDescription(DBConfig.BC_NODE));
		this.existingBCIDs.add(node.getKey());
		this.existingBCNames.add(node.getName());
		return node;
	}
	
	public DSNode generateDSNode(int type)
	{
		DSNode node = new DSNode(this.getName(), this.getIP(), this.getService(), this.getDescription(type));
		if (type == DBConfig.DL_NODE)
		{
			this.existingDLIDs.add(node.getKey());
			this.existingDLNames.add(node.getName());
		}
		else if (type == DBConfig.ON_NODE)
		{
			this.existingONIDs.add(node.getKey());
			this.existingONNames.add(node.getName());
		}
		else
		{
			this.existingSCIDs.add(node.getKey());
			this.existingSCNames.add(node.getName());
		}
		return node;
	}
	
	public String getBCID()
	{
		if (this.existingBCIDs.size() > 0)
		{
			return this.existingBCIDs.get(Rand.getRandom(this.existingBCIDs.size()));
		}
		else
		{
			return UtilConfig.NO_KEY;
		}
	}
	
	public String getDLID()
	{
		if (this.existingDLIDs.size() > 0)
		{
			return this.existingDLIDs.get(Rand.getRandom(this.existingDLIDs.size()));
		}
		else
		{
			return UtilConfig.NO_KEY;
		}
	}
	
	public String getONID()
	{
		if (this.existingONIDs.size() > 0)
		{
			return this.existingONIDs.get(Rand.getRandom(this.existingONIDs.size()));
		}
		else
		{
			return UtilConfig.NO_KEY;
		}
	}
	
	public String getSCID()
	{
		if (this.existingSCIDs.size() > 0)
		{
			return this.existingSCIDs.get(Rand.getRandom(this.existingSCIDs.size()));
		}
		else
		{
			return UtilConfig.NO_KEY;
		}
	}
	
	public String getBCName()
	{
		if (this.existingBCNames.size() > 0)
		{
			return this.existingBCNames.get(Rand.getRandom(this.existingBCNames.size()));
		}
		else
		{
			return UtilConfig.NO_KEY;
		}
	}
	
	public String getDLName()
	{
		if (this.existingDLNames.size() > 0)
		{
			return this.existingDLNames.get(Rand.getRandom(this.existingDLNames.size()));
		}
		else
		{
			return UtilConfig.NO_KEY;
		}
	}
	
	public String getONName()
	{
		if (this.existingONNames.size() > 0)
		{
			return this.existingONNames.get(Rand.getRandom(this.existingONNames.size()));
		}
		else
		{
			return UtilConfig.NO_KEY;
		}
	}

	public String getSCName()
	{
		if (this.existingSCNames.size() > 0)
		{
			return this.existingSCNames.get(Rand.getRandom(this.existingSCNames.size()));
		}
		else
		{
			return UtilConfig.NO_KEY;
		}
	}
	
	public String getBCDescription()
	{
		if (this.existingBCDescriptions.size() > 0)
		{
			return this.existingBCDescriptions.get(Rand.getRandom(this.existingBCDescriptions.size()));
		}
		else
		{
			return UtilConfig.EMPTY_STRING;
		}
	}
	
	public String getDLDescription()
	{
		if (this.existingDLDescriptions.size() > 0)
		{
			return this.existingDLDescriptions.get(Rand.getRandom(this.existingDLDescriptions.size()));
		}
		else
		{
			return UtilConfig.EMPTY_STRING;
		}
	}
	
	public String getONDescription()
	{
		if (this.existingONDescriptions.size() > 0)
		{
			return this.existingONDescriptions.get(Rand.getRandom(this.existingONDescriptions.size()));
		}
		else
		{
			return UtilConfig.EMPTY_STRING;
		}
	}
	
	public String getSCDescription()
	{
		if (this.existingSCDescriptions.size() > 0)
		{
			return this.existingSCDescriptions.get(Rand.getRandom(this.existingSCDescriptions.size()));
		}
		else
		{
			return UtilConfig.EMPTY_STRING;
		}
	}

	private String getName()
	{
		return this.names.get(Rand.getRandom(this.names.size()));
	}
	
	private IPAddress getIP()
	{
		return new IPAddress(Rand.getRandom(MAX_IP_VALUE) + UtilConfig.DOT + Rand.getRandom(MAX_IP_VALUE) + UtilConfig.DOT + Rand.getRandom(MAX_IP_VALUE) + UtilConfig.DOT + Rand.getRandom(MAX_IP_VALUE), Rand.getRandom(MAX_PORT_VALUE));
	}
	
	public String getService()
	{
		return this.services.get(Rand.getRandom(this.services.size()));
	}
	
	public int getVersion(int type)
	{
//		return Rand.getRandom(currentMaxVersion);
		if (type == DBConfig.BC_NODE)
		{
			return Rand.getRandom(this.currentBCVersions);
		}
		else if (type == DBConfig.DL_NODE)
		{
			return Rand.getRandom(this.currentDLVersions);
		}
		else if (type == DBConfig.ON_NODE)
		{
			return Rand.getRandom(this.currentONVersions);
		}
		else
		{
			return Rand.getRandom(this.currentSCVersions);
		}
	}
	
	private List<String> getPartners()
	{
		int partnerSize = Rand.getRandom(MAX_PARTNER_COUNT);
		List<String> partners = new ArrayList<String>();
		for (int i = 0; i < partnerSize; i++)
		{
			partners.add(this.getName());
		}
		return partners;
	}

	private String getDescription(int type)
	{
		String desc = this.descriptions.get(Rand.getRandom(this.descriptions.size()));
		if (type == DBConfig.BC_NODE)
		{
			this.existingBCDescriptions.add(desc);
		}
		else if (type == DBConfig.DL_NODE)
		{
			this.existingDLDescriptions.add(desc);
		}
		else if (type == DBConfig.ON_NODE)
		{
			this.existingONDescriptions.add(desc);
		}
		else
		{
			this.existingSCDescriptions.add(desc);
		}
		return desc;
	}
}
