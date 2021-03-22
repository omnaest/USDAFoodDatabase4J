package org.omnaest.usda.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.omnaest.usda.domain.index.AminoAcids;
import org.omnaest.usda.domain.index.FoodIndex;
import org.omnaest.usda.domain.index.FoodIndex.DataTypes;
import org.omnaest.usda.domain.index.FoodIndex.Food;
import org.omnaest.usda.domain.index.FoodIndex.FoodAttributes;
import org.omnaest.usda.domain.index.FoodIndex.FoodContainedNutrient;
import org.omnaest.usda.domain.index.FoodIndex.FoodContainedNutrients;
import org.omnaest.usda.domain.index.FoodIndex.Nutrient;
import org.omnaest.usda.domain.index.FoodIndex.NutrientAmount;
import org.omnaest.usda.domain.index.MeasureUnit;
import org.omnaest.usda.domain.index.MeasureUnitAmount;
import org.omnaest.usda.internal.domain.RawFood;
import org.omnaest.usda.internal.domain.RawFoodAttribute;
import org.omnaest.usda.internal.domain.RawFoodNutrient;
import org.omnaest.usda.internal.domain.RawNutrient;
import org.omnaest.utils.MapperUtils;
import org.omnaest.utils.PredicateUtils;

public class FoodImpl implements Food
{
    public static class FoodContainedNutrientsImpl implements FoodContainedNutrients
    {
        private final List<RawFoodNutrient> nutrients;
        private Map<String, RawNutrient>    globalNutrientIdToNutrient;

        public FoodContainedNutrientsImpl(List<RawFoodNutrient> nutrients, Map<String, RawNutrient> globalNutrientIdToNutrient)
        {
            this.nutrients = nutrients;
            this.globalNutrientIdToNutrient = globalNutrientIdToNutrient;
        }

        @Override
        public Stream<FoodContainedNutrient> stream()
        {
            return this.nutrients.stream()
                                 .map(foodToNutrient -> new FoodContainedNutrient()
                                 {
                                     private RawNutrient nutrient = FoodContainedNutrientsImpl.this.globalNutrientIdToNutrient.get(foodToNutrient.getNutrientId());

                                     @Override
                                     public FoodIndex.Nutrient getNutrient()
                                     {
                                         return new Nutrient()
                                         {
                                             @Override
                                             public String getName()
                                             {
                                                 return nutrient.getName();
                                             }

                                             @Override
                                             public String getUnit()
                                             {
                                                 return nutrient.getUnitName();
                                             }
                                         };
                                     }

                                     @Override
                                     public NutrientAmount getAmount()
                                     {
                                         Optional<MeasureUnitAmount> measureUnitAmount = MeasureUnit.of(this.nutrient.getUnitName())
                                                                                                    .flatMap(mu -> Optional.ofNullable(foodToNutrient.getAmount())
                                                                                                                           .map(NumberUtils::toDouble)
                                                                                                                           .map(amount -> MeasureUnitAmount.of(amount,
                                                                                                                                                               mu)));
                                         return new NutrientAmount()
                                         {
                                             @Override
                                             public String get()
                                             {
                                                 return foodToNutrient.getAmount() + " " + getNutrient().getUnit();
                                             }

                                             @Override
                                             public Optional<MeasureUnitAmount> asMeasureUnit(MeasureUnit measureUnit)
                                             {
                                                 return measureUnitAmount.flatMap(mua -> mua.asMeasureUnitAmountOf(measureUnit));
                                             }
                                         };
                                     }

                                     @Override
                                     public boolean isAminoAcid()
                                     {
                                         return this.isAnyAminoAcid(AminoAcids.values());
                                     }

                                     @Override
                                     public boolean isAnyAminoAcid(AminoAcids... aminoAcids)
                                     {
                                         return Arrays.asList(aminoAcids)
                                                      .stream()
                                                      .map(AminoAcids::asNutrientFilter)
                                                      .anyMatch(filter -> filter.test(this.nutrient));
                                     }

                                 });
        }

