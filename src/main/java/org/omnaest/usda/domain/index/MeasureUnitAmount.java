package org.omnaest.usda.domain.index;

import java.util.Optional;

import org.omnaest.utils.NumberUtils;

public class MeasureUnitAmount
{
    private double      amount;
    private MeasureUnit unit;

    protected MeasureUnitAmount(double amount, MeasureUnit unit)
    {
        super();
        this.amount = amount;
        this.unit = unit;
    }

    public double getAmount()
    {
        return this.amount;
    }

    public MeasureUnit getUnit()
    {
        return this.unit;
    }

    public Optional<MeasureUnitAmount> asMeasureUnitAmountOf(MeasureUnit measureUnit)
    {
        MeasureUnitAmount normUnitAmountSource = this.getUnit()
                                                     .getNormMeasureUnitAmount();
        MeasureUnitAmount normUnitAmountTarget = measureUnit.getNormMeasureUnitAmount();

        if (!normUnitAmountSource.getUnit()
                                 .equals(normUnitAmountTarget.getUnit()))
        {
            return Optional.empty();
        }

        double amountSource = normUnitAmountSource.getAmount();
        double amountTarget = normUnitAmountTarget.getAmount();

        double ratio = amountSource / amountTarget;

        return Optional.of(new MeasureUnitAmount(this.amount * ratio, measureUnit));
    }

    @Override
    public String toString()
    {
        return "UnitAmount [amount=" + this.amount + ", unit=" + this.unit + "]";
    }

    public static MeasureUnitAmount of(double amount, MeasureUnit measureUnit)
    {
        return new MeasureUnitAmount(amount, measureUnit);

    }

    public String asString()
    {
        return NumberUtils.formatter()
                          .withMaximumFractionDigits(3)
                          .format(this.amount)
                + " " + this.unit.name()
                                 .toLowerCase();
    }

}
