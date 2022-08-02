package org.greatfree.testing.cache.distributed;

import java.util.HashMap;
import java.util.Map;

import org.greatfree.abandoned.cache.distributed.MapValueReceivable;

// Created: 07/04/2017, Bing LI
class AddDistributedMap
{

	public static void main(String[] args)
	{
		Map<String, MapValueReceivable<String, TestPointing>> maps = new HashMap<String, MapValueReceivable<String, TestPointing>>();
		
//		maps.put("001", new DistributedPersistableMap<String, MyPointing, MyFactory, MyDB>("001"));
//		maps.put("002", new DistributedPersistableMap<String, MyPointing, MyFactory, MyDB>("002"));
		maps.put("003", new MyMap("003"));
		
		maps.get("001").valueReceived("x001", new TestPointing("x001"));
		maps.get("002").valueReceived("x002", new TestPointing("x002"));
		maps.get("003").valueReceived("x003", new TestPointing("x003"));

	}

}
