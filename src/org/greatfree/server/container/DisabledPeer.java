package org.greatfree.server.container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.BrokerClusterNotAvailableException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.p2p.message.SearchBrokerRequest;
import org.greatfree.framework.container.p2p.message.SearchBrokerResponse;
import org.greatfree.framework.cs.disabled.Config;
import org.greatfree.framework.cs.disabled.Poller;
import org.greatfree.framework.cs.disabled.broker.message.BrokerNotification;
import org.greatfree.framework.cs.disabled.broker.message.PollRequest;
import org.greatfree.framework.cs.disabled.broker.message.PollResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.container.Response;
import org.greatfree.util.Builder;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/**
 * 
 * @author libing
 * 
 * 03/06/2023
 *
 */
public class DisabledPeer
{
	private final static Logger log = Logger.getLogger("org.greatfree.server.container");
	
	private PeerContainer peer;
	private RendezvousPoint rp;
//	private Map<String, Date> partners;
	private Map<String, IPAddress> partners;
	private Map<String, IPAddress> brokers;
	
	public DisabledPeer(DisabledPeerBuilder builder) throws IOException
	{
		this.peer = new PeerContainer(builder.getPeerName(), builder.getPeerPort(), builder.getListenerCount(), builder.getMaxIOCount(), builder.getRegistryIP(), builder.getRegistryPort(), builder.getTask(), true, builder.isServerDisabled());
		this.rp = new RendezvousPoint(builder.getWaitTime());
//		this.partners = new ConcurrentHashMap<String, Date>();
		this.partners = new ConcurrentHashMap<String, IPAddress>();
		this.brokers = new ConcurrentHashMap<String, IPAddress>();
	}
	
	public static class DisabledPeerBuilder implements Builder<DisabledPeer>
	{
		private String peerName;
		private int peerPort;
		private int listenerCount;
		private int maxIOCount;
		private String registryIP;
		private int registryPort;
		private ServerTask task;
		private boolean isServerDisabled;
		private long waitTime;
		
		public DisabledPeerBuilder()
		{
		}
		
		public DisabledPeerBuilder peerName(String peerName)
		{
			this.peerName = peerName;
			return this;
		}
		
		public DisabledPeerBuilder peerPort(int peerPort)
		{
			this.peerPort = peerPort;
			return this;
		}
		
		public DisabledPeerBuilder listenerCount(int listenerCount)
		{
			this.listenerCount = listenerCount;
			return this;
		}
		
		public DisabledPeerBuilder maxIOCount(int maxIOCount)
		{
			this.maxIOCount = maxIOCount;
			return this;
		}
		
		public DisabledPeerBuilder registryIP(String registryIP)
		{
			this.registryIP = registryIP;
			return this;
		}
		
		public DisabledPeerBuilder registryPort(int registryPort)
		{
			this.registryPort = registryPort;
			return this;
		}
		
		public DisabledPeerBuilder task(ServerTask task)
		{
			this.task = task;
			return this;
		}
		
		public DisabledPeerBuilder isServerDisabled(boolean isServerDisabled)
		{
			this.isServerDisabled = isServerDisabled;
			return this;
		}
		
		public DisabledPeerBuilder waitTime(long waitTime)
		{
			this.waitTime = waitTime;
			return this;
		}

		@Override
		public DisabledPeer build() throws IOException
		{
			return new DisabledPeer(this);
		}

		public String getPeerName()
		{
			return this.peerName;
		}
		
		public int getPeerPort()
		{
			return this.peerPort;
		}
		
		public String getRegistryIP()
		{
			return this.registryIP;
		}
		
		public int getRegistryPort()
		{
			return this.registryPort;
		}
		
		public int getListenerCount()
		{
			return this.listenerCount;
		}
		
		public int getMaxIOCount()
		{
			return this.maxIOCount;
		}
		
		public ServerTask getTask()
		{
			return this.task;
		}
		
		public boolean isServerDisabled()
		{
			return this.isServerDisabled;
		}
		
