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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Ignore;
import org.junit.Test;
import org.omnaest.pdf.PDFUtils;
import org.omnaest.pdf.PDFUtils.PDFBuilderWithPage;
import org.omnaest.usda.domain.Food;
import org.omnaest.usda.domain.FoodsAndNutrients;
import org.omnaest.usda.domain.Nutrient;
import org.omnaest.usda.domain.NutrientValue;
import org.omnaest.usda.domain.index.FoodIndex;
import org.omnaest.usda.domain.index.MeasureUnit;

public class USDAFoodDBUtilsTest
{
    private static final String API_KEY = Constants.API_KEY;

    public enum Intensity
    {
        VERY_LOW, LOW, HIGH, VERY_HIGH
    }

    public static class FoodAndValue
    {
        private Food          food;
        private NutrientValue value;

        public FoodAndValue(Food food, NutrientValue value)
        {
            super();
            this.food = food;
            this.value = value;
        }

        public Food getFood()
        {
            return this.food;
        }

        public NutrientValue getValue()
        {
            return this.value;
        }

    }

    public static class Bucket
    {
        private double min;
        private double max;

        public Bucket(double min, double max)
        {
            super();
            this.min = min;
            this.max = max;
        }

        public double getMin()
        {
            return this.min;
        }

        public double getMax()
        {
            return this.max;
        }

        @Override
        public String toString()
        {
            return "Bucket [min=" + this.min + ", max=" + this.max + "]";
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 1;
            long temp;
            temp = Double.doubleToLongBits(this.max);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(this.min);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            if (obj == null)
            {
                return false;
            }
            if (this.getClass() != obj.getClass())
            {
                return false;
            }
            Bucket other = (Bucket) obj;
            if (Double.doubleToLongBits(this.max) != Double.doubleToLongBits(other.max))
            {
                return false;
            }
            if (Double.doubleToLongBits(this.min) != Double.doubleToLongBits(other.min))
            {
                return false;
            }
            return true;
        }

    }

    @Test
    @Ignore
    public void testGetFoods() throws Exception
    {
        File file = new File("cache/foods.json");
        FoodsAndNutrients foods = USDAFoodDBUtils.getAccessorInstance()
                                                 .withApiKey(API_KEY)
                                                 .readFromFileCacheOrLoadFromRestAPIIfNotPresent(file)
                                                 .get();

        //System.out.println(JSONHelper.prettyPrint(foods));

        Set<String> nutrientTerms = Arrays.asList("tryptophan", "tyrosine", "histidine", "phenylalanine", "Sugars, total", "Alcohol, ethyl",
                                                  "Glucose (dextrose)", "Fructose", "")
                                          .stream()
                                          .map(term -> term.toLowerCase())
                                          .collect(Collectors.toSet());

        List<Nutrient> nutrients = foods.getNutrients()
                                        .stream()
                                        .filter(iNutrient -> nutrientTerms.contains(iNutrient.getName()
                                                                                             .toLowerCase()))
                                        .collect(Collectors.toList());

        //System.out.println(JSONHelper.prettyPrint(nutrients));

        final int numberOfBuckets = Intensity.values().length;
        Map<String, Set<Integer>> foodIdToBucketIdsMap = new HashMap<>();

        //
        Set<String> nutrientIds = nutrients.stream()
                                           .map(nutrient -> nutrient.getId())
                                           .collect(Collectors.toSet());
        List<Food> foodsWithAllNutrients = foods.getFoods()
                                                .stream()
                                                .filter(food -> food.getNutrientValues()
                                                                    .stream()
                                                                    .map(nutrientValue -> nutrientValue.getNutrientId())
                                                                    .collect(Collectors.toSet())
                                                                    .containsAll(nutrientIds))
                                                .collect(Collectors.toList());

        //
        for (Nutrient nutrient : nutrients)
        {
            List<FoodAndValue> values = foodsWithAllNutrients.stream()
                                                             .map(food -> new FoodAndValue(food, food.getNutrientValues()
                                                                                                     .stream()
                                                                                                     .filter(value -> value.getNutrientId()
                                                                                                                           .equals(nutrient.getId()))
                                                                                                     .findFirst()
                                                                                                     .orElse(null)))
                                                             .filter(foodAndValue -> foodAndValue.getValue() != null)
                                                             .collect(Collectors.toList());

            //
            double average = this.calculateAverage(values);

            List<Bucket> buckets = this.generateBuckets(average, numberOfBuckets);

            Map<Bucket, List<FoodAndValue>> groups = this.groupValues(values, buckets);

            groups.entrySet()
                  .stream()
                  .forEach(groupEntry ->
                  {
                      int groupId = buckets.indexOf(groupEntry.getKey());

                      List<Food> newFoods = groupEntry.getValue()
                                                      .stream()
                                                      .map(foodAndValue -> foodAndValue.getFood())
                                                      .collect(Collectors.toList());

                      newFoods.stream()
                              .forEach(food ->
                              {
                                  String ndbno = food.getNdbno();
                                  foodIdToBucketIdsMap.computeIfAbsent(ndbno, (foodId) -> new HashSet<>())
                                                      .add(groupId);
                              });
                  });

        }

        //
        Set<String> blackList = Arrays.asList("vinegar", "acetate", "acetic", "cider", "balsamic", "alcohol", "sauerkraut", "mayonnaise")
                                      .stream()
                                      .collect(Collectors.toSet());
        Set<String> foodIdBlackList = foods.getFoods()
                                           .stream()
                                           .filter(food -> blackList.stream()
                                                                    .anyMatch(blackTerm -> food.getName()
                                                                                               .toLowerCase()
                                                                                               .contains(blackTerm.toLowerCase())))
                                           .peek(food -> System.out.println("Black listed: " + food.getName()))
                                           .map(food -> food.getNdbno())
                                           .collect(Collectors.toSet());

        foodIdToBucketIdsMap.entrySet()
                            .stream()
                            .filter(entry -> foodIdBlackList.contains(entry.getKey()))
                            .forEach(entry -> entry.getValue()
                                                   .add(Intensity.values().length - 1));

        Map<Integer, Set<Food>> groupToFoodMap = new TreeMap<>();
        Map<String, Food> foodIdToFoodMap = foodsWithAllNutrients.stream()
                                                                 .collect(Collectors.toMap(food -> food.getNdbno(), food -> food));

        foodIdToBucketIdsMap.entrySet()
                            .stream()
                            .forEach(entry ->
                            {
                                String foodId = entry.getKey();
                                int maxGroupId = entry.getValue()
                                                      .stream()
                                                      .mapToInt(groupId -> groupId)
                                                      .max()
                                                      .getAsInt();
                                groupToFoodMap.computeIfAbsent(maxGroupId, (id) -> new TreeSet<>((f1, f2) -> f1.getName()
                                                                                                               .compareTo(f2.getName())))
                                              .add(foodIdToFoodMap.get(foodId));
                            });

        groupToFoodMap.entrySet()
                      .stream()
                      .forEach(groupAndFood ->
                      {
                          int groupId = groupAndFood.getKey();

                          Intensity intensity = Intensity.values()[groupId];

                          PDFBuilderWithPage pdfBuilder = PDFUtils.getPDFInstance()
                                                                  .createEmptyPDF()
                                                                  .addBlankPage();
                          String title = "Levels:" + intensity;
                          pdfBuilder.addTitle(title);
                          pdfBuilder.addSubTitle("Tryptophan,Tyrosine,Phenylalanine,Histidine,Alcohol,Acetic acid and sugar levels");
                          System.out.println(title);

                          Set<Food> foodSet = groupAndFood.getValue();

                          foodSet.stream()
                                 .sorted((f1, f2) -> f1.getName()
                                                       .compareTo(f2.getName()))
                                 .map(food -> "" + food.getName())
                                 .forEach(name ->
                                 {
                                     pdfBuilder.addText(name);
                                     System.out.println(name);
                                 });

                          try
                          {
                              pdfBuilder.build()
                                        .writeTo(new File("C:/Temp/foodlist_" + intensity + ".pdf"));
                          }
                          catch (IOException e)
                          {
                              e.printStackTrace();
                          }
                      });

    }