        @Override
        public boolean hasNutrientInformation()
        {
            return !this.nutrients.isEmpty();
        }

        @Override
        public boolean hasAnyAminoAcid()
        {
            return this.hasAnyAminoAcidOf(AminoAcids.values());
        }

        @Override
        public boolean hasAnyAminoAcidOf(AminoAcids... aminoAcids)
        {
            return this.nutrients.stream()
                                 .anyMatch(nutrient -> Arrays.asList(aminoAcids)
                                                             .stream()
                                                             .map(AminoAcids::asNutrientIdFilter)
                                                             .anyMatch(filter -> filter.test(Optional.ofNullable(nutrient)
                                                                                                     .map(RawFoodNutrient::getNutrientId)
                                                                                                     .orElse(""))));
        }
    }

    private final RawFood                      rawFood;
    private final Map<String, RawNutrient>     globalNutrientIdToNutrient;
    private final Map<String, RawFoodNutrient> nutrientIdToNutrient;
    private final List<RawFoodNutrient>        rawFoodNutrients;
    private final List<RawFoodAttribute>       rawFoodAttributes;

    public FoodImpl(RawFood rawFood, Map<String, RawNutrient> nutrientIdToNutrient, List<RawFoodNutrient> rawFoodNutrients,
                    List<RawFoodAttribute> rawFoodAttributes)
    {
        this.rawFood = rawFood;
        this.globalNutrientIdToNutrient = nutrientIdToNutrient;
        this.rawFoodNutrients = rawFoodNutrients;
        this.rawFoodAttributes = rawFoodAttributes;
        this.nutrientIdToNutrient = rawFoodNutrients.stream()
                                                    .collect(Collectors.toMap(RawFoodNutrient::getNutrientId, MapperUtils.identity()));
    }

    @Override
    public String getName()
    {
        return this.rawFood.getDescription();
    }

    @Override
    public Stream<String> getNameTokens()
    {
        return Arrays.asList(StringUtils.split(this.getName(), ";,&"))
                     .stream()
                     .filter(token -> token != null)
                     .map(token -> token.trim());
    }

    @Override
    public boolean hasAnyDataTypeOf(DataTypes... dataTypes)
    {
        return Arrays.asList(dataTypes)
                     .stream()
                     .map(DataTypes::name)
                     .anyMatch(type -> StringUtils.equalsIgnoreCase(type, this.rawFood.getDataType()));
    }

    @Override
    public FoodAttributes getAttributes()
    {
        return new FoodAttributes()
        {
            @Override
            public Stream<String> getCommonNames()
            {
                return FoodImpl.this.rawFoodAttributes.stream()
                                                      .filter(attribute -> StringUtils.equals("1000", attribute.getAttributeTypeId()))
                                                      .map(RawFoodAttribute::getValue);
            }
        };
    }

    @Override
    public FoodContainedNutrients getNutrients()
    {
        List<RawFoodNutrient> nutrients = this.rawFoodNutrients;
        return new FoodContainedNutrientsImpl(nutrients, this.globalNutrientIdToNutrient);
    }

    @Override
    public FoodContainedNutrients findNutrientsBy(AminoAcids... aminoAcids)
    {
        List<RawFoodNutrient> matchingNutrients = Optional.ofNullable(aminoAcids)
                                                          .map(Arrays::asList)
                                                          .orElse(Collections.emptyList())
                                                          .stream()
                                                          .map(AminoAcids::getNutrientIds)
                                                          .flatMap(Set::stream)
                                                          .map(String::valueOf)
                                                          .map(this.nutrientIdToNutrient::get)
                                                          .filter(PredicateUtils.notNull())
                                                          .collect(Collectors.toList());
        return new FoodContainedNutrientsImpl(matchingNutrients, this.globalNutrientIdToNutrient);
    }
}