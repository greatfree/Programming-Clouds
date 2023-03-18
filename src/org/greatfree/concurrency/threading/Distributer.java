package org.greatfree.concurrency.threading;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Logger;

import org.greatfree.concurrency.threading.message.AllSlaveIPsNotification;
import org.greatfree.concurrency.threading.message.ExecuteNotification;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.IsAliveRequest;
import org.greatfree.concurrency.threading.message.IsAliveResponse;
import org.greatfree.concurrency.threading.message.KillAllNotification;
import org.greatfree.concurrency.threading.message.KillNotification;
import org.greatfree.concurrency.threading.message.MasterNotification;
import org.greatfree.concurrency.threading.message.ATMThreadRequest;
import org.greatfree.concurrency.threading.message.ATMThreadResponse;
import org.greatfree.concurrency.threading.message.ShutdownSlaveNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.concurrency.threading.message.TaskStateNotification;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.framework.container.p2p.message.AllPeerNamesRequest;
import org.greatfree.framework.container.p2p.message.AllPeerNamesResponse;
import org.greatfree.framework.container.p2p.message.AllRegisteredIPRequest;
import org.greatfree.framework.container.p2p.message.AllRegisteredIPsResponse;
import org.greatfree.framework.container.p2p.message.ChatPartnerRequest;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.message.container.Notification;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.Builder;
import org.greatfree.util.IPAddress;
import org.greatfree.util.IPPort;
import org.greatfree.util.Rand;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

// Created: 09/21/2019, Bing Li
public class Distributer
{
	private final static Logger log = Logger.getLogger("org.greatfree.concurrency.threading");

	private PeerContainer peer;
	private final String dtName;
	private final String dtKey;
	private final int port;

	/*
	 * The parameters are set for the actor who plays the role as a threading slave. 09/22/2019, Bing Li
	 */
	private String masterName;
	private String masterKey;
	private String masterIP;
	private int masterPort;
	private boolean isMaster;

	/*
	 * The parameters are set for the actor who plays the role as a threading master. 09/22/2019, Bing Li
	 */
	private Map<String, IPPort> slaves;
	private Map<String, Set<String>> slaveThreadKeys;
	private String slaveName;
	private String slaveKey;
	
	private ScheduledThreadPoolExecutor scheduler;
	private ServerTask task;
	
	public Distributer(DistributerBuilder builder)
	{
		this.isMaster = builder.isMaster();
		this.masterName = builder.getMasterName();
		this.masterKey = Tools.getHash(this.masterName);
		this.dtName = builder.getName();
		this.dtKey = Tools.getHash(this.dtName);
		this.port = builder.getPort();

		this.slaveThreadKeys = new ConcurrentHashMap<String, Set<String>>();
		this.slaves = new ConcurrentHashMap<String, IPPort>();
		
		this.scheduler = builder.getScheduler();
		if (builder.getTask() == null)
		{
//			this.task = new SlaveTask();
			this.task = new DistributerTask();
		}
		else
		{
			this.task = builder.getTask();
		}
		this.slaveName = builder.getSlaveName();
		
		PlayerSystem.THREADING().startMaster(this);
		
		/*
		if (builder.getTasks() != null)
		{
			Worker.THREADING().init(this);
			for (ThreadTask entry : builder.getTasks().values())
			{
				Worker.THREADING().addTask(entry);
			}
		}
		else
		{
			Worker.THREADING().init(this, builder.getTask());
		}
		*/
		/*
		if (builder.getTask() != null)
		{
			Worker.THREADING().init(this, builder.getTask());
		}
		else
		{
			Worker.THREADING().init(this);
		}
		*/
		/*
		if (!this.isMaster)
		{
			NewWorker.THREADING().init(this);
			ActorThreadPool.POOL().init(builder.getScheduler());
		}
		ServerStatus.FREE().init();
		*/
	}
	
	public static class DistributerBuilder implements Builder<Distributer>
	{
		/*
		 * Two names exist here. It must cause confusing. 01/11/2020, Bing Li
		 */
		private String name;
		private int port;
		private String masterName = Tools.generateUniqueKey();
//		private String masterName;
//		private String registryIP;
//		private int registryPort;
		private ScheduledThreadPoolExecutor scheduler;
//		private Map<String, ThreadTask> tasks = null;
//		private ThreadTask task = null;
		private boolean isMaster;
		private ServerTask task = null;
		private String slaveName = UtilConfig.EMPTY_STRING;
	
		public DistributerBuilder()
		{
		}

		public DistributerBuilder name(String name)
		{
			this.name = name;
			return this;
		}
		
		public DistributerBuilder port(int port)
		{
			this.port = port;
			return this;
		}
		
		public DistributerBuilder masterName(String masterName)
		{
			this.masterName = masterName;
			return this;
		}

		/*
		public FreeBuilder registryIP(String registryIP)
		{
			this.registryIP = registryIP;
			return this;
		}
		
		public FreeBuilder registryPort(int registryPort)
		{
			this.registryPort = registryPort;
			return this;
		}
		*/
		
		public DistributerBuilder scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}
		
		public DistributerBuilder task(ServerTask task)
		{
			this.task = task;
			return this;
		}

