package hudson.plugins.hello_world;

import hudson.Launcher;
import hudson.Extension;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.management.Descriptor;

import net.sf.json.JSONObject;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link HelloWorldBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(Build, Launcher, BuildListener)} method
 * will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class HelloWorldBuilder extends Builder {

    private final String name;

    /**
     * This annotation tells Hudson to call this constructor, with
     * values from the configuration form page with matching parameter names.
     */
    @DataBoundConstructor
    public HelloWorldBuilder(String name) {
        this.name = name;
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        // this is where you 'build' the project
        // since this is a dummy, we just say 'hello world' and call that a build

        // this also shows how you can consult the global configuration of the builder
        if(getDescriptor().useFrench())
            listener.getLogger().println("Bonjour, "+name+"!");
        else
            listener.getLogger().println("Hello, "+name+"!");
        return true;
    }

    /**
     * Hudson defines a method {@link Builder#getDescriptor()}, which
     * returns the corresponding {@link Descriptor} object.
     *
     * Since we know that it's actually {@link DescriptorImpl}, override
     * the method and give a better return type, so that we can access
     * {@link DescriptorImpl} methods more easily.
     *
     * This is not necessary, but just a coding style preference.
     */
    @Override
    public DescriptorImpl getDescriptor() {
        // see Descriptor javadoc for more about what a descriptor is.
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link HelloWorldBuilder}.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>views/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    // this annotation tells Hudson that this is the implementation of an extension point
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
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
         */
        @Override
        public String getDisplayName() {
            return "Say hello world";
        }

        /**
         * Applicable to any kind of project.
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
         * This method returns true if the global configuration says we should speak French.
         */
        public boolean useFrench() {
            return useFrench;
        }
    }
}
