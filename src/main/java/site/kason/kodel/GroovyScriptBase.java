package site.kason.kodel;

import groovy.lang.Script;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kason Yang
 */
public abstract class GroovyScriptBase extends Script implements ScriptExecutor {

    public final Map<String, Object> global = new HashMap();

    private String outputDir = ".";

    public final List<TemplateTask> tasks = new LinkedList();

    private List<String> templatePaths = new LinkedList();

    @Override
    public Map<String, Object> getGlobal() {
        return global;
    }

    @Override
    public String getOutputDir() {
        return this.outputDir;
    }

    @Override
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    @Override
    public List<TemplateTask> getTasks() {
        return this.tasks;
    }

    @Override
    public List<String> getTemplatePaths() {
        return this.templatePaths;
    }

    @Override
    public void executeScript() {
        this.run();
    }

}
