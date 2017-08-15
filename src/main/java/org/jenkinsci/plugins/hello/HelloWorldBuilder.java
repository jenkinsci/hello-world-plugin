package org.jenkinsci.plugins.hello;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import java.io.IOException;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link HelloWorldBuilder} is created. The created instance is persisted to
 * the project configuration XML by using XStream, so this allows you to use
 * instance fields (like {@link #name}) to remember the configuration.
 *
 * <p>
 * When a build is performed, the
 * {@link #perform(Build, Launcher, BuildListener)} method will be invoked.
 *
 * @author Kohsuke Kawaguchi
 */
public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    private final String name;

    /**
     * This annotation tells Jenkins to call this constructor, with values from
     * the configuration form page with matching parameter names.
     *
     * @param name name to be greeted in the console log
     */
    @DataBoundConstructor
    public HelloWorldBuilder(String name) {
        this.name = name;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     *
     * @return name to include in greeting
     */
    public String getName() {
        return name;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        // this is where you 'build' the project
        // since this is a dummy, we just say 'hello world' and call that a build

        // this also shows how you can consult the global configuration of the builder
        if (getDescriptor().useFrench()) {
            listener.getLogger().println("Bonjour, " + name + "!");
        } else {
            listener.getLogger().println("Hello, " + name + "!");
        }
    }

    /**
     * Jenkins defines a method {@link Builder#getDescriptor()}, which returns
     * the corresponding {@link Descriptor} object.
     *
     * Since we know that it's actually {@link DescriptorImpl}, override the
     * method and give a better return type, so that we can access
     * {@link DescriptorImpl} methods more easily.
     *
     * This is not necessary, but just a coding style preference.
     *
     * @return descriptor for this builder
     */
    @Override
    public DescriptorImpl getDescriptor() {
        // see Descriptor javadoc for more about what a descriptor is.
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link HelloWorldBuilder}. The class is marked as public
     * so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/org/jenkinsci/plugins/hello/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    // this annotation tells Jenkins that this is the implementation of an extension point
    @Extension
    @Symbol("helloWorld")
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * To persist global configuration information, simply store it in a
         * field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private boolean useFrench;

        public DescriptorImpl() {
            load();
        }

        /**
         * This human readable name is used in the configuration screen.
         *
         * @return display name for configuration screen
         */
        @Override
        public String getDisplayName() {
            return "Say hello world";
        }

        /**
         * Applicable to any kind of project.
         *
         * @param type class to be tested for applicability
         * @return true if this builder can be applied to a project of class
         * type
         */
        @Override
        public boolean isApplicable(Class type) {
            return true;
        }

        @Override
        public boolean configure(StaplerRequest staplerRequest, JSONObject json) throws FormException {
            // to persist global configuration information,
            // set that to properties and call save().
            useFrench = json.getBoolean("useFrench");
            save();
            return true; // indicate that everything is good so far
        }

        /**
         * This method returns true if the global configuration says we should
         * speak French.
         *
         * @return true if logged message should be in French
         */
        public boolean useFrench() {
            return useFrench;
        }
    }
}
