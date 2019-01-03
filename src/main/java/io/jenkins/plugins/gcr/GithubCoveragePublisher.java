package io.jenkins.plugins.gcr;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.gcr.build.BuildStepService;
import io.jenkins.plugins.gcr.github.GithubClient;
import io.jenkins.plugins.gcr.github.GithubPayload;
import io.jenkins.plugins.gcr.models.ComparisonOption;
import io.jenkins.plugins.gcr.models.CoverageRateType;
import io.jenkins.plugins.gcr.models.CoverageType;
import io.jenkins.plugins.gcr.models.PluginEnvironment;
import io.jenkins.plugins.gcr.sonar.SonarClient;
import io.jenkins.plugins.gcr.sonar.models.SonarProject;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.util.List;

public class GithubCoveragePublisher extends Recorder implements SimpleBuildStep {

    public static final int COMPARISON_SONAR = 0;
    public static final int COMPARISON_FIXED = 1;
    private final String filepath;
    private String coverageXmlType;
    private String coverageRateType;
    private ComparisonOption comparisonOption;

    @DataBoundConstructor
    public GithubCoveragePublisher(String filepath, String coverageXmlType, String coverageRateType, ComparisonOption comparisonOption) {
        this.filepath = filepath;
        this.coverageXmlType = coverageXmlType;
        this.coverageRateType = coverageRateType;
        this.comparisonOption = comparisonOption;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public String getFilepath() {
        return filepath;
    }

    public String getCoverageXmlType() {
        return coverageXmlType;
    }

    @DataBoundSetter
    public void setCoverageXmlType(String coverageXmlType) {
        this.coverageXmlType = coverageXmlType;
    }

    public ComparisonOption getComparisonOption() {
        return comparisonOption;
    }

    @DataBoundSetter
    public void setComparisonOption(ComparisonOption comparisonOption) {
        this.comparisonOption = comparisonOption;
    }

    public String getSonarProject() {
        return this.comparisonOption.getSonarProject();
    }

    public String getCoverageRateType() {
        return coverageRateType;
    }

    @DataBoundSetter
    public void setCoverageRateType(String coverageRateType) {
        this.coverageRateType = coverageRateType;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Attempting to parse file of type, " + coverageXmlType + "");

        PluginEnvironment environment = new PluginEnvironment(run.getEnvironment(listener));
        String githubAccessToken = PluginConfiguration.DESCRIPTOR.getGithubAccessToken();
        String githubUrl = PluginConfiguration.DESCRIPTOR.getGithubEnterpriseUrl();
        GithubClient githubClient = new GithubClient(environment, githubUrl, githubAccessToken);

        FilePath pathToFile = workspace.child(this.filepath);

        if (!pathToFile.exists()) {
            listener.error("The coverage file at the provided path does not exist");
            run.setResult(Result.FAILURE);
            return;
        } else {
            listener.getLogger().println(String.format("Found file '%s'", this.filepath));
        }

        BuildStepService buildStepService = new BuildStepService();

        try {
            CoverageReportAction coverageReport = buildStepService.generateCoverageReport(pathToFile, comparisonOption, coverageXmlType, coverageRateType);
            run.addAction(coverageReport);
            run.save();

            GithubPayload payload = buildStepService.generateGithubCovergePayload(coverageReport, environment.getBuildUrl());
            githubClient.sendCommitStatus(payload);

            run.setResult(Result.SUCCESS);
        } catch (Exception ex) {
            listener.error(ex.getMessage());
            ex.printStackTrace();
            run.setResult(Result.FAILURE);
        }

    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        private ListBoxModel sonarProjectModel;

        public ListBoxModel doFillCoverageXmlTypeItems() {
            // TODO: localise
            ListBoxModel model = new ListBoxModel();
            model.add("Cobertura XML", CoverageType.COBERTURA.getIdentifier());
            model.add("Jacoco XML", CoverageType.JACOCO.getIdentifier());
            return model;
        }

        public ListBoxModel doFillSonarProjectItems() {
            sonarProjectModel = new ListBoxModel();

            SonarClient client = new SonarClient();
            try {
                List<SonarProject> projects = client.listProjects();
                projects.forEach(project -> sonarProjectModel.add(project.getName(), project.getKey()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return sonarProjectModel;
        }

        public ListBoxModel doFillCoverageRateTypeItems() {
            ListBoxModel model = new ListBoxModel();
            model.add("Overall", CoverageRateType.OVERALL.getName());
            model.add("Branch", CoverageRateType.BRANCH.getName());
            model.add("Line", CoverageRateType.LINE.getName());
            model.add("Complexity", CoverageRateType.COMPLEXITY.getName());
            return model;
        }

        public FormValidation doCheckSonarProject(@QueryParameter String value) {
            if (sonarProjectModel == null || sonarProjectModel.isEmpty()) {
                return FormValidation.error("SonarQube server unreachable.");
            }
            if (value == null || value.equals("")) {
                return FormValidation.error("Invalid project selection. check that your SonarQube server is not unreachable.");
            }

            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.GithubCoveragePublisher_DescriptorImpl_DisplayName();
        }

    }
}
