package com.github.hopedc.luzern.core.resolver.javaparser.converter;

import com.github.hopedc.luzern.core.utils.JsonUtils;
import org.junit.Test;

/**
 * Created by hopedc on 2018/3/8.
 */
public class ParamTagConverterTest {

    @Test
    public void converter() throws Exception {
        ParamTagConverter converter = new ParamTagConverter();
//        String o = "@param type 账户类型(1-普通账户)|必填";
        String o = "@param user :username 账户名称|必填";
        System.out.println(JsonUtils.toJson(converter.converter("@param username 账户名称")));
        System.out.println(JsonUtils.toJson(converter.converter("@param username 账户名称|必填")));
        System.out.println(JsonUtils.toJson(converter.converter("@param user :username 账户名称|必填")));
        System.out.println(JsonUtils.toJson(converter.converter("@param user :username 账户名称|Y")));
        System.out.println(JsonUtils.toJson(converter.converter("@param username 账户名称|Boolean|必填")));
        System.out.println(JsonUtils.toJson(converter.converter("@param user :username 账户名称|N")));
        System.out.println(JsonUtils.toJson(converter.converter("@param user :username 账户名称|Y")));
        System.out.println(JsonUtils.toJson(converter.converter("@param user :username 账户名称|string")));
    }

}