    private Map<Bucket, List<FoodAndValue>> groupValues(List<FoodAndValue> values, List<Bucket> buckets)
    {
        return values.stream()
                     .collect(Collectors.groupingBy(foodAndValue -> this.findBucket(foodAndValue, buckets)));
    }

    private double calculateAverage(List<FoodAndValue> values)
    {
        return values.stream()
                     .mapToDouble(foodAndValue -> foodAndValue.getValue()
                                                              .getAmount())
                     .average()
                     .getAsDouble();
    }

    private List<Bucket> generateBuckets(double average, final int numberOfBuckets)
    {
        List<Bucket> buckets = new ArrayList<>();
        for (int ii = 0; ii < numberOfBuckets; ii++)
        {
            double min = ii * average / 2;
            double max = (ii + 1) * average / 2;

            if (ii == numberOfBuckets - 1)
            {
                max = Double.MAX_VALUE;
            }

            buckets.add(new Bucket(min, max));
        }
        return buckets;
    }

    private Bucket findBucket(FoodAndValue foodAndValue, List<Bucket> buckets)
    {
        double amount = foodAndValue.getValue()
                                    .getAmount();
        return buckets.stream()
                      .filter(bucket -> amount >= bucket.getMin() && amount < bucket.getMax())
                      .findAny()
                      .get();
    }

    @Test
    @Ignore
    public void testGetAccessorInstance() throws Exception
    {
        FoodIndex foodIndex = USDAFoodDBUtils.getAccessorInstance()
                                             .loadFullDatabaseFromPortal();
        List<FoodIndex.Food> result = foodIndex.stream()
                                               .filter(food -> food.getNutrients()
                                                                   .hasAnyAminoAcid())
                                               //                                               .limit(100)
                                               .collect(Collectors.toList());
        result.forEach(food ->
        {
            System.out.println(food.getName());
            food.getNutrients()
                .stream()
                .filter(nutrient -> nutrient.isAminoAcid())
                .forEach(nutrient ->
                {
                    System.out.println("  " + nutrient.getNutrient()
                                                      .getName()
                            + ": " + nutrient.getAmount()
                                             .asMeasureUnit(MeasureUnit.MILLIGRAM)
                                             .map(mua -> mua.asString())
                                             .orElse(""));
                });
        });

    }

}
