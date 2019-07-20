package com.github.hopedc.luzern.core;

import lombok.Setter;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.hopedc.luzern.core.format.Format;
import com.github.hopedc.luzern.core.framework.Framework;
import com.github.hopedc.luzern.core.model.ApiDoc;
import com.github.hopedc.luzern.core.model.ApiModule;
import com.github.hopedc.luzern.core.resolver.DocTagResolver;
import com.github.hopedc.luzern.core.resolver.javaparser.JavaParserDocTagResolver;
import com.github.hopedc.luzern.core.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * luzern主入口,核心处理从这里开始
 *
 * @author hopedc
 * @date 2017-03-03 16:25
 */
public class luzern {

    private static final String CHARSET = "utf-8";

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 源码路径
     */
    private List<String> srcPaths;

    /**
     * api框架类型
     */
    @Setter
    private List<Framework> frameworks = new ArrayList<>();

    /**
     * 默认的java注释解析器实现
     * <p>
     * 备注:基于sun doc的解析方式已经废弃,若需要请参考v1.0之前的版本
     *
     * @see com.github.hopedc.luzern.core.resolver.javaparser.JavaParserDocTagResolver
     */
    @Setter
    private DocTagResolver docTagResolver = new JavaParserDocTagResolver();

    /**
     * 构建luzern对象
     *
     * @param srcPath 源码路径
     */
    public luzern(String srcPath, Framework framework) {
        this(Arrays.asList(srcPath), framework);
    }

    /**
     * 构建luzern对象
     *
     * @param srcPaths 源码路径,支持多个
     */
    public luzern(List<String> srcPaths, Framework framework) {
        this.srcPaths = srcPaths;
        frameworks.add(framework);
    }

    /**
     * 解析源码并返回对应的接口数据
     *
     * @return API接口数据
     */
    public ApiDoc resolve() {
        // java doc 基础解析
        List<ApiModule> apiModules = this.docTagResolver.resolve(getAllFiles());

        // 附加解析
        for (Framework framework : frameworks) {
            apiModules = framework.extend(apiModules);
        }

        return new ApiDoc(apiModules);
    }

    private List<String> getAllFiles() {
        List<String> files = new ArrayList<>();
        for (String srcPath : this.srcPaths) {
            File dir = new File(srcPath);
            log.info("解析源码路径:{}", dir.getAbsolutePath());
            files.addAll(FileUtils.getAllJavaFiles(dir));
        }
        return files;
    }

    /**
     * 构建接口文档
     *
     * @param out    输出位置
     * @param format 文档格式
     */
    public void build(OutputStream out, Format format) {
        this.build(out, format, null);
    }

    /**
     * 构建接口文档
     *
     * @param out        输出位置
     * @param format     文档格式
     * @param properties 文档属性
     */
    public void build(OutputStream out, Format format, Map<String, Object> properties) {
        ApiDoc apiDoc = this.resolve();
        if (properties != null) {
            apiDoc.getProperties().putAll(properties);
        }

        if (apiDoc.getApiModules() != null && out != null && format != null) {
            String s = format.format(apiDoc);
            try {
                IOUtils.write(s, out, CHARSET);
            } catch (IOException e) {
                log.error("接口文档写入文件失败", e);
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
    }
}
