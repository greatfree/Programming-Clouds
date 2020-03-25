package org.greatfree.testing.cache.local;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.greatfree.cache.local.CacheMap;
import org.greatfree.util.Rand;

// Created: 12/24/2019, Bing Li
class CacheMapTester
{

	public static void main(String[] args)
	{
		CacheMap<MyObj, MyObjFactory> map = new CacheMap.CacheMapBuilder<MyObj, MyObjFactory>()
				.factory(new MyObjFactory())
				.rootPath("/Users/libing/Temp/")
				.cacheKey("mymap")
				.cacheSize(100)
				.offheapSizeInMB(4)
				.diskSizeInMB(10)
				.build();

		MyObj obj;
		String key;
		Map<String, MyObj> objs = new HashMap<String, MyObj>();
		for (int i = 0; i < 15; i++)
		{
			key = "key" + i;
			obj = new MyObj(key, Rand.getRandom(100));
//			map.put(key, obj);
			objs.put(obj.getKey(), obj);
		}
		map.putAll(objs);
		
		for (int i = 0; i < 15; i++)
		{
			key = "key" + i;
			obj = new MyObj(key, 100);
//			map.put(key, obj);
			objs.put(obj.getKey(), obj);
		}
		map.putAll(objs);
		

		for (int i = 0; i < 15; i++)
		{
			key = "key" + i;
			obj = map.get(key);
			System.out.println(obj.getKey() + ", " + obj.getValue());
		}
		
		System.out.println("==========================");
		
		Set<String> keys = map.getKeys();
		for (String entry : keys)
		{
			obj = map.get(entry);
			System.out.println(obj.getKey() + ", " + obj.getValue());
		}
		
		map.close();
	}

}
