package org.omnaest.usda.domain.index;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public enum MeasureUnit
{
    MILLIGRAM("MG"), MICROGRAM(0.001, MILLIGRAM, "UG"), GRAM(1000, MILLIGRAM, "G"), KILOGRAM(1000 * 1000, MILLIGRAM, "KG");

    private MeasureUnitAmount normMeasureUnitAmount;
    private String            unitName;

    private MeasureUnit(double amount, MeasureUnit normMeasureUnit, String unitName)
    {
        this.unitName = unitName;
        this.normMeasureUnitAmount = new MeasureUnitAmount(amount, normMeasureUnit);
    }

    private MeasureUnit(String unitName)
    {
        this.unitName = unitName;
        this.normMeasureUnitAmount = new MeasureUnitAmount(1.0, this);
    }

    public MeasureUnitAmount getNormMeasureUnitAmount()
    {
        return this.normMeasureUnitAmount;
    }

    public static Optional<MeasureUnit> of(String unitName)
    {
        return Arrays.asList(values())
                     .stream()
                     .filter(measureUnit -> StringUtils.equalsIgnoreCase(measureUnit.unitName, unitName))
                     .findFirst();
    }
}