		/*
		public ActorBuilder tasks(Map<String, ThreadTask> tasks)
		{
			this.tasks = tasks;
			return this;
		}
		*/

		/*
		public ActorBuilder task(ThreadTask task)
		{
			this.task = task;
			return this;
		}
		*/

		public DistributerBuilder isMaster(boolean isMaster)
		{
			this.isMaster = isMaster;
			return this;
		}

		public DistributerBuilder slaveName(String slaveName)
		{
			this.slaveName = slaveName;
			return this;
		}

		@Override
		public Distributer build()
		{
			return new Distributer(this);
		}

		public String getName()
		{
			return this.name;
		}
		
		public int getPort()
		{
			return this.port;
		}
		
		public String getMasterName()
		{
			return this.masterName;
		}

		/*
		public String getRegistryIP()
		{
			return this.registryIP;
		}
		
		public int getRegistryPort()
		{
			return this.registryPort;
		}
		*/
		
		public ScheduledThreadPoolExecutor getScheduler()
		{
			return this.scheduler;
		}
		
		public ServerTask getTask()
		{
			return this.task;
		}

		public boolean isMaster()
		{
			return this.isMaster;
		}
		
		public String getSlaveName()
		{
			return this.slaveName;
		}

		/*
		public Map<String, ThreadTask> getTasks()
		{
			return this.tasks;
		}
		*/

		/*
		public ThreadTask getTask()
		{
			return this.task;
		}
		*/
	}
	
	public void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, RemoteIPNotExistedException, IOException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
//		TerminateSignal.SIGNAL().notifyAllTermination();
		ServerStatus.FREE().setShutdown();
//		this.slaveThreadKeys.clear();
//		this.slaveThreadKeys = null;
//		this.slaves.clear();
//		this.slaves = null;
		if (!this.isMaster)
		{
			DistributerPool.POOL().dispose(timeout);
//			TerminateSignal.SIGNAL().setTerminated();
//			TerminateSignal.SIGNAL().notifyAllTermination();
		}
		this.peer.stop(timeout);
	}
	
//	private void initPeer(ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	private void initPeer() throws ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, IOException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		log.info("Distributer-initPeer(): dtName = " + this.dtName);
		this.peer = new PeerContainer(this.dtName, this.port, this.task, true);
		this.peer.start();
	}

//	private void initSalve(ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	private void initSalve() throws ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, IOException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		this.initPeer();
//		this.isMaster = false;
//		Worker.THREADING().init(this);
		DistributerPool.POOL().init(this.scheduler);
		ServerStatus.FREE().init();
		ServerStatus.FREE().addServerID(this.peer.getPeerID());
	}

	/*
	 * 	If a distributed node plays the role of the slave only, the method is invoked to start up. 09/22/2019, Bing Li
	 * 
	 * Only the slave invokes the method to start up. 09/22/2019, Bing Li
	 */
	/*
	public void startAsSlave() throws ClassNotFoundException, IOException, RemoteReadException
	{
//		this.initSalve(new SlaveTask());
		this.initSalve();
	}
	*/

	/*
	 * If a distributed node plays the roles of both the master and the slave at the same time, the method is invoked to start up.
	 * 
	 * It is possible that a distributed node is an exception to play the role of the master only. If so, the isMaster should be true. 09/22/2019, Bing Li
	 * 
	 * The start() is invoked when multiple slaves participate in the system for concurrency on threading and their names are unknown. 09/20/2019, Bing Li
	 */
