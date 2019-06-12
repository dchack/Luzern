package com.github.hopedc.luzern.core.resolver;

import com.github.hopedc.luzern.core.framework.Framework;
import com.github.hopedc.luzern.core.model.ApiModule;

import java.util.List;

/**
 * 注释解析器接口,所有的解析器实现都要继承此接口
 * <p>
 * 现有的实现有基于开源的javaparser.
 * 而sun javadoc这种已经废弃掉了
 * <p>
 *
 * @author hopedc
 * @date 2017/4/1 0001
 */
public interface DocTagResolver {

    /**
     * 执行解析
     *
     * @param files     要解析的所有java源代码文件的绝对路径
     */
    List<ApiModule> resolve(List<String> files);
}
