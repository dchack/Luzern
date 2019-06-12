package com.github.hopedc.luzern.core.resolver.javaparser.converter;

import com.github.hopedc.luzern.core.tag.DocTag;
import com.github.hopedc.luzern.core.tag.RespTagImpl;
import com.github.hopedc.luzern.tag.ParamTagImpl;

/**
 * 针对@resp的转换器
 * @author hopedc
 * @date 2017/3/12 0012
 */
public class RespTagConverter extends ParamTagConverter {

    @Override
    public DocTag converter(String comment) {
        ParamTagImpl paramTag = (ParamTagImpl) super.converter(comment);
        RespTagImpl respTag = new RespTagImpl(paramTag.getTagName(), paramTag.getParamName(), paramTag.getParamDesc(),
                paramTag.getParamType(), paramTag.isRequire());
        return respTag;
    }
}
