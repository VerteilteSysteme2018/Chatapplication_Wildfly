package org.chat.client.gui;

import java.util.ArrayList;
import java.util.List;

public class JsonConverter 
{
	public JsonConverter() {}

	public List<String> formatToList (String str) 
	{
		List<String> list = new ArrayList<String>();
		str = str.substring(2, str.length()-2);
		String sa[] = str.split("}");
		sa[0] = " {" + sa[0];
		
		for (String x : sa) 
		{
			list.add(x.substring(1) + "}");
		}
		
		sa = null;
		return list;
	}
		
	public String[] getNames(List<String> list) 
	{
		if (list == null) 
		{
			return new String[1];
		}
		
		String[] listEntries = new String[list.size()];
		String tmp = null;
		for (int i = 0; i < list.size(); i++) 
		{
			if (! list.get(i).contains("\"name\":\"")) 
			{
				System.out.println("WARN: name could not be parsed: " + list.get(i));
				listEntries[i] = list.get(i);
			}
			else 
			{
				tmp = (list.get(i)).split("\"name\":\"")[1];
				listEntries[i] = tmp.split("\"")[0].trim();
			}
		}
		return listEntries;
	}
}
