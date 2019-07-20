package org.treeleafj.xdoc.test;

import com.github.hopedc.luzern.core.luzern;
import com.github.hopedc.luzern.core.model.ApiDoc;
import com.github.hopedc.luzern.core.model.ApiModule;
import com.github.hopedc.luzern.spring.resolver.SpringResolver;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import com.github.hopedc.luzern.spring.format.HtmlForamt;
import com.github.hopedc.luzern.spring.format.MarkdownFormat;
import com.github.hopedc.luzern.spring.framework.SpringWebFramework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hopedc on 2017/3/3 003.
 */
public class luzernTest {

    @Test
    public void buildMarkdown() {
        //生成离线的Markdown格式的接口文档
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String rootDir = System.getProperty("user.dir");
        luzern luzern = new luzern(rootDir + "/src/main/java/org/treehopedcj", new SpringWebFramework());
        luzern.build(out, new MarkdownFormat());

        System.out.println(out.toString());
    }

    @Test
    public void buildMarkdown1() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String file1 = "/Users/dongchao/dc/github/Luzern/luzern-spring-test/src/main/java/com/github/hopedc/luzern/test/controller/UserController.java";
        String file2 = "/Users/dongchao/dc/github/Luzern/luzern-spring-test/src/main/java/com/github/hopedc/luzern/test/vo/User.java";
//        FileInputStream in = new FileInputStream(file);
//        CompilationUnit cu = JavaParser.parse(in);
//        TypeDeclaration typeDeclaration = cu.getTypes().get(0);
//        System.out.printf(typeDeclaration.getComment().get().getContent());
        List<String> files = new ArrayList();
        files.add(file1);
        files.add(file2);
        SpringResolver springResolver = new SpringResolver();
        List<ApiModule> modules = springResolver.resolve(files);
        ApiDoc apiDoc = new ApiDoc(modules);
        String s = new MarkdownFormat().format(apiDoc);
        try {
            IOUtils.write(s, out, "UTF-8");
        } catch (IOException e) {
//            log.error("接口文档写入文件失败", e);
        } finally {
            IOUtils.closeQuietly(out);
        }
//        luzern.build(out, new MarkdownFormat());

        System.out.println(out.toString());
    }

    @Test
    public void buildHtml() throws Exception {
        //生成离线的HTML格式的接口文档
        String userDir = System.getProperty("user.dir");
        FileOutputStream out = new FileOutputStream(new File(userDir, "api.html"));
        luzern luzern = new luzern(userDir + "/src/main/java/org/treehopedcj", new SpringWebFramework());
        luzern.build(out, new HtmlForamt());
    }
}
