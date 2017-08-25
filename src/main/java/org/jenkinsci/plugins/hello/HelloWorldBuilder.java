package org.jenkinsci.plugins.hello;

import java.io.IOException;
import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

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
 * {@link #perform(Run, FilePath, Launcher, TaskListener)} method will
 * be invoked.
 *
 * @author Kohsuke Kawaguchi
 */
public class HelloWorldBuilder extends Builder implements SimpleBuildStep {

    /** Name to be displayed by the 'Say hello world' build step. */
    private final String name;

    /**
     * This annotation tells Jenkins to call this constructor, with values from
     * the configuration form page with matching parameter names.
     *
     * @param name name to be greeted in the console log
     */
    @DataBoundConstructor
    public HelloWorldBuilder(final String name) {
        this.name = name;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     *
     * @return name to include in greeting
     */
    public final String getName() {
        return name;
    }

    @Override
    public final void perform(final Run<?, ?> run,
                              final FilePath workspace,
                              final Launcher launcher,
                              final TaskListener listener)
        throws InterruptedException, IOException {
        // this is where you 'build' the project since this is a
        // dummy, we just say 'hello world' and call that a build

        // this also shows how you can consult the global
        // configuration of the builder
        if (getDescriptor().useFrench()) {
            listener.getLogger().println("Bonjour, " + name + "!");
        } else {
            listener.getLogger().println("Hello, " + name + "!");
        }
    }

    /**
     * Jenkins defines a method {@link Builder#getDescriptor()}, which returns
     * the corresponding {@link hudson.model.Descriptor} object.
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
    public final DescriptorImpl getDescriptor() {
        // see Descriptor javadoc for more about what a descriptor is.
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link HelloWorldBuilder}. The class is marked as public
     * so that it can be accessed from views.
     *
     * <p>
     * See
     * src/main/resources/org/jenkinsci/plugins/hello/HelloWorldBuilder/
     * for the actual HTML fragments for the configuration screen.
     */
    // @Extension annotation identifies this uses an extension point
    // @Symbol annotation registers a symbol with pipeline
    @Extension
    @Symbol("helloWorld")
    public static final class DescriptorImpl
        extends BuildStepDescriptor<Builder> {

        /**
         * To persist global configuration information, simply store it in a
         * field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private boolean useFrench;

        /** Warn if user provided name is shorter than NAME_LENGTH_WARNING.
         */
        private static final int NAME_LENGTH_WARNING = 4;

        /**
         * Constructor for this descriptor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      outcome of the validation. This is sent to the browser.
         *      <p>
         *      Returning {@link FormValidation#error(String)} does
         *      not prevent the form from being saved. It just means
         *      that a message will be displayed to the user.
         * @throws java.io.IOException on input / output error
         * @throws javax.servlet.ServletException on servlet exception
         */
        public FormValidation doCheckName(@QueryParameter final String value)
                throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set a name");
            }
            if (value.length() < NAME_LENGTH_WARNING) {
                return FormValidation.warning("Isn't the name too short?");
            }
            return FormValidation.ok();
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
        public boolean isApplicable(final Class type) {
            return true;
        }

        @Override
        public boolean configure(final StaplerRequest staplerRequest,
                                 final JSONObject json)
            throws FormException {
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

        /**
         * Global configuration to force output in French.
         *  Intentionally package protected for testing.
         * @param value true if output should be French
         */
        void setUseFrench(final boolean value) {
            useFrench = value;
        }
    }
}
