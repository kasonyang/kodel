package site.kason.kodel;

import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import site.kason.kodel.renderer.handlebars.HandlebarsTemplateRenderer;
import site.kason.kodel.renderer.pebble.PebbleTemplateRenderer;
import site.kason.kodel.renderer.tempera.TemperaTemplateRender;

/**
 *
 * @author Kason Yang
 */
public interface ScriptExecutor {

    public Map<String, Object> getGlobal();

    public String getOutputDir();

    public void setOutputDir(String outputDir);

    public List<TemplateTask> getTasks();

    public List<String> getTemplatePaths();
    
    public void executeScript();

    /**
     * Create a new template task
     *
     * @param model the data for rendering
     * @param templateName the name of the template to render
     * @param destination the destination of the rendering result
     * @param engine which template engine to use
     * @return the task created
     */
    default TemplateTask template(Map<String, Object> model, String templateName, String destination, String engine) {
        String destFile = getOutputDir() + "/" + destination;
        if (engine == null || engine.isEmpty()) {
            engine = this.detectEngine(templateName);
        }
        TemplateTask task = new TemplateTask(model, templateName, destFile, engine);
        getTasks().add(task);
        return task;
    }

    /**
     * As same as invoking template(model,tplName,destination,null)
     *
     * @param model
     * @param templateName
     * @param destination
     * @return
     */
    default TemplateTask template(Map<String, Object> model, String templateName, String destination) {
        return template(model, templateName, destination, null);
    }

    /**
     * Add a directory for finding templates
     *
     * @param path the directory for template searching
     */
    default void addTemplatePath(String path) {
        this.getTemplatePaths().add(0, path);
    }

    /**
     * Specify the directory for output
     *
     * @param outDir the new directory
     */
    default void outputDir(String outDir) {
        this.setOutputDir(outDir);
    }

    /**
     * Add a new global variable which could be used in every template
     *
     * @param name the name of the global variable
     * @param value the value of the global variable
     */
    default void global(String name, Object value) {
        this.getGlobal().put(name, value);
    }

    default String detectEngine(String tplName) {
        String tplExt = FilenameUtils.getExtension(tplName);
        switch (tplExt) {
            case "hbs":
                return HandlebarsTemplateRenderer.ENGINE_NAME;
            case "tpr":
                return TemperaTemplateRender.ENGINE_NAME;
            case "pbl":
                return PebbleTemplateRenderer.ENGINE_NAME;
            default:
                throw new IllegalArgumentException("unknown file extension:" + tplExt);
        }
    }

}
