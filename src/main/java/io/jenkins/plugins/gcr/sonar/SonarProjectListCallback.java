package io.jenkins.plugins.gcr.sonar;

import io.jenkins.plugins.gcr.sonar.models.SonarProject;

import java.util.List;

public interface SonarProjectListCallback {

    void perform(List<SonarProject> projects);

}
