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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FoodsAndNutrients
{
	private List<Food>		foods		= new ArrayList<>();
	private List<Nutrient>	nutrients	= new ArrayList<>();

	@JsonCreator
	public FoodsAndNutrients(@JsonProperty("foods") List<Food> foods, @JsonProperty("nutrients") List<Nutrient> nutrients)
	{
		this((Collection<Food>) foods, (Collection<Nutrient>) nutrients);
	}

	public FoodsAndNutrients(Collection<Food> foods, Collection<Nutrient> nutrients)
	{
		super();
		this.nutrients.addAll(nutrients);
		this.foods.addAll(foods);
	}

	public List<Food> getFoods()
	{
		return this.foods;
	}

	public List<Nutrient> getNutrients()
	{
		return this.nutrients;
	}

}
