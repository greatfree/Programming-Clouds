package us.treez.inventory.db;

import java.io.File;
import java.util.ArrayList;

import java.util.List;
import org.greatfree.cache.db.DBEnv;
import org.greatfree.util.FileManager;
import org.greatfree.util.Tools;

import com.sleepycat.persist.EntityJoin;
import com.sleepycat.persist.ForwardCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

import us.treez.inventory.Order;
import us.treez.inventory.TreezConfig;

// Created: 02/05/2020, Bing Li
class OrderDB
{
	private File envPath;
	private DBEnv env;
	private OrderAccessor accessor;

	public OrderDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, TreezConfig.DB_CACHE_SIZE, TreezConfig.LOCK_TIME_OUT, TreezConfig.ORDER_STORE);
		this.accessor = new OrderAccessor(this.env.getEntityStore());
	}

	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}
	
	public void putOrder(Order order)
	{
		this.accessor.getPrimaryIndex().put(new OrderEntity(Tools.generateUniqueKey(), order.getMerchandiseKey(), order.getCustomerEmail(), order.getOrderPlacedDate(), order.getStatus(), order.getOrderedCount(), order.getPayment()));
	}
	
	public Order getOrder(String orderKey)
	{
		OrderEntity entry = this.accessor.getPrimaryIndex().get(orderKey);
		if (entry != null)
		{
			return new Order(entry.getKey(), entry.getMerchandiseKey(), entry.getCustomerEmail(), entry.getOrderPlacedDate(), entry.getStatus(), entry.getOrderedCount(), entry.getPayment());
		}
		return null;
	}

	public List<Order> getOrders(String merchandiseKey)
	{
		PrimaryIndex<String, OrderEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, OrderEntity.class);
		SecondaryIndex<String, String, OrderEntity> merchandiseKeyIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, TreezConfig.MERCHANDISE_KEY);
		EntityJoin<String, OrderEntity> join = new EntityJoin<String, OrderEntity>(primaryIndex);
		join.addCondition(merchandiseKeyIndex, merchandiseKey);
		ForwardCursor<OrderEntity> results = join.entities();
		List<Order> orders = new ArrayList<Order>();
		for (OrderEntity entry : results)
		{
			orders.add(new Order(entry.getKey(), entry.getMerchandiseKey(), entry.getCustomerEmail(), entry.getOrderPlacedDate(), entry.getStatus(), entry.getOrderedCount(), entry.getPayment()));
		}
		results.close();
		return orders;
	}
	
	public void update(Order order)
	{
		this.accessor.getPrimaryIndex().put(new OrderEntity(order.getKey(), order.getMerchandiseKey(), order.getCustomerEmail(), order.getOrderPlacedDate(), order.getStatus(), order.getOrderedCount(), order.getPayment()));
	}
	
	public void remove(String key)
	{
		this.env.getEntityStore().getPrimaryIndex(String.class, OrderEntity.class).delete(key);
	}
}
