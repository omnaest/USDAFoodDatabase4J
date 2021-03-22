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
package org.omnaest.usda.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.omnaest.usda.domain.Food;
import org.omnaest.usda.domain.Nutrient;
import org.omnaest.usda.domain.NutrientValue;
import org.omnaest.usda.domain.response.NutrientsResponse;
import org.omnaest.usda.domain.response.NutrientsResponse.Content;
import org.omnaest.usda.domain.response.NutrientsResponse.Content.Item;
import org.omnaest.utils.JSONHelper;
import org.omnaest.utils.rest.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class USDAFoodDBRestUtils
{
    private static final Logger LOG = LoggerFactory.getLogger(USDAFoodDBRestUtils.class);

    public static List<Nutrient> getNutrients(String apiKey)
    {
        List<Nutrient> retlist = new ArrayList<>();

        RestClient restClient = getRestClient();
        NutrientsResponse nutrientsResponse = restClient.requestGet("http://api.nal.usda.gov/ndb/list?format=json&lt=n&sort=n&max=1500&api_key=" + apiKey,
                                                                    NutrientsResponse.class);
        if (nutrientsResponse != null)
        {
            Content content = nutrientsResponse.getList();
            if (content != null)
            {
                List<Item> items = content.getItem();
                if (items != null)
                {
                    retlist.addAll(items.stream()
                                        .map(item -> new Nutrient(item.getId(), item.getName()))
                                        .collect(Collectors.toSet()));
                }
            }
        }

        return retlist;
    }

    private static RestClient getRestClient()
    {
        return RestClient.newJSONRestClient();
    }

    public static List<Food> resolveNutrientData(List<String> nutrientIds, String apiKey)
    {
        List<Food> retlist = new ArrayList<>();

        Integer end = -1;
        Integer total = 0;

        while (end < total)
        {
            StringBuilder nutrientsSB = new StringBuilder();
            for (String nutrientId : nutrientIds)
            {
                nutrientsSB.append("&nutrients=" + nutrientId);
            }

            String url = "http://api.nal.usda.gov/ndb/nutrients/?format=json&api_key=" + apiKey + nutrientsSB.toString() + "&max=1500&offset=" + (end + 1);
            LOG.info("Requesting data from usda database url: " + url);
            @SuppressWarnings("unchecked")
            Map<String, Object> data = getRestClient().requestGet(url, Map.class);
            if (data == null)
            {
                throw new RuntimeException("Unexpected empty data");
            }
            else
            {
                Map<String, Object> report = (Map<String, Object>) data.get("report");
                if (report == null)
                {
                    throw new RuntimeException("Unexpected empty report \n" + JSONHelper.prettyPrint(data));
                }
                else
                {
                    end = (Integer) report.get("end");
                    total = (Integer) report.get("total");

                    List<Map<String, Object>> foods = (List<Map<String, Object>>) report.get("foods");
                    if (foods == null || foods.isEmpty())
                    {
                        throw new RuntimeException("Unexpected empty food list \n" + JSONHelper.prettyPrint(data));
                    }
                    else
                    {
                        for (Map<String, Object> food : foods)
                        {
                            String name = (String) food.get("name");
                            String ndbno = (String) food.get("ndbno");
                            Double weight = (Double) food.get("weight");

                            List<Map<String, Object>> nutrientsData = (List<Map<String, Object>>) food.get("nutrients");
                            if (nutrientsData != null && !nutrientsData.isEmpty())
                            {
                                List<NutrientValue> nutrientValues = new ArrayList<>();
                                for (Map<String, Object> nutrientData : nutrientsData)
                                {
                                    String id = (String) nutrientData.get("nutrient_id");
                                    String unit = (String) nutrientData.get("unit");
                                    String valueStr = (String) nutrientData.get("value");

                                    if (!nutrientIds.contains(id))
                                    {
                                        LOG.warn("Wrong nutrient id: " + id);
                                    }
                                    else if (StringUtils.equalsIgnoreCase("--", valueStr))
                                    {
                                        LOG.trace("Ignoring empty value");
                                    }
                                    else
                                    {
                                        Double value = Double.valueOf(valueStr);
                                        double normalizedValue = value.doubleValue() / weight.doubleValue();

                                        nutrientValues.add(new NutrientValue(id, normalizedValue, unit));
                                    }
                                }

                                if (!nutrientValues.isEmpty())
                                {
                                    Food foodInfo = new Food(ndbno, name);
                                    foodInfo.addNutrientValues(nutrientValues);
                                    retlist.add(foodInfo);
                                }
                            }
                        }
                    }
                }
            }
        }

        return retlist;
    }

    public static byte[] downloadCurrentDatabase()
    {
        return RestClient.newByteArrayRestClient()
                         .request()
                         .toUrl("https://fdc.nal.usda.gov/fdc-datasets/FoodData_Central_csv_2020-10-30.zip")
                         .get(byte[].class);
    }
}