		public long getWaitTime()
		{
			return this.waitTime;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, InterruptedException, RemoteReadException, RemoteIPNotExistedException, IOException
	{
		this.partners.clear();
		this.rp.dispose();
		/*
		if (this.peer.isDisabled())
		{
			Scheduler.PERIOD().shutdown(timeout);
		}
		*/
		TerminateSignal.SIGNAL().notifyAllTermination();
		Scheduler.PERIOD().shutdown(timeout);
		this.peer.stop(timeout);
	}

	public void start() throws ClassNotFoundException, RemoteReadException, IOException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		/*
		if (this.peer.isDisabled())
		{
			Scheduler.PERIOD().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		}
		*/
		Scheduler.PERIOD().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		Scheduler.PERIOD().submit(new Poller(this), Config.POLLING_DELAY, Config.POLLING_PERIOD);
		this.peer.start();
	}

	/*
	public String getPeerName()
	{
		return this.peer.getPeerName();
	}
	*/

	public void processNotifiation(Notification notification)
	{
		this.peer.processNotification(notification);
	}
	
	public ServerMessage processRequest(Request request)
	{
		return this.peer.processRequest(request);
	}
	
	public void saveResponse(Response response)
	{
		this.rp.saveResponse(response);
	}
	
	public List<BrokerNotification> checkMessages() throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, BrokerClusterNotAvailableException, IOException
	{
		IPAddress broker = this.getBrokerIP();
		if (broker != null)
		{
			List<PollResponse> responses = this.peer.read(broker.getIP(), broker.getPort(), new PollRequest(this.peer.getPeerName()), PollResponse.class);
			List<BrokerNotification> notifications = new ArrayList<BrokerNotification>();
			for (PollResponse entry : responses)
			{
				/*
				 * It is weird that the program gets stuck without the below condition. 03/17/2023, Bing Li
				 */
				if (entry.getMessages() != null)
				{
					notifications.addAll(entry.getMessages());
				}
			}
			return notifications;
		}
		return null;
	}
	
	public void notifyBroker(String destinationName, Response response) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, BrokerClusterNotAvailableException, IOException, InterruptedException
	{
		IPAddress broker = this.getBrokerIP();
		this.peer.syncNotify(broker.getIP(), broker.getPort(), new BrokerNotification(this.peer.getPeerName(), destinationName, response));
	}

	public void notify(String peerName, Notification notification) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, IOException, InterruptedException, BrokerClusterNotAvailableException
	{
		IPAddress ip = this.getPeerIP(peerName);
		if (ip != UtilConfig.NO_IP_ADDRESS)
		{
			this.peer.syncNotify(ip.getIP(), ip.getPort(), notification);
		}
		else
		{
			ip = this.getBrokerIP(peerName);
			if (ip != UtilConfig.NO_IP_ADDRESS)
			{
				this.peer.syncNotify(ip.getIP(), ip.getPort(), new BrokerNotification(this.peer.getPeerName(), peerName, notification));
			}
		}
	}
	
	/*
	 * 
	 * Since the broker is a cluster, the method is useful to retain messages to the disabled peer. 03/08/2023, Bing Li
	 * 
	 * Just leave the method here. 03/07/2023, Bing Li
	 * 
	 * It is not reasonable for such a client to send messages to a server-disabled cluster. In other words, a cluster never disables its server character. 03/07/2023, Bing Li
	 * 
	 */
	/*
	public void notify(String peerName, ClusterNotification notification) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, IOException, InterruptedException, BrokerClusterNotAvailableException
	{
		IPAddress ip = this.getIP(peerName);
		if (ip != UtilConfig.NO_IP_ADDRESS)
		{
			this.peer.syncNotify(ip.getIP(), ip.getPort(), notification);
		}
	}
	*/

