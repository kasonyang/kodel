package site.kason.kodel.renderer.handlebars;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;

/**
 * create file from templates in resource
 * @author Kason Yang
 */
public class TemplateFactory {

    private final Handlebars hbs;
    private File tplDir;
    private final String suffix;

     public TemplateFactory(String tplDir){
         this(tplDir, ".hbs");
     }
    
    public TemplateFactory(String tplDir,String suffix) {
        this.tplDir = new File(tplDir);
        this.suffix = suffix;
        //ClassPathTemplateLoader loader = new ClassPathTemplateLoader(tplDir);
        FileTemplateLoader loader = new FileTemplateLoader(tplDir);
        loader.setSuffix(suffix);
        hbs = new Handlebars(loader);
    }
    
    public File[] listTemplate(){
        Collection<File> files = FileUtils.listFiles(this.tplDir,null, false);
        List<File> retList = new ArrayList(files.size());
        for(File f:files){
            if(f.getName().endsWith(this.suffix)){
                retList.add(f);
            }
        }
        return retList.toArray(new File[retList.size()]);
    }
    
    public void render(File file,Object data,String destPath) throws IOException{
        this.renderString(FileUtils.readFileToString(file, "utf-8"), data,new File(destPath));
    }
    
    public String render(File file,Object data) throws IOException{
        return this.renderString(FileUtils.readFileToString(file, "utf-8"), data); 
    }
    
    public void render(String tplName, Object data, String destPath) throws IOException {
        render(tplName, data, new File(destPath));
    }

    public void render(String tplName, Object data, File destPath) throws IOException {
        Template tpl = hbs.compile(tplName);
        FileUtils.writeStringToFile(destPath,renderTemplate(tpl, data),"utf-8");
    }
    
    public void renderString(String tplString,Object data,File destPath) throws IOException{
        Template tpl = hbs.compileInline(tplString);
        FileUtils.writeStringToFile(destPath, renderTemplate(tpl, data),"utf-8");
    }
    
    public String renderString(String tplString,Object data) throws IOException{
        Template tpl = hbs.compileInline(tplString);
        return renderTemplate(tpl, data);
    }
    
    private String renderTemplate(Template tpl,Object data){
        Context context = Context
                .newBuilder(data)
                .resolver(
                        FieldValueResolver.INSTANCE
                        , JavaBeanValueResolver.INSTANCE
                        , MapValueResolver.INSTANCE
                        ,MethodValueResolver.INSTANCE
                )
                .build();
        StringWriter writer = new StringWriter();
        try {
            tpl.apply(context, writer);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return writer.toString();
    }

}
