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
package org.omnaest.usda;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.omnaest.usda.accessor.FoodsAndNutrientsAccessor;
import org.omnaest.usda.accessor.FoodsAndNutrientsAccessor.FoodsAndNutrientsAccessorWithLoadedData;
import org.omnaest.usda.domain.Food;
import org.omnaest.usda.domain.FoodsAndNutrients;
import org.omnaest.usda.domain.Nutrient;
import org.omnaest.utils.JSONHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class USDAFoodDBUtils
{
	private static final String	UTF_8	= "utf-8";
	private static final Logger	LOG		= LoggerFactory.getLogger(USDAFoodDBUtils.class);

	public static FoodsAndNutrientsAccessor getAccessorInstance(String apiKey)
	{
		return new FoodsAndNutrientsAccessorWithLoadedData()
		{
			private FoodsAndNutrients foodsAndNutrients;

			@Override
			public FoodsAndNutrientsAccessorWithLoadedData writeToFile(File file) throws IOException
			{
				FileUtils.writeStringToFile(file, JSONHelper.prettyPrint(this.foodsAndNutrients), UTF_8);
				return this;
			}

			@Override
			public FoodsAndNutrientsAccessorWithLoadedData readFromFileCacheOrLoadFromRestAPIIfNotPresent(File file) throws IOException
			{
				try
				{
					this.readFromFile(file);
				}
				catch (IOException e)
				{
					this.loadFromRestAPI();
					this.writeToFile(file);
				}
				return this;
			}

			@Override
			public FoodsAndNutrientsAccessorWithLoadedData readFromFile(File file) throws IOException
			{
				String json = FileUtils.readFileToString(file, UTF_8);
				if (StringUtils.isNotBlank(json))
				{
					this.foodsAndNutrients = JSONHelper.readFromString(json, FoodsAndNutrients.class);
				}
				return this;

			}

			@Override
			public FoodsAndNutrientsAccessorWithLoadedData loadFromRestAPI()
			{
				Map<String, Food> foodIdToFoodMap = new ConcurrentHashMap<>();

				List<Nutrient> nutrients = USDAFoodDBRestUtils.getNutrients(apiKey);
				Iterator<Nutrient> iterator = nutrients.iterator();

				while (iterator.hasNext())
				{
					List<String> ids = this.collectIds(iterator, 1);
					try
					{
						USDAFoodDBRestUtils	.resolveNutrientData(ids, apiKey)
											.forEach(food ->
											{
												foodIdToFoodMap	.computeIfAbsent(food.getNdbno(), (foodId) -> new Food(food.getNdbno(), food.getName()))
																.addNutrientValues(food.getNutrientValues());
											});
					}
					catch (Exception e)
					{
						LOG.error("Exception resolving data for " + ids, e);
					}
				}

				this.foodsAndNutrients = new FoodsAndNutrients(foodIdToFoodMap.values(), nutrients);
				return this;
			}

			@Override
			public FoodsAndNutrients get()
			{
				return this.foodsAndNutrients;
			}

			private List<String> collectIds(Iterator<Nutrient> iterator, int count)
			{
				List<String> retlist = new ArrayList<>();
				for (int ii = 0; ii < count && iterator.hasNext(); ii++)
				{
					retlist.add(iterator.next()
										.getId());
				}
				return retlist;
			}
		};

	}

}
