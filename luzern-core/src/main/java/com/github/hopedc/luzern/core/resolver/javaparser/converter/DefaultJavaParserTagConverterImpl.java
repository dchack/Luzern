package com.github.hopedc.luzern.core.resolver.javaparser.converter;

import com.github.hopedc.luzern.core.tag.DocTag;
import com.github.hopedc.luzern.core.tag.DocTagImpl;
import com.github.hopedc.luzern.core.utils.CommentUtils;

/**
 * 基于JavaParser包的默认注释解析转换器
 *
 * @author hopedc
 * @date 2017/3/4
 */
public class DefaultJavaParserTagConverterImpl implements JavaParserTagConverter<String> {

    @Override
    public DocTag converter(String comment) {
        String tagType = CommentUtils.getTagType(comment);
        String coment = comment.substring(tagType.length()).trim();
        return new DocTagImpl(tagType, coment);
    }
}
