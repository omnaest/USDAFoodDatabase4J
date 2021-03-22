package org.omnaest.usda.internal.domain;

import org.omnaest.utils.csv.CSVUtils.Field;

public class RawFoodAttributeType
{
    @Field("id")
    private String attributeTypeId;

    @Field("name")
    private String name;

    @Field("description")
    private String description;

    public String getAttributeTypeId()
    {
        return this.attributeTypeId;
    }

    public String getName()
    {
        return this.name;
    }

    public String getDescription()
    {
        return this.description;
    }

    @Override
    public String toString()
    {
        return "RawFoodAttribute [attributeTypeId=" + this.attributeTypeId + ", name=" + this.name + ", description=" + this.description + "]";
    }

}