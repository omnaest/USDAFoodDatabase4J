package org.omnaest.usda.internal.domain;

import org.omnaest.utils.csv.CSVUtils.Field;

public class RawFood
{

    @Field("fdc_id")
    private String foodId;

    @Field("data_type")
    private String dataType;

    @Field("description")
    private String description;

    @Field("food_category_id")
    private String foodCategoryId;

    @Field("publication_date")
    private String publicationDate;

    public String getFoodId()
    {
        return this.foodId;
    }

    public String getDataType()
    {
        return this.dataType;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getFoodCategoryId()
    {
        return this.foodCategoryId;
    }

    public String getPublicationDate()
    {
        return this.publicationDate;
    }

    @Override
    public String toString()
    {
        return "RawFood [foodId=" + this.foodId + ", dataType=" + this.dataType + ", description=" + this.description + ", foodCategoryId="
                + this.foodCategoryId + ", publicationDate=" + this.publicationDate + "]";
    }
}