package site.kason.kodel.renderer.pebble;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import site.kason.kodel.TemplateException;
import site.kason.kodel.TemplateRenderer;

/**
 *
 * @author Kason Yang
 */
public class PebbleTemplateRenderer implements TemplateRenderer {
  
  public static final String ENGINE_NAME = "pebble";

  PebbleEngine pbEngine = new PebbleEngine.Builder().build();
  PebbleEngine stringPbEngine = new PebbleEngine.Builder().loader(new StringLoader()).build();

  @Override
  public String renderStringTemplate(String stringTemplate, Map<String, Object> data) throws IOException, TemplateException {
    try {
      PebbleTemplate tpl = stringPbEngine.getTemplate(stringTemplate);
      StringWriter writer = new StringWriter();
      tpl.evaluate(writer, data);
      return writer.toString();
    } catch (PebbleException ex) {
      throw new TemplateException(ex);
    }
  }

  @Override
  public String renderFileTemplate(File fileTemplate, Map<String, Object> data) throws IOException, TemplateException {
    try {
      PebbleTemplate tpl = pbEngine.getTemplate(fileTemplate.getAbsolutePath());
      StringWriter writer = new StringWriter();
      tpl.evaluate(writer, data);
      return writer.toString();
    } catch (PebbleException ex) {
      throw new TemplateException(ex);
    }
  }

}