//	public void start(ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException
//	public void start(String localKey, ServerTask task, boolean isMaster) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException
//	public void start(ServerTask task, boolean isMaster) throws IOException, ClassNotFoundException, RemoteReadException, InterruptedException
	public void start() throws ClassNotFoundException, RemoteReadException, InterruptedException, DuplicatePeerNameException, IOException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		if (!this.isMaster)
		{
//			this.initSalve(task);
			this.initSalve();
		}
		else
		{
			if (this.slaveName.equals(UtilConfig.EMPTY_STRING))
			{
//				this.isMaster = true;
//				this.initPeer(task);
				this.initPeer();
//				System.out.println("ActorClient-start(): RegistryIP = " + this.peer.getRegistryIP());
//				System.out.println("ActorClient-start(): RegistryPort = " + this.peer.getRegistryPort());
//				ClusterIPResponse response = (ClusterIPResponse)this.master.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new ClusterIPRequest());
				AllRegisteredIPsResponse response = (AllRegisteredIPsResponse)this.peer.read(this.peer.getRegistryIP(), this.peer.getRegistryPort(), new AllRegisteredIPRequest());
				
//				System.out.println("ActorClient-start(): AllRegisteredIPsResponse is received!");
				
//				Set<String> slaveKeys = response.getIPs().keySet();
				Map<String, IPAddress> ipAddresses = response.getIPs();
//				System.out.println("ActorClient-start(): ipAddresses size = " + ipAddresses.size());
				Map<String, IPPort> ips = new HashMap<String, IPPort>();
//				System.out.println("ActorClient-start(): local key = " + NodeIDs.ID().getLocalKey());
//				System.out.println("ActorClient-start(): local peer IP = " + this.peer.getPeerIP() + ", port = " + this.peer.getPeerPort());
//				for (String entry : slaveKeys)
				for (Map.Entry<String, IPAddress> entry : ipAddresses.entrySet())
				{
//					System.out.println("ActorClient-start(): ip = " + entry.getValue() + " for the node key = " + entry.getKey());
//					if (!NodeIDs.ID().getLocalKey().equals(entry.getKey()))
//					if (!this.masterKey.equals(entry.getKey()))
					if (!this.dtKey.equals(entry.getKey()))
					{
						this.slaves.put(entry.getKey(), new IPPort(entry.getValue().getIP(), entry.getValue().getPort()));
						ips.put(entry.getKey(), this.slaves.get(entry.getKey()));
					}
				}
				ips.put(this.peer.getPeerID(), new IPPort(this.peer.getPeerIP(), this.peer.getPeerPort()));
				
				log.info("Distributer-start(): slaves size = " + this.slaves.size());
				
				for (IPPort entry : this.slaves.values())
				{
					this.peer.syncNotify(entry.getIP(), entry.getPort(), new AllSlaveIPsNotification(new IPPort(this.peer.getPeerIP(), this.peer.getPeerPort()), ips));
				}
			}
			else
			{
				this.initPeer();
				this.slaveKey = Tools.getHash(this.slaveName);
//				ChatPartnerResponse response = (ChatPartnerResponse)this.master.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new ChatPartnerRequest(this.slaveKey));
				ChatPartnerResponse response = (ChatPartnerResponse)this.peer.read(this.peer.getRegistryIP(), this.peer.getRegistryPort(), new ChatPartnerRequest(this.slaveKey));
				this.slaves.put(this.slaveKey, new IPPort(response.getIP(), response.getPort()));
				
				log.info("Distributer-start(): peer name = " + this.peer.getPeerName());
				
				this.peer.syncNotify(response.getIP(), response.getPort(), new MasterNotification(this.peer.getPeerName(), this.peer.getPeerID(), this.peer.getPeerIP(), this.peer.getPeerPort()));
				ServerStatus.FREE().init();
				ServerStatus.FREE().addServerID(this.slaveKey);
				ServerStatus.FREE().addServerID(this.peer.getPeerID());
			}
		}
		Worker.ATM().init(this);
	}

	/*
	 * 	If a distributed node plays the role of the master only, the method is invoked to start up. 09/22/2019, Bing Li
	 * 
	 * The master invokes the method. 09/22/2019, Bing Li
	 * 
	 * The start() is invoked when only one slave participates in the system for concurrency on threading and its name is known. 09/20/2019, Bing Li
	 */
