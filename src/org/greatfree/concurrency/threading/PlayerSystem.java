package org.greatfree.concurrency.threading;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.threading.message.AllSlavesNotification;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.container.Notification;
import org.greatfree.util.IPPort;

// Created: 09/29/2019, Bing Li
public class PlayerSystem
{
	private Distributer dt;

	private PlayerSystem()
	{
	}
	
	private static PlayerSystem instance = new PlayerSystem();
	
	public static PlayerSystem THREADING()
	{
		if (instance == null)
		{
			instance = new PlayerSystem();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
//		TerminateSignal.SIGNAL().setTerminated();
//		TerminateSignal.SIGNAL().notifyAllTermination();
		if (!this.dt.isMaster())
		{
			Scheduler.PERIOD().shutdown(timeout);
		}
		else
		{
			for (String entry : this.dt.getSlaveKeys())
			{
				this.dt.killAll(entry, timeout);
				this.dt.shutdownSlave(entry, timeout);
			}
		}
		this.dt.stop(timeout);
	}

	/*
	 * When multiple slaves serve, the startup method is invoked. 09/29/2019, Bing Li
	 */
	public void startSlave(int port) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		Scheduler.PERIOD().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		this.dt = new Distributer.DistributerBuilder()
			.name(DistributerIDs.ID().getNickName())
			.port(port)
			.scheduler(Scheduler.PERIOD().getScheduler())
			.isMaster(false)
			.build();
		this.dt.start();
	}

	/*
	 * When multiple slaves serve, the startup method is invoked. 09/29/2019, Bing Li
	 */
	public void startSlave(int port, ATMTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		Scheduler.PERIOD().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		this.dt = new Distributer.DistributerBuilder()
			.name(DistributerIDs.ID().getNickName())
			.port(port)
			.scheduler(Scheduler.PERIOD().getScheduler())
			.isMaster(false)
			.task(task)
			.build();
		this.dt.start();
	}

	/*
	 * When one slave serves for one master only, the startup method is invoked. 09/29/2019, Bing Li
	 */
//	public void startSlave(String slaveName, int port, String masterName) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	public void startSlave(String slaveName, int port) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		Scheduler.PERIOD().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		this.dt = new Distributer.DistributerBuilder()
			.name(slaveName)
			.port(port)
//			.masterName(masterName)
			.scheduler(Scheduler.PERIOD().getScheduler())
			.isMaster(false)
			.build();
		this.dt.start();
	}

	/*
	 * When a master manages multiple slaves, the startup method is invoked. 09/29/2019, Bing Li
	 */
