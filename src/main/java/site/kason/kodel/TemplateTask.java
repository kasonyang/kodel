package site.kason.kodel;

import java.util.Map;

/**
 *
 * @author Kason Yang
 */
public class TemplateTask {

  private String template;

  boolean overwrite;

  private final String destination;
  
  private final String engine;

  private Map<String, Object> model;

  public TemplateTask(Map<String, Object> model, String template, String dest,String engine) {
    this.template = template;
    this.destination = dest;
    this.model = model;
    this.engine = engine;
  }

  public String getTemplate() {
    return template;
  }

  public boolean isOverwrite() {
    return overwrite;
  }

  public TemplateTask overwrite(boolean overwrite) {
    this.overwrite = overwrite;
    return this;
  }

  public String getDestination() {
    return destination;
  }

  public Map<String, Object> getModel() {
    return model;
  }

  public void setModel(Map<String, Object> model) {
    this.model = model;
  }

  public String getEngine() {
    return engine;
  }

}
