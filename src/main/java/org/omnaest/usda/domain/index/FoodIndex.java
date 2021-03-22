package org.omnaest.usda.domain.index;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.omnaest.utils.stream.Streamable;

public interface FoodIndex
{
    public Stream<Food> stream();

    public static interface Food
    {
        public String getName();

        /**
         * Returns the {@link #getName()} but separated into tokens
         * 
         * @return
         */
        public Stream<String> getNameTokens();

        public FoodAttributes getAttributes();

        public FoodContainedNutrients getNutrients();

        public FoodContainedNutrients findNutrientsBy(AminoAcids... aminoAcids);

        public boolean hasAnyDataTypeOf(DataTypes... dataTypes);
    }

    public static enum DataTypes
    {
        SAMPLE_FOOD, SR_LEGACY_FOOD, SUB_SAMPLE_FOOD, EXPERIMENTAL_FOOD, INPUT_FOOD, FOUNDATION_FOOD, BRANDED_FOOD, SURVEY_FNDDS_FOOD, MARKET_ACQUISTION
    }

    public static interface FoodAttributes
    {
        public Stream<String> getCommonNames();
    }

    public static interface FoodContainedNutrients extends Streamable<FoodContainedNutrient>
    {
        public boolean hasNutrientInformation();

        public boolean hasAnyAminoAcid();

        public boolean hasAnyAminoAcidOf(AminoAcids... aminoAcids);
    }

    public static interface FoodContainedNutrient
    {
        public Nutrient getNutrient();

        public NutrientAmount getAmount();

        public boolean isAminoAcid();

        public boolean isAnyAminoAcid(AminoAcids... aminoAcids);
    }

    public static interface NutrientAmount extends Supplier<String>
    {

        public Optional<MeasureUnitAmount> asMeasureUnit(MeasureUnit measureUnit);

    }

    public static interface Nutrient
    {
        public String getName();

        public String getUnit();
    }
}
