package io.jenkins.plugins.gcr.parsers;

import hudson.FilePath;
import io.jenkins.plugins.gcr.models.Coverage;

public interface CoverageParser {

    Coverage parse(FilePath filepath) throws ParserException;

}
