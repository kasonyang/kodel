package site.kason.kodel;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import site.kason.kodel.renderer.handlebars.HandlebarsTemplateRenderer;
import site.kason.kodel.renderer.pebble.PebbleTemplateRenderer;
import site.kason.kodel.renderer.tempera.TemperaTemplateRender;

/**
 *
 * @author Kason Yang
 */
public class KodelRenderer {

  private ClassLoader classLoader;

  public KodelRenderer(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  protected void rendered(File file) {
    System.out.println("rendered:" + file);
  }

  public void render(Map<String, Object> data, File tplFile, String destPathPattern, boolean overwrite,String engineName) throws CompilationFailedException, IOException, TemplateException {
    TemplateRenderer tplRenderer = getRendererByEngine(this.classLoader, engineName);
    //TODO check destPath
    File realDestFile = new File(tplRenderer.renderStringTemplate(destPathPattern, data));
    if (!overwrite && realDestFile.exists()) {
      return;
    }
    String out = tplRenderer.renderFileTemplate(tplFile, data);
    FileUtils.writeStringToFile(realDestFile, out, "utf-8");
    rendered(realDestFile);
  }
  
  private static TemplateRenderer getRendererByEngine(ClassLoader classLoader, String engineName) {
    TemplateRenderer tplRenderer;
    switch (engineName) {
      case HandlebarsTemplateRenderer.ENGINE_NAME:
        tplRenderer = new HandlebarsTemplateRenderer();
        break;
      case TemperaTemplateRender.ENGINE_NAME:
        tplRenderer = new TemperaTemplateRender(classLoader);
        break;
      case PebbleTemplateRenderer.ENGINE_NAME:
        tplRenderer = new PebbleTemplateRenderer();
        break;
      default:
        throw new IllegalArgumentException("unknown engine:" + engineName);
    }
    return tplRenderer;
  }

}

