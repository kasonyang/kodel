package test.site.kason.kodel;

import org.junit.Test;
import site.kason.kodel.App;

/**
 *
 * @author Kason Yang
 */
public class ExampleTest {
    
    public ExampleTest() {
    }
    
    @Test
    public void test() throws Exception{
        test("example/hello.kodel");
        test("example/hello.kodel.kl");        
    }
    
    private void test(String modelFile) throws Exception{
        String[] args = new String[]{
            modelFile,
            "--templatepath","example/templates",
            "--out","build/example/"
        };
        App.main(args);
    }
    
}
