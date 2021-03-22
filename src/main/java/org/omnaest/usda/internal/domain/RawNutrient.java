package org.omnaest.usda.internal.domain;

import org.omnaest.utils.csv.CSVUtils.Field;

public class RawNutrient
{
    @Field("id")
    private String nutrientId;

    @Field("name")
    private String name;

    @Field("unit_name")
    private String unitName;

    @Field("nutrient_nbr")
    private String nbr;

    @Field("rank")
    private String rank;

    public String getNutrientId()
    {
        return this.nutrientId;
    }

    public String getName()
    {
        return this.name;
    }

    public String getUnitName()
    {
        return this.unitName;
    }

    public String getNbr()
    {
        return this.nbr;
    }

    public String getRank()
    {
        return this.rank;
    }

    @Override
    public String toString()
    {
        return "RawNutrient [nutrientId=" + this.nutrientId + ", name=" + this.name + ", unitName=" + this.unitName + ", nbr=" + this.nbr + ", rank="
                + this.rank + "]";
    }

}