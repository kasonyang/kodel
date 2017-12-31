package site.kason.kodel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author Kason Yang
 */
public interface TemplateRenderer {

  public String renderStringTemplate(String stringTemplate, Map<String, Object> data) throws IOException, TemplateException;

  public String renderFileTemplate(File fileTemplate, Map<String, Object> data) throws IOException, TemplateException;

}
