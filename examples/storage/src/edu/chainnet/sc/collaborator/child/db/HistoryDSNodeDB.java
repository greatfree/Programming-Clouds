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

import edu.chainnet.sc.message.DSNode;

/*
 * Deprecated DSNode is saved in the Berkeley Object-Oriented DB for Java. 10/19/2020, Bing Li 
 */

// Created: 10/19/2020, Bing Li
class HistoryDSNodeDB
{
	private File envPath;
	private DBEnv env;
	private HistoryDSNodeAccessor accessor;
	
	public HistoryDSNodeDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.HISTORY_DSNODE_STORE);
		this.accessor = new HistoryDSNodeAccessor(this.env.getEntityStore());
	}

	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}
	
	public void saveNode(DSNode node, int version, int nodeType)
	{
//		this.accessor.getPrimaryKey().put(new HistoryDSNodeEntity(DBConfig.getHistoryDSKey(node.getKey(), version, nodeType), node.getKey(), version, nodeType, node.getName(), node.getIP().getIP(), node.getIP().getPort(), node.getServiceName(), node.getDescription()));
		this.accessor.getPrimaryKey().put(new HistoryDSNodeEntity(DBConfig.getHistoryKey(node.getKey(), version), node.getKey(), version, nodeType, node.getName(), node.getIP().getIP(), node.getIP().getPort(), node.getServiceName(), node.getDescription()));
	}

//	public DSNode getNodeByID(String nodeID, int version, int nodeType)
	public DSNode getNodeByID(String nodeID, int version)
	{
//		HistoryDSNodeEntity entity = this.accessor.getPrimaryKey().get(DBConfig.getHistoryDSKey(nodeID, version, nodeType));
		HistoryDSNodeEntity entity = this.accessor.getPrimaryKey().get(DBConfig.getHistoryKey(nodeID, version));
		if (entity != null)
		{
			return new DSNode(entity.getName(), new IPAddress(entity.getIP(), entity.getPort()), entity.getServiceName(), entity.getDescription());
		}
		return null;
	}
	
	public DSNode getNodeByKey(String key)
	{
		HistoryDSNodeEntity entity = this.accessor.getPrimaryKey().get(key);
		if (entity != null)
		{
			return new DSNode(entity.getName(), new IPAddress(entity.getIP(), entity.getPort()), entity.getServiceName(), entity.getDescription());
		}
		return null;
	}
	
//	public List<DSNode> getNodesByID(String nodeID, int nodeType)
	public List<DSNode> getNodesByID(String nodeID)
	{
		PrimaryIndex<String, HistoryDSNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryDSNodeEntity.class);
		SecondaryIndex<String, String, HistoryDSNodeEntity> sIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, DBConfig.NODE_ID);
//		SecondaryIndex<Integer, String, HistoryDSNodeEntity> ntIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, Integer.class, DBConfig.NODE_TYPE);
		EntityJoin<String, HistoryDSNodeEntity> join = new EntityJoin<String, HistoryDSNodeEntity>(primaryIndex);
		join.addCondition(sIndex, nodeID);
//		join.addCondition(ntIndex, nodeType);
		ForwardCursor<HistoryDSNodeEntity> results = join.entities();
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (HistoryDSNodeEntity entry : results)
		{
			nodes.add(new DSNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getDescription()));
		}
		results.close();
		return nodes;
	}

//	public List<DSNode> getNodesByName(String name, int nodeType)
	public List<DSNode> getNodesByName(String name)
	{
		String nodeID = DSNode.getKey(name);
		PrimaryIndex<String, HistoryDSNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryDSNodeEntity.class);
		SecondaryIndex<String, String, HistoryDSNodeEntity> sIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, DBConfig.NODE_ID);
//		SecondaryIndex<Integer, String, HistoryDSNodeEntity> ntIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, Integer.class, DBConfig.NODE_TYPE);
		EntityJoin<String, HistoryDSNodeEntity> join = new EntityJoin<String, HistoryDSNodeEntity>(primaryIndex);
		join.addCondition(sIndex, nodeID);
//		join.addCondition(ntIndex, nodeType);
		ForwardCursor<HistoryDSNodeEntity> results = join.entities();
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (HistoryDSNodeEntity entry : results)
		{
			nodes.add(new DSNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getDescription()));
		}
		results.close();
		return nodes;
	}

//	public int getVersionSize(String nodeID, int nodeType)
	public int getVersionSize(String nodeID)
	{
		PrimaryIndex<String, HistoryDSNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryDSNodeEntity.class);
		SecondaryIndex<String, String, HistoryDSNodeEntity> sIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, DBConfig.NODE_ID);
//		SecondaryIndex<Integer, String, HistoryDSNodeEntity> ntIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, Integer.class, DBConfig.NODE_TYPE);
		EntityJoin<String, HistoryDSNodeEntity> join = new EntityJoin<String, HistoryDSNodeEntity>(primaryIndex);
		join.addCondition(sIndex, nodeID);
//		join.addCondition(ntIndex, nodeType);
		ForwardCursor<HistoryDSNodeEntity> results = join.entities();
		int size = 1;
		for (HistoryDSNodeEntity entry : results)
		{
			// The below is used for removing warnings only. 10/19/2020, Bing Li
			entry.getVersion();
			size++;
		}
		results.close();
		return size;
	}
	
	public List<DSNode> getNodesByService(String serviceName, int nodeType)
	{
		PrimaryIndex<String, HistoryDSNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryDSNodeEntity.class);
		SecondaryIndex<String, String, HistoryDSNodeEntity> sIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, DBConfig.SERVICE_NAME);
		SecondaryIndex<Integer, String, HistoryDSNodeEntity> ntIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, Integer.class, DBConfig.NODE_TYPE);
		EntityJoin<String, HistoryDSNodeEntity> join = new EntityJoin<String, HistoryDSNodeEntity>(primaryIndex);
		join.addCondition(sIndex, serviceName);
		join.addCondition(ntIndex, nodeType);
		ForwardCursor<HistoryDSNodeEntity> results = join.entities();
		join.addCondition(ntIndex, nodeType);
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (HistoryDSNodeEntity entry : results)
		{
			nodes.add(new DSNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getDescription()));
		}
		results.close();
		return nodes;
	}
	
	public List<DSNode> getNodesByVersion(int version, int nodeType)
	{
		PrimaryIndex<String, HistoryDSNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryDSNodeEntity.class);
		SecondaryIndex<Integer, String, HistoryDSNodeEntity> vIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, Integer.class, DBConfig.VERSION);
		SecondaryIndex<Integer, String, HistoryDSNodeEntity> ntIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, Integer.class, DBConfig.NODE_TYPE);
		EntityJoin<String, HistoryDSNodeEntity> join = new EntityJoin<String, HistoryDSNodeEntity>(primaryIndex);
		join.addCondition(vIndex, version);
		join.addCondition(ntIndex, nodeType);
		ForwardCursor<HistoryDSNodeEntity> results = join.entities();
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (HistoryDSNodeEntity entry : results)
		{
			nodes.add(new DSNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getDescription()));
		}
		results.close();
		return nodes;
	}
	
	public List<DSNode> getNodesByDescription(String description)
	{
		EntityCursor<HistoryDSNodeEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, HistoryDSNodeEntity.class).entities();
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (HistoryDSNodeEntity entry : results)
		{
			if (entry.getDescription().indexOf(description) >= 0)
			{
				nodes.add(new DSNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getDescription()));
			}
		}
		results.close();
		return nodes;
	}

}
