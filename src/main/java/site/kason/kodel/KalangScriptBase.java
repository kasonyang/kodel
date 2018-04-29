package site.kason.kodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import kalang.lang.Script;

/**
 *
 * @author Kason Yang
 */
public abstract class KalangScriptBase extends Script implements ScriptExecutor {

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
        try {
            this.execute();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

}
