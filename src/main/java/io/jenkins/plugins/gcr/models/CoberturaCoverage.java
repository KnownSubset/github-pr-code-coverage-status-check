package io.jenkins.plugins.gcr.models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "coverage")
public class CoberturaCoverage extends XmlCoverage {

    @XmlAttribute(name = "line-rate")
    public double lineRate;

    @XmlAttribute(name = "branch-rate")
    public double branchRate;

    @XmlAttribute(name = "complexity")
    public double complexity = -1.0f;

    public CoberturaCoverage() {
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
    public String toString() {
        return String.format("[ lineRate=%f, branchRate=%f ]", lineRate, branchRate);
    }
}
