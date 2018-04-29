package site.kason.kodel;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import kalang.compiler.Configuration;
import kalang.tool.KalangShell;
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
    
    private final static String APP_NAME = "kodel";

    private final static String SYNTAX = "kodel [options] BUILD_FILE";

    private final static Options OPTIONS;

    static {
        OPTIONS = new Options();
        OPTIONS.addOption("h", "help", false, "show this help message")
                .addOption(null, "classpath", true, "specifies class path")
                .addOption(null, "templatepath", true, "specifies template path")
                .addOption("o", "out", true, "specifies output directory");
    }

    public static void printUsage() {
        printVersion();
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
        if (cli.hasOption("help")) {
            printUsage();
            return;
        }
        String cliArgs[] = cli.getArgs();
        if (cliArgs.length <=0 ){
            printUsage();
            System.exit(-1);
        }
        File modelFile = new File(cliArgs[0]);
        if (!modelFile.exists()){
            System.err.println("file not found:" + modelFile.getName());
            System.exit(-2);
        }
        String cp = cli.getOptionValue("classpath", new File(modelFile.getParent(), "classes").getAbsolutePath());
        File appHome = new File(FileUtils.getUserDirectoryPath(), ".kodel");
        String homeClassPath = new File(appHome, "classes").getAbsolutePath();
        URLClassLoader classLoader = new URLClassLoader(
                new URL[]{
                    new File(homeClassPath).toURL(),
                    new File(cp).toURL()
                }
                , App.class.getClassLoader()
        );
        String outputDir = cli.getOptionValue("out", ".");
        String templatePath = cli.getOptionValue("templatepath", "templates");
        String modelFileName = modelFile.getName();
        ScriptExecutor script;
        if (modelFileName.endsWith(".kl") || modelFileName.endsWith(".kalang")){
            script = parseKalang(classLoader,modelFile);
        } else {
            script = parseGroovy(classLoader, modelFile);
        }
        script.outputDir(outputDir);
        String homeTemplatePath = new File(appHome, "templates").getAbsolutePath();
        script.addTemplatePath(homeTemplatePath);
        script.addTemplatePath(templatePath);
        script.executeScript();
        KodelRenderer renderer = new KodelRenderer(classLoader);
        List<TemplateTask> tasks = script.getTasks();
        Map<String, Object> global = script.getGlobal();
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
            renderer.render(data, tplFile, t.getDestination(), t.isOverwrite(), t.getEngine());
        }
    }

    private static void printVersion() {
        Properties prop = new Properties();
        InputStream is = App.class.getResourceAsStream("/default.properties");
        if (is != null) {
            try {
                prop.load(is);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        String version = prop.getProperty("version", "UNKNOWN");
        System.out.println(String.format("%s %s", APP_NAME, version));
    }
    
    private static ScriptExecutor parseKalang(ClassLoader classLoader,File modelFile) throws IOException{
        Configuration config = new Configuration();
        config.setScriptBaseClass(KalangScriptBase.class.getName());
        KalangShell shell = new KalangShell(config, classLoader);
        ScriptExecutor script = (ScriptExecutor) shell.parseScript(modelFile);
        return script;
    }
    
    private static ScriptExecutor parseGroovy(ClassLoader classLoader,File modelFile) throws CompilationFailedException, IOException{
        CompilerConfiguration config = new CompilerConfiguration();
        config.setSourceEncoding("utf-8");
        config.setScriptBaseClass(GroovyScriptBase.class.getName());
        GroovyShell shell = new GroovyShell(classLoader, new Binding(), config);
        GroovyScriptBase script = (GroovyScriptBase) shell.parse(modelFile);
        return script;
    }

}
