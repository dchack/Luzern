package com.github.hopedc.luzern.core.resolver;

import com.github.hopedc.luzern.core.model.ApiModule;

import java.util.List;

/**
 * 所有的解析器实现都要继承此接口，默认使用javaparser实现
 * @author dongchao
 * @date 2019-06-02
 */
public interface Resolver {

    /**
     * 解析文件方法
     * @param files
     * @return
     */
    List<ApiModule> resolve(List<String> files);
}
