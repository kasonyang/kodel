package site.kason.kodel;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 *
 * @author Kason Yang
 */
public class App {

  private final static String SYNTAX = "kodel [options] model template destination";

  private final static Options OPTIONS;

  static {
    OPTIONS = new Options();
    OPTIONS.addOption("h", false, "show this help message")
            .addOption("overwrite", false, "overwrite if destination exists")
            .addOption("collect", false, "render all models in one file")
            .addOption("cp", true, "specifies class path")
            .addOption("tp", true, "specifies template path")
            .addOption("o", true, "specifies output directory");
  }

  public static void printUsage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(SYNTAX, OPTIONS);
  }

  public static File findTemplateFile(List<String> templatePaths, String template) {
    for (String p : templatePaths) {
      File f = new File(p, template);
      if (f.exists()) {
        return f;
      }
    }
    return null;
  }

  public static void main(String[] args) throws ParseException, CompilationFailedException, IOException, TemplateException {
    DefaultParser parser = new DefaultParser();
    CommandLine cli = parser.parse(OPTIONS, args);
    if (cli.hasOption("h")) {
      printUsage();
      return;
    }
    String cliArgs[] = cli.getArgs();
    if (cliArgs.length == 0) {
      printUsage();
      return;
    }
    File modelFile = new File(cliArgs[0]);
    String cp = cli.getOptionValue("cp", new File(modelFile.getParent(), "classes").getAbsolutePath());
    URLClassLoader classLoader = new URLClassLoader(
            new URL[]{new File(cp).toURL()}, App.class.getClassLoader()
    );
    CompilerConfiguration config = new CompilerConfiguration();
    config.setSourceEncoding("utf-8");
    //config.setClasspath(cp);
    File appHome = new File(FileUtils.getUserDirectoryPath(), ".kodel");
    String homeClassPath = new File(appHome, "classes").getAbsolutePath();
    config.getClasspath().add(homeClassPath);
    //System.out.println(cp);
    config.setScriptBaseClass(ScriptExecutor.class.getName());
    GroovyShell shell = new GroovyShell(classLoader, new Binding(), config);
    ScriptExecutor script = (ScriptExecutor) shell.parse(modelFile);
    String outputDir = cli.getOptionValue("o", ".");
    script.outputDir(outputDir);
    String homeTemplatePath = new File(appHome, "templates").getAbsolutePath();
    String tp = cli.getOptionValue("tp", "templates");
    script.addTemplatePath(homeTemplatePath);
    script.addTemplatePath(tp);
    script.run();
    KodelRenderer renderer = new KodelRenderer(classLoader);
    List<TemplateTask> tasks = script.tasks;
    Map<String, Object> global = script.global;
    List<String> templatePaths = script.getTemplatePaths();
    for (TemplateTask t : tasks) {
      String tpl = t.getTemplate();
      File tplFile = findTemplateFile(templatePaths, tpl);
      if (tplFile == null) {
        throw new IOException("template not found:" + tpl + ",template paths:" + templatePaths);
      }
      Map<String, Object> data = new HashMap();
      data.putAll(t.getModel());
      data.putAll(global);
      renderer.render(data, tplFile, t.getDestination(), t.isOverwrite(),t.getEngine());
    }
  }

}
