package org.omnaest.usda.domain.index;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.NumberUtils;
import org.omnaest.usda.internal.domain.RawNutrient;

public enum AminoAcids
{
    CHOLINE(1194, 1195, 1196, 1197, 1199),
    BETAINE(1198),
    TRYPTOPHAN(1210),
    THREONINE(1211),
    ISOLEUCINE(1212),
    LEUCINE(1213),
    LYSINE(1214),
    METHIONINE(1215),
    CYSTINE(1216),
    PHENYLALANINE(1217),
    TYROSINE(1218),
    VALINE(1219),
    ARGININE(1220),
    HISTIDINE(1221),
    ALANINE(1222),
    ASPARTATE(1223),
    GLUTAMATE(1224),
    GLYCINE(1225),
    PROLINE(1226),
    SERINE(1227),
    HYDROXYPROLINE(1228),
    CYSTEINE(1232),
    GLUTAMINE(1233),
    TAURINE(1234);

    private Set<Integer> nutrientIds;

    private AminoAcids(Integer... nutrientIds)
    {
        this.nutrientIds = Arrays.asList(nutrientIds)
                                 .stream()
                                 .collect(Collectors.toSet());
    }

    public Predicate<RawNutrient> asNutrientFilter()
    {
        Predicate<String> nutrientIdFilter = this.asNutrientIdFilter();
        return nutrient -> nutrientIdFilter.test((Optional.ofNullable(nutrient)
                                                          .map(RawNutrient::getNutrientId)
                                                          .orElse("")));
    }

    public Predicate<String> asNutrientIdFilter()
    {
        return nutrientId -> this.nutrientIds.contains(Optional.ofNullable(nutrientId)
                                                               .map(NumberUtils::toInt)
                                                               .orElse(0));
    }

    public static Stream<AminoAcids> stream()
    {
        return Arrays.asList(values())
                     .stream();
    }

    public Set<Integer> getNutrientIds()
    {
        return this.nutrientIds;
    }

}