//	public void startAsMaster(String slaveName, ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	/*
	public void startAsMaster(String slaveName) throws IOException, ClassNotFoundException, RemoteReadException
	{
//		this.master = new PeerContainer(masterName, ThreadConfig.THREAD_PORT, task, true);
//		this.peer = new PeerContainer(this.actorName, this.actorPort, task, true);
//		this.peer.start();
		this.isMaster = true;
//		this.initPeer(task);
		this.initPeer();
		
		this.slaveKey = Tools.getHash(slaveName);
		
//		ChatPartnerResponse response = (ChatPartnerResponse)this.master.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new ChatPartnerRequest(this.slaveKey));
		ChatPartnerResponse response = (ChatPartnerResponse)this.peer.read(this.peer.getRegistryIP(), this.peer.getRegistryPort(), new ChatPartnerRequest(this.slaveKey));
		this.slaves.put(this.slaveKey, new IPPort(response.getIP(), response.getPort()));
		
		ServerStatus.FREE().init();
		ServerStatus.FREE().addServerID(this.slaveKey);
		ServerStatus.FREE().addServerID(this.peer.getPeerID());
	}
	*/

	/*
	 * 	If a distributed node plays the role of the master only, the method is invoked to start up. 09/22/2019, Bing Li
	 * 
	 * The master invokes the method. 09/22/2019, Bing Li
	 * 
	 * The start() is invoked when multiple slaves participate in the system for concurrency on threading and their names are known. 09/20/2019, Bing Li
	 */
	/*
	public void startAsMaster(Set<String> slaveNames, ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	{
//		this.master = new PeerContainer(masterName, ThreadConfig.THREAD_PORT, task, true);
//		this.peer = new PeerContainer(this.actorName, this.actorPort, task, true);
//		this.peer.start();
		this.isMaster = true;
		this.initPeer(task);
		
		ServerStatus.FREE().init();
		ServerStatus.FREE().addServerID(this.peer.getPeerID());
		Set<String> slaveKeys = Sets.newHashSet();
		String partnerKey;
		for (String entry : slaveNames)
		{
			partnerKey = Tools.getHash(entry);
			ServerStatus.FREE().addServerID(partnerKey);
			slaveKeys.add(partnerKey);
		}

//		PartnersResponse response = (PartnersResponse)this.master.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new PartnersRequest(slaveKeys));
		PartnersResponse response = (PartnersResponse)this.peer.read(this.peer.getRegistryIP(), this.peer.getRegistryPort(), new PartnersRequest(slaveKeys));
		this.slaves = new ConcurrentHashMap<String, IPPort>(response.getIPs());
	}
	*/
	
	/*
	public String getSlaveKey()
	{
		return this.slaveKey;
	}
	*/

	public String getDTName()
	{
		return this.dtName;
	}
	
	public String getDTKey()
	{
		return this.dtKey;
	}
	
	public boolean isMaster()
	{
		return this.isMaster;
	}
	
	public String getSlaveKey()
	{
		return this.slaveKey;
	}
	
	public Set<String> getSlaveKeys()
	{
		return this.slaves.keySet();
	}
	
	public String getMasterName()
	{
		return this.masterName;
	}
	
	public void setMasterName(String name)
	{
		this.masterName = name;
		this.masterKey = Tools.getHash(name);
	}
	
	public String getMasterKey()
	{
		return this.masterKey;
	}

	/*
	 * Different from reusing threads methods, the method creates new threads after it is invoked. 09/30/2019, Bing Li
	 */
	public Map<String, Set<String>> createThreads(Map<String, Integer> slaveThreadSizes) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		Set<String> threadKeys;
		IPPort ip;
		Map<String, Set<String>> createdThreadKeys = new HashMap<String, Set<String>>();
		for (Map.Entry<String, Integer> entry : slaveThreadSizes.entrySet())
		{
			ip = this.slaves.get(entry.getKey());
			threadKeys = ((ATMThreadResponse)this.peer.read(ip.getIP(), ip.getPort(), new ATMThreadRequest(entry.getValue()))).getThreadKeys();
			
//			System.out.println("Distributer-createThreads(): threadKeys size = " + threadKeys.size());
			
			if (!this.slaveThreadKeys.containsKey(entry.getKey()))
			{
				this.slaveThreadKeys.put(entry.getKey(), threadKeys);
			}
			else
			{
				this.slaveThreadKeys.get(entry.getKey()).addAll(threadKeys);
			}
			createdThreadKeys.put(entry.getKey(), threadKeys);
		}
		return createdThreadKeys;
	}
	
	/*
	 * Different from reusing threads methods, the method creates new threads after it is invoked. 09/30/2019, Bing Li
	 */
	public Set<String> createThreads(int size) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
		Set<String> threadKeys = ((ATMThreadResponse)this.peer.read(ip.getIP(), ip.getPort(), new ATMThreadRequest(size))).getThreadKeys();
		if (!this.slaveThreadKeys.containsKey(this.slaveKey))
		{
			this.slaveThreadKeys.put(this.slaveKey, threadKeys);
		}
		else
		{
			this.slaveThreadKeys.get(this.slaveKey).addAll(threadKeys);
		}
		return threadKeys;
	}

	public Set<String> createThreads(String slaveKey, int size) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		IPPort ip = this.slaves.get(slaveKey);
		Set<String> threadKeys = ((ATMThreadResponse)this.peer.read(ip.getIP(), ip.getPort(), new ATMThreadRequest(size))).getThreadKeys();
		if (!this.slaveThreadKeys.containsKey(slaveKey))
		{
			this.slaveThreadKeys.put(slaveKey, threadKeys);
		}
		else
		{
			this.slaveThreadKeys.get(slaveKey).addAll(threadKeys);
		}
		return threadKeys;
	}
	
	/*
	 * Different from reusing threads methods, the method creates new threads after it is invoked. 09/30/2019, Bing Li
	 */
	public String createThread(String slaveKey) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		IPPort ip = this.slaves.get(slaveKey);
		String threadKey = ((ATMThreadResponse)this.peer.read(ip.getIP(), ip.getPort(), new ATMThreadRequest())).getThreadKey();
		if (!threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			if (!this.slaveThreadKeys.containsKey(slaveKey))
			{
//				Set<String> tKeys = Sets.newHashSet();
				Set<String> tKeys = new HashSet<String>();
				this.slaveThreadKeys.put(slaveKey, tKeys);
			}
			this.slaveThreadKeys.get(slaveKey).add(threadKey);
			return threadKey;
		}
		return ThreadConfig.NO_THREAD_KEY;
	}

	public String createThread() throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
		String threadKey = ((ATMThreadResponse)this.peer.read(ip.getIP(), ip.getPort(), new ATMThreadRequest())).getThreadKey();
		if (!threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			if (!this.slaveThreadKeys.containsKey(this.slaveKey))
			{
//				Set<String> tKeys = Sets.newHashSet();
				Set<String> tKeys = new HashSet<String>();
				this.slaveThreadKeys.put(this.slaveKey, tKeys);
			}
			this.slaveThreadKeys.get(this.slaveKey).add(threadKey);
			return threadKey;
		}
		return ThreadConfig.NO_THREAD_KEY;
	}

	/*
	 * To avoid the method is invoked many times, it is necessary to reuse existing threads. That is why the method is upgraded compared with the old version. 09/29/2019, Bing Li
	 */
	public Map<String, Set<String>> reuseThreads(Map<String, Integer> slaveThreadSizes) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		Set<String> threadKeys;
		IPPort ip;
		Map<String, Set<String>> createdThreadKeys = new HashMap<String, Set<String>>();
		Map<String, Integer> updatedRequirements = new HashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : slaveThreadSizes.entrySet())
		{
			if (this.slaveThreadKeys.containsKey(entry.getKey()))
			{
				if (this.slaveThreadKeys.get(entry.getKey()).size() >= entry.getValue())
				{
					createdThreadKeys.put(entry.getKey(), Rand.getRandomSet(this.slaveThreadKeys.get(entry.getKey()), entry.getValue()));
				}
				else
				{
					createdThreadKeys.put(entry.getKey(), this.slaveThreadKeys.get(entry.getKey()));
					updatedRequirements.put(entry.getKey(), entry.getValue() - this.slaveThreadKeys.get(entry.getKey()).size());
				}
			}
			else
			{
				updatedRequirements.put(entry.getKey(), entry.getValue());
			}
		}
		for (Map.Entry<String, Integer> entry : updatedRequirements.entrySet())
		{
			ip = this.slaves.get(entry.getKey());
			threadKeys = ((ATMThreadResponse)this.peer.read(ip.getIP(), ip.getPort(), new ATMThreadRequest(entry.getValue()))).getThreadKeys();
			if (!this.slaveThreadKeys.containsKey(entry.getKey()))
			{
				this.slaveThreadKeys.put(entry.getKey(), threadKeys);
			}
			else
			{
				this.slaveThreadKeys.get(entry.getKey()).addAll(threadKeys);
			}
			if (!createdThreadKeys.containsKey(entry.getKey()))
			{
				createdThreadKeys.put(entry.getKey(), threadKeys);
			}
			else
			{
				createdThreadKeys.get(entry.getKey()).addAll(threadKeys);
			}
		}
		return createdThreadKeys;
	}
	
	/*
	 * To avoid the method is invoked many times, it is necessary to reuse existing threads. That is why the method is upgraded compared with the old version. 09/29/2019, Bing Li
	 */
	public Set<String> reuseThreads(int size) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		int updatedSize = 0;
		if (this.slaveThreadKeys.containsKey(this.slaveKey))
		{
			if (this.slaveThreadKeys.get(this.slaveKey).size() >= size)
			{
				return Rand.getRandomSet(this.slaveThreadKeys.get(this.slaveKey), size);
			}
			else
			{
				updatedSize = size - this.slaveThreadKeys.get(this.slaveKey).size();
			}
		}
		else
		{
			updatedSize = size;
		}
		IPPort ip = this.slaves.get(this.slaveKey);
		Set<String> threadKeys = ((ATMThreadResponse)this.peer.read(ip.getIP(), ip.getPort(), new ATMThreadRequest(updatedSize))).getThreadKeys();
