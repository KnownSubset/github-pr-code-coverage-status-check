package io.jenkins.plugins.gcr.models;

public class DefaultCoverage implements Coverage {

    private double lineRate;
    private double branchRate;
    private double complexity;
    private double overallRate;

    public DefaultCoverage(double overallRate, double lineRate, double branchRate, double complexity) {
        this.lineRate = lineRate;
        this.branchRate = branchRate;
        this.overallRate = overallRate;
        this.complexity = complexity;
    }

    @Override
    public double getLineRate() {
        return lineRate;
    }

    @Override
    public double getBranchRate() {
        return branchRate;
    }

    @Override
    public double getComplexity() {
        return complexity;
    }

    @Override
    public double getOverallRate() {
        return overallRate;
    }
}
