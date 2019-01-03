package io.jenkins.plugins.gcr.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.stream.Stream;

@XmlRootElement(name = "report")
public class JacocoCoverage extends XmlCoverage {

    public static final String TYPE_INSTRUCTION = "INSTRUCTION";
    public static final String TYPE_BRANCH = "BRANCH";
    public static final String TYPE_LINE = "LINE";
    public static final String TYPE_COMPLEXITY = "COMPLEXITY";

    @XmlElement(name = "counter")
    public List<JacocoCounter> counters;

    public JacocoCoverage() {
    }

    public double getComplexity() {
        JacocoCounter counter = filterStreamFor(TYPE_COMPLEXITY).findAny().get();
        return rateForCounter(counter);
    }

    public double getLineRate() {
        JacocoCounter counter = filterStreamFor(TYPE_LINE).findAny().get();
        return rateForCounter(counter);
    }

    public double getBranchRate() {
        JacocoCounter counter = filterStreamFor(TYPE_BRANCH).findAny().get();
        return rateForCounter(counter);
    }

    private Stream<JacocoCounter> filterStreamFor(String type) {
        return counters.stream().filter(obj -> obj.type.equals(type));
    }

    private double rateForCounter(JacocoCounter counter) {
        return ((double) counter.covered / (double) counter.getTotal());
    }

    @Override
    public String toString() {
        return String.format("[ lineRate=%f, branchRate=%f ]", getLineRate(), getBranchRate());
    }
}