//		System.out.println("Distributer-reuseThreads(): threadKeys = " + threadKeys);
		if (!this.slaveThreadKeys.containsKey(this.slaveKey))
		{
			this.slaveThreadKeys.put(this.slaveKey, threadKeys);
		}
		else
		{
			this.slaveThreadKeys.get(this.slaveKey).addAll(threadKeys);
		}
		return this.slaveThreadKeys.get(this.slaveKey);
	}
	
	/*
	 * To avoid the method is invoked many times, it is necessary to reuse existing threads. That is why the method is upgraded compared with the old version. 09/29/2019, Bing Li
	 */
	public String reuseThread(String slaveKey) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		IPPort ip = this.slaves.get(slaveKey);
		if (this.slaveThreadKeys.containsKey(slaveKey))
		{
			if (this.slaveThreadKeys.get(slaveKey).size() > 0)
			{
				return Rand.getRandomStringInSet(this.slaveThreadKeys.get(slaveKey));
			}
		}
//		Set<String> threadKeys = Sets.newHashSet();
		Set<String> threadKeys = new HashSet<String>();
		this.slaveThreadKeys.put(slaveKey, threadKeys);
		String threadKey = ((ATMThreadResponse)this.peer.read(ip.getIP(), ip.getPort(), new ATMThreadRequest())).getThreadKey();
		if (!threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			this.slaveThreadKeys.get(slaveKey).add(threadKey);
			return threadKey;
		}
		return ThreadConfig.NO_THREAD_KEY;
	}
	
	/*
	 * To avoid the method is invoked many times, it is necessary to reuse existing threads. That is why the method is upgraded compared with the old version. 09/29/2019, Bing Li
	 */
	public String reuseThread() throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
		if (this.slaveThreadKeys.containsKey(this.slaveKey))
		{
			if (this.slaveThreadKeys.get(this.slaveKey).size() > 0)
			{
				return Rand.getRandomStringInSet(this.slaveThreadKeys.get(this.slaveKey));
			}
		}
