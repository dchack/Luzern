package com.github.hopedc.luzern.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hopedc
 * @date 2017-03-09 15:43
 */
@Data
@ConfigurationProperties("luzern")
public class luzernProperties {

    /**
     * 是否启动luzern,此值便于在生产等环境启动程序时增加参数进行控制
     */
    private boolean enable = true;

    /**
     * 界面标题描述
     */
    private String title = "luzern 接口文档";

    /**
     * 源码相对路径(支持多个,用英文逗号隔开)
     */
    private String sourcePath;

    /**
     * 文档版本号
     */
    private String version;

}
