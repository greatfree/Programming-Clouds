package edu.chainnet.sc.collaborator.child.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.cache.db.DBEnv;
import org.greatfree.util.FileManager;
import org.greatfree.util.IPAddress;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityJoin;
import com.sleepycat.persist.ForwardCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

import edu.chainnet.sc.message.BCNode;

/*
 * BCNode is saved in the Berkeley Object-Oriented DB for Java. 10/18/2020, Bing Li 
 */

// Created: 10/18/2020, Bing Li
class BCNodeDB
{
	private File envPath;
	private DBEnv env;
	private BCNodeAccessor accessor;
	
	public BCNodeDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.BCNODE_STORE);
		this.accessor = new BCNodeAccessor(this.env.getEntityStore());
	}

	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}
	
	public void saveNode(BCNode node)
	{
		this.accessor.getPrimaryKey().put(new BCNodeEntity(node.getKey(), node.getName(), node.getIP().getIP(), node.getIP().getPort(), node.getServiceName(), node.getPartners(), node.getDescription()));
	}

	public BCNode getNodeByID(String nodeID)
	{
		BCNodeEntity entity = this.accessor.getPrimaryKey().get(nodeID);
		if (entity != null)
		{
			return new BCNode(entity.getName(), new IPAddress(entity.getIP(), entity.getPort()), entity.getServiceName(), entity.getPartners(), entity.getDescription());
		}
		return null;
	}
	
	public BCNode getNodeByName(String name)
	{
		BCNodeEntity entity = this.accessor.getPrimaryKey().get(BCNode.getKey(name));
		if (entity != null)
		{
			return new BCNode(entity.getName(), new IPAddress(entity.getIP(), entity.getPort()), entity.getServiceName(), entity.getPartners(), entity.getDescription());
		}
		return null;
	}
	
	public List<BCNode> getNodesByService(String serviceName)
	{
		PrimaryIndex<String, BCNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, BCNodeEntity.class);
		SecondaryIndex<String, String, BCNodeEntity> sIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, DBConfig.SERVICE_NAME);
		EntityJoin<String, BCNodeEntity> join = new EntityJoin<String, BCNodeEntity>(primaryIndex);
		join.addCondition(sIndex, serviceName);
		ForwardCursor<BCNodeEntity> results = join.entities();
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (BCNodeEntity entry : results)
		{
			nodes.add(new BCNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getPartners(), entry.getDescription()));
		}
		results.close();
		return nodes;
	}
	
	public List<BCNode> getNodesByDescription(String description)
	{
		EntityCursor<BCNodeEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, BCNodeEntity.class).entities();
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (BCNodeEntity entry : results)
		{
			if (entry.getDescription().indexOf(description) >= 0)
			{
				nodes.add(new BCNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getPartners(), entry.getDescription()));
			}
		}
		results.close();
		return nodes;
	}
}
