package com.github.hopedc.luzern.core.resolver.javaparser.converter;

import com.github.hopedc.luzern.core.tag.DocTag;
import com.github.hopedc.luzern.core.tag.DocTagImpl;
import com.github.hopedc.luzern.core.tag.ReturnTagImpl;

/**
 * @author dongchao
 * @date 2019-06-22
 * @desc
 */
public class ReturnTagConverter extends DefaultJavaParserTagConverterImpl {

    @Override
    public DocTag converter(String comment) {
        DocTagImpl docTag = (DocTagImpl) super.converter(comment);
        return new ReturnTagImpl(docTag.getTagName(), docTag.value);
    }
}
