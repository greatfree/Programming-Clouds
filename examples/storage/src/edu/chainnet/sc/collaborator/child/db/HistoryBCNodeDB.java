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
 * Deprecated BCNode is saved in the Berkeley Object-Oriented DB for Java. 10/18/2020, Bing Li 
 */

// Created: 10/18/2020, Bing Li
class HistoryBCNodeDB
{
	private File envPath;
	private DBEnv env;
	private HistoryBCNodeAccessor accessor;
	
	public HistoryBCNodeDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.HISTORY_BCNODE_STORE);
		this.accessor = new HistoryBCNodeAccessor(this.env.getEntityStore());
	}

	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}
	
	public void saveNode(BCNode node, int version)
	{
		this.accessor.getPrimaryKey().put(new HistoryBCNodeEntity(DBConfig.getHistoryKey(node.getKey(), version), node.getKey(), version, node.getName(), node.getIP().getIP(), node.getIP().getPort(), node.getServiceName(), node.getPartners(), node.getDescription()));
	}

	public BCNode getNodeByID(String nodeID, int version)
	{
		HistoryBCNodeEntity entity = this.accessor.getPrimaryKey().get(DBConfig.getHistoryKey(nodeID, version));
		if (entity != null)
		{
			return new BCNode(entity.getName(), new IPAddress(entity.getIP(), entity.getPort()), entity.getServiceName(), entity.getPartners(), entity.getDescription());
		}
		return null;
	}
	
	public BCNode getNodeByKey(String key)
	{
		HistoryBCNodeEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != null)
		{
			return new BCNode(entity.getName(), new IPAddress(entity.getIP(), entity.getPort()), entity.getServiceName(), entity.getPartners(), entity.getDescription());
		}
		return null;
	}
	
	public List<BCNode> getNodesByID(String nodeID)
	{
		PrimaryIndex<String, HistoryBCNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryBCNodeEntity.class);
		SecondaryIndex<String, String, HistoryBCNodeEntity> sIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, DBConfig.NODE_ID);
		EntityJoin<String, HistoryBCNodeEntity> join = new EntityJoin<String, HistoryBCNodeEntity>(primaryIndex);
		join.addCondition(sIndex, nodeID);
		ForwardCursor<HistoryBCNodeEntity> results = join.entities();
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (HistoryBCNodeEntity entry : results)
		{
			nodes.add(new BCNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getPartners(), entry.getDescription()));
		}
		results.close();
		return nodes;
	}
	
	public List<BCNode> getNodesByName(String name)
	{
		PrimaryIndex<String, HistoryBCNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryBCNodeEntity.class);
		SecondaryIndex<String, String, HistoryBCNodeEntity> sIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, DBConfig.NODE_ID);
		EntityJoin<String, HistoryBCNodeEntity> join = new EntityJoin<String, HistoryBCNodeEntity>(primaryIndex);
		join.addCondition(sIndex, BCNode.getKey(name));
		ForwardCursor<HistoryBCNodeEntity> results = join.entities();
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (HistoryBCNodeEntity entry : results)
		{
			nodes.add(new BCNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getPartners(), entry.getDescription()));
		}
		results.close();
		return nodes;
	}
	
	public int getVersionSize(String nodeID)
	{
		PrimaryIndex<String, HistoryBCNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryBCNodeEntity.class);
		SecondaryIndex<String, String, HistoryBCNodeEntity> sIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, DBConfig.NODE_ID);
		EntityJoin<String, HistoryBCNodeEntity> join = new EntityJoin<String, HistoryBCNodeEntity>(primaryIndex);
		join.addCondition(sIndex, nodeID);
		ForwardCursor<HistoryBCNodeEntity> results = join.entities();
		int size = 1;
		for (HistoryBCNodeEntity entry : results)
		{
			// The below is used for removing warnings only. 10/19/2020, Bing Li
			entry.getVersion();
			size++;
		}
		results.close();
		return size;
	}
	
	public List<BCNode> getNodesByService(String serviceName)
	{
		PrimaryIndex<String, HistoryBCNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryBCNodeEntity.class);
		SecondaryIndex<String, String, HistoryBCNodeEntity> sIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, DBConfig.SERVICE_NAME);
		EntityJoin<String, HistoryBCNodeEntity> join = new EntityJoin<String, HistoryBCNodeEntity>(primaryIndex);
		join.addCondition(sIndex, serviceName);
		ForwardCursor<HistoryBCNodeEntity> results = join.entities();
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (HistoryBCNodeEntity entry : results)
		{
			nodes.add(new BCNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getPartners(), entry.getDescription()));
		}
		results.close();
		return nodes;
	}
	
	public List<BCNode> getNodesByVersion(int version)
	{
		PrimaryIndex<String, HistoryBCNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryBCNodeEntity.class);
		SecondaryIndex<Integer, String, HistoryBCNodeEntity> vIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, Integer.class, DBConfig.VERSION);
		EntityJoin<String, HistoryBCNodeEntity> join = new EntityJoin<String, HistoryBCNodeEntity>(primaryIndex);
		join.addCondition(vIndex, version);
		ForwardCursor<HistoryBCNodeEntity> results = join.entities();
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (HistoryBCNodeEntity entry : results)
		{
			nodes.add(new BCNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getPartners(), entry.getDescription()));
		}
		results.close();
		return nodes;
	}
	
	public List<BCNode> getNodesByDescription(String description)
	{
		EntityCursor<HistoryBCNodeEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryBCNodeEntity.class).entities();
		List<BCNode> nodes = new ArrayList<BCNode>();
		for (HistoryBCNodeEntity entry : results)
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
