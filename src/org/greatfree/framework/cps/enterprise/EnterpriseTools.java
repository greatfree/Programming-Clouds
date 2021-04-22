package org.greatfree.framework.cps.enterprise;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.greatfree.util.Tools;

// Created: 04/23/2020, Bing Li
public class EnterpriseTools
{
	public static String getClientKey(String name)
	{
		return Tools.getHash(name);
	}
	
	public static String getObjectKey(String clientKey, String className)
	{
		return Tools.getHash(clientKey + className);
	}
	
	public static Object getObject(String className, List<String> parameterTypes, Object[] parameterValues) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Class<?> sessionClass = Class.forName(className);
		Class<?>[] parameters = EnterpriseTools.getParameters(parameterTypes);
		Constructor<?> constructor = sessionClass.getConstructor(parameters);
		return constructor.newInstance(parameterValues);
	}
	
	public static Method getMethod(String className, String methodName, List<String> parameterTypes) throws ClassNotFoundException, NoSuchMethodException, SecurityException
	{
		Class<?> sessionClass = Class.forName(className);
		Class<?>[] parameters = EnterpriseTools.getParameters(parameterTypes);
		return sessionClass.getMethod(methodName, parameters);
	}

	public static Class<?>[] getParameters(List<String> parameterTypes) throws ClassNotFoundException
	{
		Class<?>[] parameters = new Class<?>[parameterTypes.size()];
		int index = 0;
		for (String entry : parameterTypes)
		{
			switch (entry)
			{
				case EnterpriseConfig.INT:
					parameters[index++] = int.class;
					break;

				case EnterpriseConfig.FLOAT:
					parameters[index++] = float.class;
					break;

				case EnterpriseConfig.DOUBLE:
					parameters[index++] = double.class;
					break;

				case EnterpriseConfig.STRING:
					parameters[index++] = String.class;
					break;
					
				default:
					parameters[index++] = Class.forName(entry);
					break;
			}
		}
		return parameters;
	}

}
