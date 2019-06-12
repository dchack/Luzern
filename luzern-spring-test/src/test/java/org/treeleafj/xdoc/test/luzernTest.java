package org.treeleafj.xdoc.test;

import com.github.hopedc.luzern.core.luzern;
import org.junit.Test;
import com.github.hopedc.luzern.spring.format.HtmlForamt;
import com.github.hopedc.luzern.spring.format.MarkdownFormat;
import com.github.hopedc.luzern.spring.framework.SpringWebFramework;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

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
    public void buildHtml() throws Exception {
        //生成离线的HTML格式的接口文档
        String userDir = System.getProperty("user.dir");
        FileOutputStream out = new FileOutputStream(new File(userDir, "api.html"));
        luzern luzern = new luzern(userDir + "/src/main/java/org/treehopedcj", new SpringWebFramework());
        luzern.build(out, new HtmlForamt());
    }
}