//	public void startMaster(String masterName, int port, PlayerTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	public void startMaster(int port, ATMTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.dt = new Distributer.DistributerBuilder()
			.name(DistributerIDs.ID().getNickName())
			.port(port)
			.isMaster(true)
			.task(task)
			.build();
		this.dt.start();
		DistributerIDs.ID().setAllSlaveIDs(this.dt.getSlaveKeys());
		Map<String, String> names = this.dt.getSlaveNames();
		names.remove(DistributerIDs.ID().getNickKey());
		DistributerIDs.ID().setSlaveNames(names);
	}
	
	/*
	 * When one master manages one slave only, the startup method is invoked. 09/29/2019, Bing Li
	 */
	public void startMaster(String masterName, int port, String slaveName, ATMTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.dt = new Distributer.DistributerBuilder()
				.name(masterName)
				.port(port)
				.task(task)
				.slaveName(slaveName)
				.isMaster(true)
				.build();
		this.dt.start();
	}
	
	public void startMaster(Distributer dt)
	{
		this.dt = dt;
	}
	
	/*
	 * The below methods create new threads for invokers. 09/30/2019, Bing Li
	 */
	
	/*
	 * The method creates a player which has one only thread on the default slave. 10/01/2019, Bing Li
	 */
	public Player create() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String threadKey = this.dt.createThread();
		if (!threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			return new Player(this.dt, threadKey, this.dt.getSlaveKey());
		}
		return ThreadConfig.NO_PLAYER;
	}
	
	public Player create(int size) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Set<String> threadKeys = this.dt.createThreads(size);
		if (threadKeys != ThreadConfig.NO_THREAD_KEYS)
		{
			return new Player(this.dt, threadKeys, this.dt.getSlaveKey());
		}
		return ThreadConfig.NO_PLAYER;
	}
	
	/*
	 * The method creates a player which has one only thread on the specified slave. 10/01/2019, Bing Li
	 */
	public Player create(String slaveKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		String threadKey = this.dt.createThread(slaveKey);
		if (!threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			return new Player(this.dt, threadKey, slaveKey);
		}
		return ThreadConfig.NO_PLAYER;
	}
	
	/*
	 * The method creates a player which has one only thread on the specified slave. 10/01/2019, Bing Li
	 */
	public Player create(String slaveKey, long readTimeout) throws ClassNotFoundException, RemoteReadException, IOException
	{
		String threadKey = this.dt.createThread(slaveKey);
		if (!threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			return new Player(this.dt, threadKey, slaveKey, readTimeout);
		}
		return ThreadConfig.NO_PLAYER;
	}
	
	/*
	 * The method creates a player which has a number of threads on the specified slave. 10/01/2019, Bing Li
	 */
	public Player create(String slaveKey, int size) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Set<String> threadKeys = this.dt.createThreads(slaveKey, size);
		if (threadKeys != ThreadConfig.NO_THREAD_KEYS)
		{
			return new Player(this.dt, threadKeys, slaveKey);
		}
		return ThreadConfig.NO_PLAYER;
	}
	
	/*
	 * The method creates a number of players which has a number of threads on the specified slaves. 10/01/2019, Bing Li
	 */
	public Map<String, Player> create(Map<String, Integer> slaveThreadNumbers) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Map<String, Set<String>> allThreadKeys = this.dt.createThreads(slaveThreadNumbers);
		DistributerIDs.ID().setThreadKeys(allThreadKeys);
		Map<String, Player> players = new HashMap<String, Player>();
		for (Map.Entry<String, Set<String>> entry : allThreadKeys.entrySet())
		{
			players.put(entry.getKey(), new Player(this.dt, entry.getValue(), entry.getKey()));
		}
		return players;
	}

	/*
	 * The below methods reuse new threads for invokers. 09/30/2019, Bing Li
	 */
	
	/*
	 * The method reuses a player which has one only thread on the default slave. 10/01/2019, Bing Li
	 */
	public Player reuse() throws ClassNotFoundException, RemoteReadException, IOException
	{
		String threadKey = this.dt.reuseThread();
		if (!threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			return new Player(this.dt, threadKey, this.dt.getSlaveKey());
		}
		return ThreadConfig.NO_PLAYER;
	}
	
	/*
	 * The method reuses a player which has one only thread on the specified slave. 10/01/2019, Bing Li
	 */
	public Player reuse(String slaveKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		String threadKey = this.dt.reuseThread(slaveKey);
		if (!threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			return new Player(this.dt, threadKey, slaveKey);
		}
		return ThreadConfig.NO_PLAYER;
	}
	
	/*
	 * The method reuses a player which has one only thread on the specified slave. 10/01/2019, Bing Li
	 */
	public Player reuse(String slaveKey, long readTimeout) throws ClassNotFoundException, RemoteReadException, IOException
	{
		String threadKey = this.dt.reuseThread(slaveKey);
		if (!threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			return new Player(this.dt, threadKey, slaveKey, readTimeout);
		}
		return ThreadConfig.NO_PLAYER;
	}
	
	/*
	 * The method reuses a player which has a number of threads on the specified slave. 10/01/2019, Bing Li
	 */
	public Player reuse(String slaveKey, int size) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Set<String> threadKeys = this.dt.reuseThreads(size);
		if (threadKeys != ThreadConfig.NO_THREAD_KEYS)
		{
			return new Player(this.dt, threadKeys, slaveKey);
		}
		return ThreadConfig.NO_PLAYER;
	}
	
	/*
	 * The method reuses a number of players which has a number of threads on the specified slaves. 10/01/2019, Bing Li
	 */
	public Map<String, Player> reuse(Map<String, Integer> slaveThreadNumbers) throws ClassNotFoundException, RemoteReadException, IOException
	{
		Map<String, Set<String>> allThreadKeys = this.dt.createThreads(slaveThreadNumbers);
		DistributerIDs.ID().setThreadKeys(allThreadKeys);
		Map<String, Player> players = new HashMap<String, Player>();
		for (Map.Entry<String, Set<String>> entry : allThreadKeys.entrySet())
		{
			players.put(entry.getKey(), new Player(this.dt, entry.getValue(), entry.getKey()));
		}
		return players;
	}
	
	public void notifyAllSlaves(Map<String, Player> players) throws IOException, InterruptedException
	{
		for (Map.Entry<String, Player> entry : players.entrySet())
		{
//			this.dt.syncNotifySlave(entry.getKey(), new AllSlavesNotification(DistributerIDs.ID().getLocalName(), players.keySet(), DistributerIDs.ID().getAllThreadKeys(), DistributerIDs.ID().getAllSlaveNames()));
			this.dt.syncNotifySlave(entry.getKey(), new AllSlavesNotification(DistributerIDs.ID().getNickName(), DistributerIDs.ID().getAllSlaves(), DistributerIDs.ID().getAllThreadKeys(), DistributerIDs.ID().getAllSlaveNames()));
		}
	}
	
	public String getPlayerSystemName()
	{
		return this.dt.getMasterName();
	}
	
	public String getPlayerSystemKey()
	{
		return this.dt.getMasterKey();
	}
	
	public void asyncNotifyMaster(Notification notification) throws IOException, InterruptedException
	{
		this.dt.asyncNotifyMaster(notification);
	}
	
	public void syncNotifyMaster(Notification notification) throws IOException, InterruptedException
	{
		this.dt.syncNotifyMaster(notification);
	}
	
	public void addTask(ThreadTask task)
	{
		this.dt.addTask(task);
	}
	
	public void addSlaveIPs(Map<String, IPPort> ips)
	{
		for (Map.Entry<String, IPPort> entry : ips.entrySet())
		{
			this.dt.addSlave(entry.getKey(), entry.getValue());
		}
	}
	
	public void addSlaveIP(String slaveKey, IPPort ip)
	{
		this.dt.addSlave(slaveKey, ip);
	}
	
	public void setMasterIP(IPPort ip)
	{
		this.dt.setMasterIP(ip);
	}

	public void setMasterName(String name)
	{
		this.dt.setMasterName(name);
	}
	
	public String getMasterName()
	{
		return this.dt.getDTName();
	}
	
	public String getNickName()
	{
		return DistributerIDs.ID().getNickName();
	}
	
	public String getNickKey()
	{
		return DistributerIDs.ID().getNickKey();
	}
	
	public String getSlaveName(String slaveKey)
	{
		return DistributerIDs.ID().getSlaveName(slaveKey);
	}

	public Set<String> getSlavesExceptLocal(int size)
	{
		return DistributerIDs.ID().getSlavesExceptLocal(size);
	}
	
	public Player getSlaveExceptFrom(Set<String> slaves)
	{
		String slaveKey = DistributerIDs.ID().getSlaveExceptFrom(slaves);
		Set<String> threadKeys = DistributerIDs.ID().getThreadKeys(slaveKey);
		return new Player(this.dt, threadKeys, slaveKey);
	}
	
	public Player retrievePlayerWithAllThreads(String slaveKey)
	{
		return new Player(this.dt, DistributerIDs.ID().getThreadKeys(slaveKey), slaveKey);
	}
	
	public int getSlaveSize()
	{
		return DistributerIDs.ID().getSlaveSize();
	}
	
	public Set<String> getAllSlaves()
	{
		return DistributerIDs.ID().getAllSlaves();
	}
}
