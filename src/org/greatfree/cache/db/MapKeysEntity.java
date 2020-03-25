package org.greatfree.cache.db;

import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;

import org.greatfree.util.Tools;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

/*
 * When the size of the value keys of one map is large, the DB throws the exception of java.lang.ArrayIndexOutOfBoundsException. So I need to update the entity. That is, each cache key saves one value key only. 10/14/2019, Bing Li 
 */

// Created: 05/07/2018, Bing Li
@Entity
class MapKeysEntity
{
	@PrimaryKey
	private String key;
	
	@SecondaryKey(relate=MANY_TO_ONE)
	private String mapKey;

//	private Set<String> mapKeys;
	private String valueKey;
	
	public MapKeysEntity()
	{
	}

//	public MapKeysEntity(String key, Set<String> mapKeys)
	public MapKeysEntity(String mapKey, String valueKey)
	{
		this.key = Tools.generateUniqueKey();
		this.mapKey = mapKey;
		this.valueKey = valueKey;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getKey()
	{
		return this.key;
	}

	public void setMapKey(String mapKey)
	{
		this.mapKey = mapKey;
	}
	
	public String getMapKey()
	{
		return this.mapKey;
	}

	public void setValueKey(String valueKey)
	{
		this.valueKey = valueKey;
	}
	
	public String getValueKey()
	{
		return this.valueKey;
	}
}
