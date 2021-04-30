package edu.chainnet.center.child;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.queryparser.classic.ParseException;
import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

import edu.chainnet.center.CenterConfig;
import edu.chainnet.center.child.lucene.ChildProfile;
import edu.chainnet.center.child.lucene.Page;
import edu.chainnet.center.child.lucene.PageCache;
import edu.chainnet.center.child.lucene.Searcher;
import edu.chainnet.center.message.CenterApplicationID;
import edu.chainnet.center.message.SearchRequest;
import edu.chainnet.center.message.SearchResponse;
import edu.chainnet.crawler.AuthoritySolrValue;
import edu.chainnet.crawler.message.CrawlingApplicationID;
import edu.chainnet.crawler.message.UploadPagesNotification;

// Created: 04/22/2021, Bing Li
class DataChildTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.center.child");

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CrawlingApplicationID.UPLOAD_PAGES_NOTIFICATION:
				log.info("UPLOAD_PAGES_NOTIFICATION received @" + Calendar.getInstance().getTime());
				UploadPagesNotification upn = (UploadPagesNotification)notification;
				for (AuthoritySolrValue entry : upn.getPages())
				{
					PageCache.CENTER().enqueue(entry.authorityKey);
					PageCache.CENTER().put(new Page(entry.authorityKey, entry.url, entry.title, entry.content, entry.time, entry.hostHubURL));
				}
				break;
				
			case CenterApplicationID.PAUSE_INDEXING_NOTIFICATION:
				log.info("PAUSE_INDEXING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ChildProfile.CENTER().pause();
				break;
				
			case CenterApplicationID.RESUME_INDEXING_NOTIFICATION:
				log.info("RESUME_INDEXING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ChildProfile.CENTER().resume();
				break;
				
			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				log.info("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					DataChild.CENTER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
				
			case CenterApplicationID.STOP_CENTER_CHILDREN_NOTIFICATION:
				log.info("STOP_CENTER_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					DataChild.CENTER().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public MulticastResponse processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case CenterApplicationID.SEARCH_REQUEST:
				System.out.println("SEARCH_REQUEST received @" + Calendar.getInstance().getTime());
				SearchRequest sr = (SearchRequest)request;
				try
				{
					List<String> results = Searcher.perform(ChildProfile.CENTER().getDocPath(), ChildProfile.CENTER().getIndexPath(), sr.getQuery(), CenterConfig.HITS_PER_PAGE);
					return new SearchResponse(PageCache.CENTER().getIndexes(results), sr.getCollaboratorKey());
				}
				catch (IOException | ParseException e)
				{
					e.printStackTrace();
				}
				
				break;
		}
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processResponse(Response response)
	{
		// TODO Auto-generated method stub
		
	}

}
