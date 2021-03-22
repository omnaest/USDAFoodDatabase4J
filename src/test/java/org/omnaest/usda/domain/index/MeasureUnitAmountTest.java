package org.omnaest.usda.domain.index;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MeasureUnitAmountTest
{

    @Test
    public void testAsMeasureUnitAmountOf() throws Exception
    {
        MeasureUnitAmount newMeasureUnitAmount = MeasureUnitAmount.of(1.0, MeasureUnit.GRAM)
                                                                  .asMeasureUnitAmountOf(MeasureUnit.MICROGRAM)
                                                                  .get();
        assertEquals(MeasureUnit.MICROGRAM, newMeasureUnitAmount.getUnit());
        assertEquals(1000 * 1000.0, newMeasureUnitAmount.getAmount(), 0.001);
    }

}
