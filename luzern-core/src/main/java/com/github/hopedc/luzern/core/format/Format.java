package com.github.hopedc.luzern.core.format;

import com.github.hopedc.luzern.core.model.ApiDoc;

/**
 * 文档输出格式
 * <p>
 * Created by hopedc on 2018/6/22.
 */
public interface Format {

    String format(ApiDoc apiDoc);
}