	public ServerMessage read(String peerName, Request request) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, BrokerClusterNotAvailableException, IOException, InterruptedException
	{
		IPAddress ip = this.getPeerIP(peerName);
		if (ip != UtilConfig.NO_IP_ADDRESS)
		{
//			log.info(peerName + "'s IP is obtained: " + ip);
			return this.peer.read(ip.getIP(), ip.getPort(), request);
		}
		else
		{
			ip = this.getBrokerIP(peerName);
			if (ip != UtilConfig.NO_IP_ADDRESS)
			{
//				log.info("Broker's IP is obtained: " + ip);
//				this.peer.read(ip.getIP(), ip.getPort(), request);
//				this.peer.read(ip.getIP(), ip.getPort(), new BrokerNotification(this.peer.getPeerName(), peerName, request));
				this.peer.syncNotify(ip.getIP(), ip.getPort(), new BrokerNotification(this.peer.getPeerName(), peerName, request));
				log.info("Waiting for request, " + request.getKey());
				return this.rp.waitResponse(request.getKey());
			}
		}
		return null;
	}

	/*
	 * 
	 * Since the broker is a cluster, the method is useful to retain messages to the disabled peer. 03/08/2023, Bing Li
	 * 
	 * Just leave the method here. 03/07/2023, Bing Li
	 * 
	 * It is not reasonable for such a client to send messages to a server-disabled cluster. In other words, a cluster never disables its server character. 03/07/2023, Bing Li
	 * 
	 */
	/*
	public <Response> List<Response> read(String peerName, ClusterRequest request, Class<Response> responseClass) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, BrokerClusterNotAvailableException
	{
		IPAddress ip = this.getIP(peerName);
		if (ip != UtilConfig.NO_IP_ADDRESS)
		{
			CollectedClusterResponse response = (CollectedClusterResponse)this.peer.read(ip.getIP(), ip.getPort(), request);
			return Tools.filter(response.getResponses(), responseClass);
		}
		return null;
	}
	*/
	
	private IPAddress getPeerIP(String peerName) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
		if (!this.partners.containsKey(peerName))
		{
			String peerKey = PeerContainer.getPeerKey(peerName);
			if (!this.peer.isServerDisabled(this.peer.getRegistryIP(), this.peer.getRegistryPort(), peerKey))
			{
				IPAddress ip = this.peer.getIPAddress(this.peer.getRegistryIP(), this.peer.getRegistryPort(), peerKey);
				this.partners.put(peerKey, ip);
				return ip;
			}
			return UtilConfig.NO_IP_ADDRESS;
		}
		else
		{
			return this.partners.get(peerName);
		}
	}
	
	private IPAddress getBrokerIP(String peerName) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, BrokerClusterNotAvailableException
	{
		if (this.brokers.size() <= 0)
		{
			SearchBrokerResponse br = ((SearchBrokerResponse)this.peer.read(this.peer.getRegistryIP(), this.peer.getRegistryPort(), new SearchBrokerRequest()));
			if (br.getBroker() != null)
			{
				this.brokers.put(br.getBroker().getPeerKey(), br.getBroker());
				return br.getBroker();
			}
			else
			{
				throw new BrokerClusterNotAvailableException(peerName);
			}
		}
		else
		{
			return this.brokers.get(Tools.getRandomSetElement(this.brokers.keySet()));
		}
	}
	
	private IPAddress getBrokerIP() throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException, BrokerClusterNotAvailableException
	{
		if (this.brokers.size() <= 0)
		{
			SearchBrokerResponse br = (SearchBrokerResponse)this.peer.read(this.peer.getRegistryIP(), this.peer.getRegistryPort(), new SearchBrokerRequest());
			if (br.getBroker() != null)
			{
				/*
				 * It is weird that the program gets stuck since the peer key is NULL. 03/17/2023, Bing Li
				 */
//				log.info("broker peer key = " + br.getBroker().getPeerKey());
				this.brokers.put(br.getBroker().getIPKey(), br.getBroker());
				return br.getBroker();
			}
			else
			{
				throw new BrokerClusterNotAvailableException();
			}
		}
		else
		{
			return this.brokers.get(Tools.getRandomSetElement(this.brokers.keySet()));
		}
	}
}
