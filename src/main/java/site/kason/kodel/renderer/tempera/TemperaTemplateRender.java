package site.kason.kodel.renderer.tempera;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import site.kason.kodel.TemplateException;
import site.kason.kodel.TemplateRenderer;
import org.apache.commons.codec.digest.DigestUtils;
import site.kason.tempera.engine.Configuration;
import site.kason.tempera.engine.Engine;
import site.kason.tempera.engine.Template;
import site.kason.tempera.source.FileTemplateSource;

/**
 *
 * @author Kason Yang
 */
public class TemperaTemplateRender implements TemplateRenderer {
  
  public static final String ENGINE_NAME = "tempera";


  private final Engine engine;

  public TemperaTemplateRender(ClassLoader classLoader) {
    Configuration conf = new Configuration();
    conf.setClassLoader(classLoader);
    engine = new Engine(conf);
  }

  @Override
  public String renderStringTemplate(String stringTemplate, Map<String, Object> data) throws IOException, TemplateException {
    String key = DigestUtils.md5Hex(stringTemplate);
    return engine.compileInline(stringTemplate, key).render(data);
  }

  @Override
  public String renderFileTemplate(File fileTemplate, Map<String, Object> data) throws IOException, TemplateException {
    String tplName = fileTemplate.getName().split("\\.")[0];
    Template tpl = engine.compile(new FileTemplateSource(tplName, fileTemplate, "utf-8"));
    return tpl.render(data);
  }

}
