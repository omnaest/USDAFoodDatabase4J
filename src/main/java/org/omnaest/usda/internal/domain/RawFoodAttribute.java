package org.omnaest.usda.internal.domain;

import org.omnaest.utils.csv.CSVUtils.Field;

public class RawFoodAttribute
{
    @Field("id")
    private String attributeId;

    @Field("fdc_id")
    private String foodId;

    @Field("seq_num")
    private String sequenceNumber;

    @Field("food_attribute_type_id")
    private String attributeTypeId;

    @Field("name")
    private String name;

    @Field("value")
    private String value;

    public String getAttributeId()
    {
        return this.attributeId;
    }

    public String getFoodId()
    {
        return this.foodId;
    }

    public String getSequenceNumber()
    {
        return this.sequenceNumber;
    }

    public String getAttributeTypeId()
    {
        return this.attributeTypeId;
    }

    public String getName()
    {
        return this.name;
    }

    public String getValue()
    {
        return this.value;
    }

    @Override
    public String toString()
    {
        return "RawFoodAttribute [attributeId=" + this.attributeId + ", foodId=" + this.foodId + ", sequenceNumber=" + this.sequenceNumber
                + ", attributeTypeId=" + this.attributeTypeId + ", name=" + this.name + ", value=" + this.value + "]";
    }

}