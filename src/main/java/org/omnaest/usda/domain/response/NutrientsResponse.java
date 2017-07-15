/* 

	Copyright 2017 Danny Kunz

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
	

*/
package org.omnaest.usda.domain.response;

import java.util.List;

public class NutrientsResponse
{
	private NutrientsResponse.Content list;

	public static class Content
	{
		private String				lt;
		private int					start;
		private int					end;
		private int					total;
		private String				sr;
		private String				sort;
		private List<Content.Item>	item;

		public static class Item
		{
			private int		offset;
			private String	id;
			private String	name;

			public int getOffset()
			{
				return this.offset;
			}

			public void setOffset(int offset)
			{
				this.offset = offset;
			}

			public String getId()
			{
				return this.id;
			}

			public void setId(String id)
			{
				this.id = id;
			}

			public String getName()
			{
				return this.name;
			}

			public void setName(String name)
			{
				this.name = name;
			}

		}

		public String getLt()
		{
			return this.lt;
		}

		public void setLt(String lt)
		{
			this.lt = lt;
		}

		public int getStart()
		{
			return this.start;
		}

		public void setStart(int start)
		{
			this.start = start;
		}

		public int getEnd()
		{
			return this.end;
		}

		public void setEnd(int end)
		{
			this.end = end;
		}

		public int getTotal()
		{
			return this.total;
		}

		public void setTotal(int total)
		{
			this.total = total;
		}

		public String getSr()
		{
			return this.sr;
		}

		public void setSr(String sr)
		{
			this.sr = sr;
		}

		public String getSort()
		{
			return this.sort;
		}

		public void setSort(String sort)
		{
			this.sort = sort;
		}

		public List<Content.Item> getItem()
		{
			return this.item;
		}

		public void setItem(List<Content.Item> items)
		{
			this.item = items;
		}

	}

	public NutrientsResponse.Content getList()
	{
		return this.list;
	}

	public void setList(NutrientsResponse.Content list)
	{
		this.list = list;
	}

}