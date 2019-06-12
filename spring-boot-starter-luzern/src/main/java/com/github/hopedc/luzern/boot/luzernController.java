package com.github.hopedc.luzern.boot;

import com.github.hopedc.luzern.core.luzern;
import com.github.hopedc.luzern.core.model.ApiDoc;
import com.github.hopedc.luzern.core.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.github.hopedc.luzern.spring.framework.SpringWebFramework;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * luzern的Spring Web入口
 *
 * @author hopedc
 * @date 2017-03-09 15:36
 */
@RequestMapping("luzern")
public class luzernController {

    private Logger log = LoggerFactory.getLogger(luzernController.class);

    @Autowired
    private luzernProperties luzernProperties;

    private static ApiDoc apiDoc;

    @PostConstruct
    public void init() {
        if (!luzernProperties.isEnable()) {
            return;
        }

        String path = luzernProperties.getSourcePath();

        if (StringUtils.isBlank(path)) {
            path = ".";//默认为当前目录
        }

        List<String> paths = Arrays.asList(path.split(","));

        log.debug("starting luzern, source path:{}", paths);

        try {
            luzern luzern = new luzern(paths, new SpringWebFramework());

            Thread thread = new Thread(() -> {
                try {
                    apiDoc = luzern.resolve();
                    HashMap<String, Object> properties = new HashMap<>();
                    properties.put("version", luzernProperties.getVersion());
                    properties.put("title", luzernProperties.getTitle());
                    apiDoc.setProperties(properties);

                    log.info("start up luzern");
                } catch (Exception e) {
                    log.error("start up luzern error", e);
                }
            });
            thread.start();
        } catch (Exception e) {
            log.error("start up luzern error", e);
        }
    }

    /**
     * 跳转到luzern接口文档首页
     */
    @GetMapping
    public String index() {
        return "redirect:index.html";
    }

    /**
     * 获取所有文档api
     *
     * @return 系统所有文档接口的数据(json格式)
     */
    @ResponseBody
    @RequestMapping("apis")
    public Object apis() {
        return JsonUtils.toJson(apiDoc);
    }

    /**
     * 重新构建文档
     *
     * @return 文档页面
     */
    @GetMapping("rebuild")
    public String rebuild() {
        init();
        return "redirect:index.html";
    }
}
