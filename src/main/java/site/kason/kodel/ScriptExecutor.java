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

  private String templateSource = "";

  private String destination = ".";

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
   * create a new template task
   * @param model the data for rendering
   * @param tplName the name of the template to render
   * @param destination the destination of the rendering result
   * @param engine which template engine to use
   * @return the task created
   */
  public TemplateTask template(Map<String,Object> model, String tplName, String destination,String engine) {
    String destFile = outputDir + "/" + this.destination + "/" + destination;
    if(engine==null || engine.isEmpty()){
      engine = this.detectEngine(tplName);
    }
    TemplateTask task = new TemplateTask(model, templateSource + "/" + tplName, destFile,engine);
    tasks.add(task);
    return task;
  }
  
  /**
   * As same as invoking template(model,tplName,destination,null) 
   */
  public TemplateTask template(Map<String,Object> model, String tplName, String destination){
    return template(model,tplName,destination,null);
  }

  /**
   * specify the directory to find the template
   * @param dir the directory for template searching
   */
  public void templateSource(String dir) {
    this.templateSource = dir.endsWith("/") ? dir.substring(0, dir.length() - 1) : dir;
  }

  public void addTemplatePath(String path) {
    this.templatePaths.add(0, path);
  }

  public void destination(String dest) {
    this.destination = dest;
  }

  /**
   * specify the output directory
   * @param outDir 
   */
  public void outputDir(String outDir) {
    this.outputDir = outDir;
  }

  public List<String> getTemplatePaths() {
    return templatePaths;
  }

  /**
   * add a new global variable which could be used in every template
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
