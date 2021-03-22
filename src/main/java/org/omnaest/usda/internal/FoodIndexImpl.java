package org.omnaest.usda.internal;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.omnaest.usda.domain.index.FoodIndex;
import org.omnaest.usda.internal.domain.RawFood;
import org.omnaest.usda.internal.domain.RawFoodAttribute;
import org.omnaest.usda.internal.domain.RawFoodNutrient;
import org.omnaest.usda.internal.domain.RawNutrient;

public class FoodIndexImpl implements FoodIndex
{
    private final Map<String, RawFood>                foodIdToFood;
    private final Map<String, List<RawFoodAttribute>> foodIdToFoodAttributes;
    private final Map<String, RawNutrient>            nutrientIdToNutrient;
    private final Map<String, List<RawFoodNutrient>>  foodIdToFoodNutrients;

    public FoodIndexImpl(Map<String, RawFood> foodIdToFood, Map<String, List<RawFoodAttribute>> foodIdToFoodAttributes,
                         Map<String, RawNutrient> nutrientIdToNutrient, Map<String, List<RawFoodNutrient>> foodIdToFoodNutrients)
    {
        this.foodIdToFood = foodIdToFood;
        this.foodIdToFoodAttributes = foodIdToFoodAttributes;
        this.nutrientIdToNutrient = nutrientIdToNutrient;
        this.foodIdToFoodNutrients = foodIdToFoodNutrients;
    }

    @Override
    public Stream<FoodIndex.Food> stream()
    {
        return this.foodIdToFood.keySet()
                                .stream()
                                .map(foodId ->
                                {
                                    RawFood rawFood = this.foodIdToFood.get(foodId);
                                    List<RawFoodAttribute> rawFoodAttributes = this.foodIdToFoodAttributes.getOrDefault(foodId, Collections.emptyList());
                                    List<RawFoodNutrient> rawFoodNutrients = this.foodIdToFoodNutrients.getOrDefault(foodId, Collections.emptyList());
                                    Map<String, RawNutrient> nutrientIdToNutrient = this.nutrientIdToNutrient;
                                    return new FoodImpl(rawFood, nutrientIdToNutrient, rawFoodNutrients, rawFoodAttributes);
                                });
    }
}