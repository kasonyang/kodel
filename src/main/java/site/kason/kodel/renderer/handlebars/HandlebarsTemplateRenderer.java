package site.kason.kodel.renderer.handlebars;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import site.kason.kodel.TemplateException;
import site.kason.kodel.TemplateRenderer;

/**
 *
 * @author Kason Yang
 */
public class HandlebarsTemplateRenderer implements TemplateRenderer {
  
  public final static String ENGINE_NAME = "handlebars";

  TemplateFactory tplFactory = new TemplateFactory(".", ".hbs");

  public HandlebarsTemplateRenderer() {
    //System.err.println("warning:the handlebars engine doesn't support global varible");
  }

  @Override
  public String renderStringTemplate(String stringTemplate, Map<String, Object> data) throws IOException {
    return tplFactory.renderString(stringTemplate, data);
  }

  @Override
  public String renderFileTemplate(File fileTemplate, Map<String, Object> data) throws IOException, TemplateException {
    return tplFactory.render(fileTemplate, data);
  }

}