//		Set<String> threadKeys = Sets.newHashSet();
		Set<String> threadKeys = new HashSet<String>();
		this.slaveThreadKeys.put(this.slaveKey, threadKeys);
		String threadKey = ((ATMThreadResponse)this.peer.read(ip.getIP(), ip.getPort(), new ATMThreadRequest())).getThreadKey();
		if (!threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			this.slaveThreadKeys.get(this.slaveKey).add(threadKey);
			return threadKey;
		}
		return ThreadConfig.NO_THREAD_KEY;
	}

	// Send one task notification to one slave through asynchronous notifying. 01/08/2020, Bing Li
	public void assignTask(String slaveKey, TaskNotification task) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		// Get the IP address of the slave. 01/08/2020, Bing Li
		IPPort ip = this.slaves.get(slaveKey);
		// Notify the slave asynchronously. 01/08/2020, Bing Li
		this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
	}

	public void assignTask(TaskNotification task) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
		this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
	}
	
	public TaskResponse assignTask(String slaveKey, TaskRequest task, long timeout) throws ThreadAssignmentException
	{
		if (!task.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			Worker.ATM().addSync(task.getCollaboratorKey());
			IPPort ip = this.slaves.get(slaveKey);
			this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
			Worker.ATM().holdOn(task.getCollaboratorKey(), timeout);
			return Worker.ATM().getResponse(task.getCollaboratorKey());
		}
		else
		{
			throw new ThreadAssignmentException(); 
		}
	}

	public Set<TaskResponse> assignTasks(String slaveKey, TaskRequest task, long timeout) throws ThreadAssignmentException
	{
		if (task.getThreadKeys() != ThreadConfig.NO_THREAD_KEYS)
		{
			Worker.ATM().addSync(task.getCollaboratorKey(), task.getThreadKeys().size());
			IPPort ip = this.slaves.get(slaveKey);
			this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
			Worker.ATM().holdOn(task.getCollaboratorKey(), timeout);
			return Worker.ATM().getResponses(task.getCollaboratorKey());
		}
		else
		{
			throw new ThreadAssignmentException(); 
		}
	}
	
	public TaskResponse assignTask(TaskRequest task, long timeout) throws ThreadAssignmentException
	{
		if (!task.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			Worker.ATM().addSync(task.getCollaboratorKey());
			IPPort ip = this.slaves.get(this.slaveKey);
			this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
			Worker.ATM().holdOn(task.getCollaboratorKey(), timeout);
			return Worker.ATM().getResponse(task.getCollaboratorKey());
		}
		else
		{
			throw new ThreadAssignmentException(); 
		}
	}
	
	public Set<TaskResponse> assignTasks(TaskRequest task, long timeout) throws ThreadAssignmentException
	{
		if (task.getThreadKeys() != ThreadConfig.NO_THREAD_KEYS)
		{
			Worker.ATM().addSync(task.getCollaboratorKey(), task.getThreadKeys().size());
			IPPort ip = this.slaves.get(this.slaveKey);
			this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
			Worker.ATM().holdOn(task.getCollaboratorKey(), timeout);
			return Worker.ATM().getResponses(task.getCollaboratorKey());
		}
		else
		{
			throw new ThreadAssignmentException(); 
		}
	}

	public void assignTask(String slaveKey, TaskInvokeNotification task) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(slaveKey);
		this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
	}

	public void assignTask(TaskInvokeNotification task) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
		this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
	}
	
	public TaskResponse assignTask(String slaveKey, TaskInvokeRequest task, long timeout) throws ThreadAssignmentException
	{
		if (!task.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			Worker.ATM().addSync(task.getCollaboratorKey());
			IPPort ip = this.slaves.get(slaveKey);
			this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
			Worker.ATM().holdOn(task.getCollaboratorKey(), timeout);
			return Worker.ATM().getResponse(task.getCollaboratorKey());
		}
		else
		{
			throw new ThreadAssignmentException(); 
		}
	}
	
	public Set<TaskResponse> assignTasks(String slaveKey, TaskInvokeRequest task, long timeout) throws ThreadAssignmentException
	{
		if (task.getThreadKeys() != ThreadConfig.NO_THREAD_KEYS)
		{
			Worker.ATM().addSync(task.getCollaboratorKey(), task.getThreadKeys().size());
			IPPort ip = this.slaves.get(slaveKey);
			this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
			Worker.ATM().holdOn(task.getCollaboratorKey(), timeout);
			return Worker.ATM().getResponses(task.getCollaboratorKey());
		}
		else
		{
			throw new ThreadAssignmentException(); 
		}
	}
	
	public TaskResponse assignTask(TaskInvokeRequest task, long timeout) throws ThreadAssignmentException
	{
		if (!task.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			Worker.ATM().addSync(task.getCollaboratorKey());
			IPPort ip = this.slaves.get(this.slaveKey);
			this.peer.asyncNotify(ip.getIP(), ip.getPort(), task);
			Worker.ATM().holdOn(task.getCollaboratorKey(), timeout);
			return Worker.ATM().getResponse(task.getCollaboratorKey());
		}
		else
		{
			throw new ThreadAssignmentException(); 
		}
	}
	
	public void notify(InteractNotification notification)
	{
		IPPort ip = this.slaves.get(notification.getSlaveKey());		
		this.peer.asyncNotify(ip.getIP(), ip.getPort(), notification);
	}
	
	public TaskResponse readThread(InteractRequest request, long timeout) throws ClassNotFoundException, RemoteReadException, ThreadAssignmentException, InterruptedException, RemoteIPNotExistedException
	{
		if (!request.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			Worker.ATM().addSync(request.getCollaboratorKey());
			if (!this.slaves.containsKey(request.getDestinationSlaveKey()))
			{
				log.info("Distributer-readThread(): try to obtain destination slave IP address ...");
				this.obtainSlaveAddress(request.getDestinationSlaveKey());
			}
			IPPort ip = this.slaves.get(request.getDestinationSlaveKey());
			log.info("Distributer-readThread(): ip = " + ip.getIP() + ", port = " + ip.getPort());
			this.peer.asyncNotify(ip.getIP(), ip.getPort(), request);
			Worker.ATM().holdOn(request.getCollaboratorKey(), timeout);
			return Worker.ATM().getResponse(request.getCollaboratorKey());
		}
		else
		{
			throw new ThreadAssignmentException(); 
		}
	}
	
	public Set<TaskResponse> readThreads(InteractRequest request, long timeout) throws ClassNotFoundException, RemoteReadException, ThreadAssignmentException, RemoteIPNotExistedException
	{
		if (request.getThreadKeys() != ThreadConfig.NO_THREAD_KEYS)
		{
			Worker.ATM().addSync(request.getCollaboratorKey(), request.getThreadKeys().size());
			if (!this.slaves.containsKey(request.getDestinationSlaveKey()))
			{
				this.obtainSlaveAddress(request.getDestinationSlaveKey());
			}
			IPPort ip = this.slaves.get(request.getDestinationSlaveKey());
			this.peer.asyncNotify(ip.getIP(), ip.getPort(), request);
			Worker.ATM().holdOn(request.getCollaboratorKey(), timeout);
			return Worker.ATM().getResponses(request.getCollaboratorKey());
		}
		else
		{
			throw new ThreadAssignmentException(); 
		}
	}

	public void execute(String slaveKey, String threadKey)
	{
		IPPort ip = this.slaves.get(slaveKey);
		this.peer.asyncNotify(ip.getIP(), ip.getPort(), new ExecuteNotification(threadKey));
	}

	public void execute(String threadKey)
	{
		IPPort ip = this.slaves.get(this.slaveKey);
		this.peer.asyncNotify(ip.getIP(), ip.getPort(), new ExecuteNotification(threadKey));
	}

	public boolean isAlive(String slaveKey, String threadKey) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		IPPort ip = this.slaves.get(slaveKey);
		return ((IsAliveResponse)this.peer.read(ip.getIP(), ip.getPort(), new IsAliveRequest(threadKey))).isAlive();
	}

	public boolean isAlive(String threadKey) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
		return ((IsAliveResponse)this.peer.read(ip.getIP(), ip.getPort(), new IsAliveRequest(threadKey))).isAlive();
	}

	/*
	 * The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
	 */
	/*
	public void wait(String slaveKey, String threadKey) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(slaveKey);
//		this.master.asyncNotify(ip.getIP(), ip.getPort(), new WaitNotification(threadKey));
		this.master.syncNotify(ip.getIP(), ip.getPort(), new WaitNotification(threadKey));
	}
	*/

	/*
	 * The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
	 */
	/*
	public void wait(String threadKey) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
//		this.master.asyncNotify(ip.getIP(), ip.getPort(), new WaitNotification(threadKey));
		this.master.syncNotify(ip.getIP(), ip.getPort(), new WaitNotification(threadKey));
	}
	*/

	/*
	 * The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
	 */
	/*
	public void wait(String slaveKey, String threadKey, long timeout) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(slaveKey);
//		this.master.asyncNotify(ip.getIP(), ip.getPort(), new WaitNotification(threadKey, timeout));
		this.master.syncNotify(ip.getIP(), ip.getPort(), new WaitNotification(threadKey, timeout));
	}
	*/

	/*
	 * The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
	 */
	/*
	public void wait(String threadKey, long timeout) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
//		this.master.asyncNotify(ip.getIP(), ip.getPort(), new WaitNotification(threadKey, timeout));
		this.master.syncNotify(ip.getIP(), ip.getPort(), new WaitNotification(threadKey, timeout));
	}
	*/

	/*
	 * The thread is signaled automatically when messages are enqueued. So the below lines are not necessary. 09/18/2019, Bing Li
	 */
	/*
	public void signal(String slaveKey, String threadKey) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(slaveKey);
//		this.master.asyncNotify(ip.getIP(), ip.getPort(), new SignalNotification(threadKey));
		this.master.syncNotify(ip.getIP(), ip.getPort(), new SignalNotification(threadKey));
	}
	
	public void signal(String threadKey) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
//		this.master.asyncNotify(ip.getIP(), ip.getPort(), new SignalNotification(threadKey));
		this.master.syncNotify(ip.getIP(), ip.getPort(), new SignalNotification(threadKey));
	}
	*/
	
	public void addSlave(String slaveKey, IPPort ip)
	{
		this.slaves.put(slaveKey, ip);
	}
	
	public void setMasterIP(IPPort ip)
	{
		this.masterIP = ip.getIP();
		this.masterPort = ip.getPort();
	}
	
	public void kill(String slaveKey, String threadKey, long timeout) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(slaveKey);
