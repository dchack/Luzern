package com.github.hopedc.luzern.spring.framework;

import lombok.Data;
import com.github.hopedc.luzern.core.model.ApiAction;
import com.github.hopedc.luzern.core.model.ObjectInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hopedc on 2017/3/4.
 */
@Data
public class SpringApiAction extends ApiAction {

    /**
     * 访问的uri地址
     */
    private List<String> uris;

    /**
     * 允许的访问方法:POST,GET,DELETE,PUT等, 如果没有,则无限制
     */
    private List<String> methods;

    /**
     * 入参
     */
    private List<ParamInfo> param;

    /**
     * 返回对象
     */
    private List<ParamInfo> returnParam;

    /**
     * 出参
     */
    private List<ParamInfo> respParam = new ArrayList<>(0);

    /**
     * 返回描述
     */
    private String returnDesc;

    /**
     * 返回的数据
     */
    private String respbody;

    /**
     * 是否返回json
     */
    private boolean json;


}
