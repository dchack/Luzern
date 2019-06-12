package com.github.hopedc.luzern.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.hopedc.luzern.core.tag.DocTag;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 接口信息,一个接口类里面会有多个接口,每个接口都抽象成ApiAction
 *
 * @author hopedc
 * @date 2017-03-03 11:09
 */
@Data
public class ApiAction {

    /**
     * 展示用的标题
     */
    private String title;

    /**
     * 接口方法名称
     */
    private String name;

    /**
     * 接口方法
     */
    @JsonIgnore
    private transient Method method;

    @JsonIgnore
    private transient MethodDeclaration methodDeclaration;

    /**
     * 接口的描述
     */
    private String comment;

    /**
     * 方法上标注的注解
     */
    private List<DocTag> docTags;
}
