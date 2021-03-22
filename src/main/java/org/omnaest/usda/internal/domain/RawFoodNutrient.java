package org.omnaest.usda.internal.domain;

import org.omnaest.utils.csv.CSVUtils.Field;

public class RawFoodNutrient
{
    @Field("id")
    private String id;

    @Field("fdc_id")
    private String foodId;

    @Field("nutrient_id")
    private String nutrientId;

    @Field("amount")
    private String amount;

    @Field("data_points")
    private String dataPoints;

    @Field("derivation_id")
    private String derivationId;

    @Field("min")
    private String min;

    @Field("max")
    private String max;

    @Field("median")
    private String median;

    @Field("footnote")
    private String footnote;

    @Field("min_year_acquired")
    private String minYearAcquired;

    public String getId()
    {
        return this.id;
    }

    public String getFoodId()
    {
        return this.foodId;
    }

    public String getNutrientId()
    {
        return this.nutrientId;
    }

    public String getAmount()
    {
        return this.amount;
    }

    public String getDataPoints()
    {
        return this.dataPoints;
    }

    public String getDerivationId()
    {
        return this.derivationId;
    }

    public String getMin()
    {
        return this.min;
    }

    public String getMax()
    {
        return this.max;
    }

    public String getMedian()
    {
        return this.median;
    }

    public String getFootnote()
    {
        return this.footnote;
    }

    public String getMinYearAcquired()
    {
        return this.minYearAcquired;
    }

    @Override
    public String toString()
    {
        return "RawFoodNutrient [id=" + this.id + ", foodId=" + this.foodId + ", nutrientId=" + this.nutrientId + ", amount=" + this.amount + ", dataPoints="
                + this.dataPoints + ", derivationId=" + this.derivationId + ", min=" + this.min + ", max=" + this.max + ", median=" + this.median
                + ", footnote=" + this.footnote + ", minYearAcquired=" + this.minYearAcquired + "]";
    }

}