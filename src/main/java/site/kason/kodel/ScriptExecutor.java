package site.kason.kodel;

import groovy.lang.Script;
import java.util.HashMap;
import java.util.LinkedList;
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
public abstract class ScriptExecutor extends Script {

  public final Map<String, Object> global = new HashMap();

  private Map currentModel;

  private String outputDir = ".";

  public final List<TemplateTask> tasks = new LinkedList();

  private List<String> templatePaths = new LinkedList();

  @Override
  public void setProperty(String property, Object newValue) {
    if (currentModel != null) {
      currentModel.put(property, newValue);
    } else {
      super.setProperty(property, newValue);
    }
  }

  /**
   * Create a new template task
   *
   * @param model the data for rendering
   * @param templateName the name of the template to render
   * @param destination the destination of the rendering result
   * @param engine which template engine to use
   * @return the task created
   */
  public TemplateTask template(Map<String, Object> model, String templateName, String destination, String engine) {
    String destFile = outputDir + "/" + destination;
    if (engine == null || engine.isEmpty()) {
      engine = this.detectEngine(templateName);
    }
    TemplateTask task = new TemplateTask(model, templateName, destFile, engine);
    tasks.add(task);
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
  public TemplateTask template(Map<String, Object> model, String templateName, String destination) {
    return template(model, templateName, destination, null);
  }

  /**
   * Add a directory for finding templates
   *
   * @param path the directory for template searching
   */
  public void addTemplatePath(String path) {
    this.templatePaths.add(0, path);
  }

  /**
   * Specify the directory for output
   *
   * @param outDir the new directory
   */
  public void outputDir(String outDir) {
    this.outputDir = outDir;
  }

  /**
   * Get the template paths
   * @return the template paths
   */
  public List<String> getTemplatePaths() {
    return templatePaths;
  }

  /**
   * Add a new global variable which could be used in every template
   *
   * @param name the name of the global variable
   * @param value the value of the global variable
   */
  public void global(String name, Object value) {
    this.global.put(name, value);
  }

  private String detectEngine(String tplName) {
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
