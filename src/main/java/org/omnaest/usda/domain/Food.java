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
package org.omnaest.usda.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Food
{
	private String						ndbno;
	private String						name;
	private Map<String, NutrientValue>	nutrientIdToValueMap	= new LinkedHashMap<>();

	public Food(String ndbno, String name)
	{
		super();
		this.ndbno = ndbno;
		this.name = name;
	}

	@JsonCreator
	public Food(@JsonProperty("ndbno") String ndbno, @JsonProperty("name") String name, @JsonProperty("nutrientValues") List<NutrientValue> nutrientValues)
	{
		super();
		this.ndbno = ndbno;
		this.name = name;
		if (nutrientValues != null)
		{
			this.nutrientIdToValueMap.putAll(nutrientValues	.stream()
															.collect(Collectors.toMap(value -> value.getNutrientId(), value -> value)));
		}
	}

	public Food()
	{
		super();
	}

	public String getNdbno()
	{
		return this.ndbno;
	}

	public void setNdbno(String ndbno)
	{
		this.ndbno = ndbno;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addNutrientValue(NutrientValue nutrientValue)
	{
		this.nutrientIdToValueMap.computeIfAbsent(nutrientValue.getNutrientId(), (id) -> nutrientValue);
	}

	public void addNutrientValues(List<NutrientValue> nutrientValues)
	{
		if (nutrientValues != null)
		{
			for (NutrientValue nutrientValue : nutrientValues)
			{
				this.addNutrientValue(nutrientValue);
			}
		}
	}

	public List<NutrientValue> getNutrientValues()
	{
		return this.nutrientIdToValueMap.values()
										.stream()
										.collect(Collectors.toList());
	}

	@Override
	public String toString()
	{
		return "FoodInfo [ndbno=" + this.ndbno + ", name=" + this.name + ", nutrientIdToValueMap=" + this.nutrientIdToValueMap + "]";
	}

}