package io.jenkins.plugins.gcr.models;

import java.util.Arrays;
import java.util.stream.Stream;

public enum CoverageRateType {
    LINE("Line"),
    BRANCH("Branch"),
    OVERALL("Overall"),
    COMPLEXITY("Complexity");

    private String name;

    CoverageRateType(String name) {
        this.name = name;
    }

    public static CoverageRateType fromName(String name) {
        Stream<CoverageRateType> stream = Arrays.stream(CoverageRateType.values());
        return stream.filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    public String getName() {
        return name;
    }
}
