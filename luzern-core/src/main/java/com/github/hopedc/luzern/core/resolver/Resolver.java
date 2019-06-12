package com.github.hopedc.luzern.core.resolver;

import com.github.hopedc.luzern.core.model.ApiModule;

import java.util.List;

/**
 * @author dongchao
 * @date 2019-06-02
 * @desc
 */
public interface Resolver {

    /**
     * 解析文件
     * @param files
     * @return
     */
    List<ApiModule> resolve(List<String> files);
}