//		this.master.asyncNotify(ip.getIP(), ip.getPort(), new KillNotification(threadKey, timeout));
		this.peer.syncNotify(ip.getIP(), ip.getPort(), new KillNotification(threadKey, timeout));
	}
	
	public void kill(String threadKey, long timeout) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
//		this.master.asyncNotify(ip.getIP(), ip.getPort(), new KillNotification(threadKey, timeout));
		this.peer.syncNotify(ip.getIP(), ip.getPort(), new KillNotification(threadKey, timeout));
	}
	
	public void killAll(String slaveKey, long timeout) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(slaveKey);
//		this.master.asyncNotify(ip.getIP(), ip.getPort(), new KillAllNotification(timeout));
		this.peer.syncNotify(ip.getIP(), ip.getPort(), new KillAllNotification(timeout));
	}
	
	public void killAll(long timeout) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
//		this.master.asyncNotify(ip.getIP(), ip.getPort(), new KillAllNotification(timeout));
		this.peer.syncNotify(ip.getIP(), ip.getPort(), new KillAllNotification(timeout));
	}
	
	public void syncNotifySlave(String slaveKey, Notification notification) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(slaveKey);
		this.peer.syncNotify(ip.getIP(), ip.getPort(), notification);
	}
	
	public void syncNotify(IPPort ip, Notification notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ip.getIP(), ip.getPort(), notification);
	}
	
	public void syncNotifyMaster(Notification notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(this.masterIP, this.masterPort, notification);
	}
	
	public void asyncNotifyMaster(Notification notification) throws IOException, InterruptedException
	{
		this.peer.asyncNotify(this.masterIP, this.masterPort, notification);
	}
	
	public void asyncNotifyMaster(TaskResponse response)
	{
		if (this.masterIP == null)
		{
			try
			{
				this.obtainMasterAddress();
			}
			catch (ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
		this.peer.asyncNotify(this.masterIP, this.masterPort, response);
	}
	
	public void asyncNotifySlave(String slaveKey, TaskResponse response) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		if (!this.slaves.containsKey(slaveKey))
		{
			// Since the master IP is saved in the slaves, it is not necessary to retrieve the master IP from the registry server. I need to test why it happens. 10/06/2019, Bing Li
			log.info("Distributer-asyncNotifySlave(): obtaining slave address ...");
			this.obtainSlaveAddress(slaveKey);
		}
		log.info("Distributer-asyncNotifySlave(): slave address: " + this.slaves.get(slaveKey).getIP() + ":" + this.slaves.get(slaveKey).getPort());
		this.peer.asyncNotify(this.slaves.get(slaveKey).getIP(), this.slaves.get(slaveKey).getPort(), response);
	}
	
	public void shutdownSlave(String slaveKey, long timeout) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(slaveKey);
		this.peer.syncNotify(ip.getIP(), ip.getPort(), new ShutdownSlaveNotification(timeout));
	}
	
	public void shutdownSlave(long timeout) throws IOException, InterruptedException
	{
		IPPort ip = this.slaves.get(this.slaveKey);
		this.peer.syncNotify(ip.getIP(), ip.getPort(), new ShutdownSlaveNotification(timeout));
	}
	
	public void addTask(ThreadTask t)
	{
		Worker.ATM().addTask(t);
	}
	
	private void obtainMasterAddress() throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		ChatPartnerResponse response = (ChatPartnerResponse)this.peer.read(this.peer.getRegistryIP(), this.peer.getRegistryPort(), new ChatPartnerRequest(this.masterKey));
		this.masterIP = response.getIP();
		log.info("Distributer-obtainMasterAddress(): masterIP = " + this.masterIP);
		this.masterPort = response.getPort();
		log.info("Distributer-obtainMasterAddress(): masterPort = " + this.masterPort);
		ServerStatus.FREE().addServerID(this.masterKey);
		ServerStatus.FREE().addServerID(this.peer.getPeerID());
	}
	
	private void obtainSlaveAddress(String slaveKey) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		ChatPartnerResponse response = (ChatPartnerResponse)this.peer.read(this.peer.getRegistryIP(), this.peer.getRegistryPort(), new ChatPartnerRequest(slaveKey));
		this.slaves.put(slaveKey, new IPPort(response.getIP(), response.getPort()));
		ServerStatus.FREE().addServerID(slaveKey);
		ServerStatus.FREE().addServerID(this.peer.getPeerID());
	}
	
	public Map<String, String> getSlaveNames() throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		return ((AllPeerNamesResponse)this.peer.read(this.peer.getRegistryIP(), this.peer.getRegistryPort(), new AllPeerNamesRequest())).getAllNames();
	}
	
	public void asyncNotifyState(String threadKey, String taskKey, int instructType, String instructKey, boolean isDone)
	{
		if (this.masterIP == null)
		{
			try
			{
				log.info("Distributer-asyncNotifyState(): dt hashCode = " + super.hashCode());
				this.obtainMasterAddress();
			}
			catch (ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException e)
			{
				e.printStackTrace();
			}
		}
		this.peer.asyncNotify(this.masterIP, this.masterPort, new TaskStateNotification(threadKey, taskKey, instructType, instructKey, isDone));
	}
	
	/*
	public void notifyState(String threadKey, int instructType, String instructKey, boolean isDone)
	{
		if (this.masterIP == null)
		{
			try
			{
				this.obtainMasterAddress();
			}
			catch (ClassNotFoundException | RemoteReadException | IOException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
		this.slave.asyncNotify(this.masterIP, this.masterPort, new TaskStateNotification(threadKey, instructType, instructKey, isDone));
	}
	*/
}
