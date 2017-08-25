/*
 * The MIT License
 *
 * Copyright 2017 Mark Waite.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.hello;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import static org.hamcrest.CoreMatchers.*;
import org.hamcrest.core.IsInstanceOf;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.WithoutJenkins;

/**
 * Test the HelloWorldBuilder.
 *
 * @author Mark Waite
 */
public class HelloWorldBuilderTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    private HelloWorldBuilder builder = null;
    private String name = null;

    public HelloWorldBuilderTest() {
    }

    @Before
    public void setUp() {
        name = "New name";
        builder = new HelloWorldBuilder(name);
    }

    @Test
    @WithoutJenkins // This test does not need the JenkinsRule instance
    public void testGetName() {
        assertThat(builder.getName(), is(name));
    }

    @Test
    public void testPerform() {
        // Tested by testFreeStyleProject
        // Tested by testScriptedPipeline
    }

    @Test
    public void testFreeStyleProject() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(builder);
        FreeStyleBuild completedBuild = jenkins.assertBuildStatusSuccess(project.scheduleBuild2(0));
        String helloString = "Hello, " + name + "!";
        jenkins.assertLogContains(helloString, completedBuild);
    }

    @Test
    public void testScriptedPipeline() throws Exception {
        String agentLabel = "my-agent";
        jenkins.createOnlineSlave(Label.get(agentLabel));
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "test-perform-pipeline");
        String pipelineScript
                = "node {\n"
                + "  step([$class: 'HelloWorldBuilder', name: '" + name + "'])"
                + "}";
        job.setDefinition(new CpsFlowDefinition(pipelineScript, true));
        WorkflowRun completedBuild = jenkins.assertBuildStatusSuccess(job.scheduleBuild2(0));
        String expectedString = "Hello, " + name + "!";
        jenkins.assertLogContains(expectedString, completedBuild);
    }

    @Test
    public void testScriptedPipelineUseSymbol() throws Exception {
        String agentLabel = "my-agent";
        jenkins.createOnlineSlave(Label.get(agentLabel));
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "test-scripted-pipeline-use-symbol");
        String pipelineScript
                = "node {\n"
                + "  helloWorld '" + name + "'\n"
                + "}";
        job.setDefinition(new CpsFlowDefinition(pipelineScript, true));
        WorkflowRun completedBuild = jenkins.assertBuildStatusSuccess(job.scheduleBuild2(0));
        String expectedString = "Hello, " + name + "!";
        jenkins.assertLogContains(expectedString, completedBuild);
    }

    @Test
    public void testGetDescriptor() {
        assertThat(builder.getDescriptor(), IsInstanceOf.instanceOf(BuildStepDescriptor.class));
        assertThat(builder.getDescriptor().getDisplayName(), is("Say hello world"));
    }

    @Test
    public void testDescriptorIsApplicable() {
        BuildStepDescriptor<Builder> descriptor = builder.getDescriptor();
        assertThat(descriptor.isApplicable(FreeStyleProject.class), is(true));
    }

    /* Confirm job configuration is unharmed by a round trip through UI */
    @Test
    public void testJobConfigRoundTrip() throws Exception {
        HelloWorldBuilder after = jenkins.configRoundtrip(builder);
        jenkins.assertEqualDataBoundBeans(builder, after);
    }

    /* Confirm system configuration is unharmed by a round trip through UI */
    @Test
    public void testSystemConfigRoundTrip() throws Exception {
        jenkins.configRoundtrip();
    }

    /* Confirm default system configuration does not use French */
    @Test
    public void testSystemConfigDefault() throws Exception {
        assertThat(builder.getDescriptor().useFrench(), is(false));
    }

    /* Confirm modified system configuration is unharmed by a round trip through UI */
    @Test
    public void testSystemConfigFrenchRoundTrip() throws Exception {
        builder.getDescriptor().setUseFrench(true);
        jenkins.configRoundtrip();
        assertThat(builder.getDescriptor().useFrench(), is(true));
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(builder);
        FreeStyleBuild completedBuild = jenkins.assertBuildStatusSuccess(project.scheduleBuild2(0));
        String helloString = "Bonjour, " + name + "!";
        jenkins.assertLogContains(helloString, completedBuild);
    }
}
