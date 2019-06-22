package com.github.hopedc.luzern.spring.resolver;

import com.github.hopedc.luzern.core.model.ApiAction;
import com.github.hopedc.luzern.core.model.ApiModule;
import com.github.hopedc.luzern.core.model.FieldInfo;
import com.github.hopedc.luzern.core.resolver.IgnoreApi;
import com.github.hopedc.luzern.core.resolver.Resolver;
import com.github.hopedc.luzern.core.resolver.javaparser.JavaParserDocTagResolver;
import com.github.hopedc.luzern.core.resolver.javaparser.converter.JavaParserTagConverter;
import com.github.hopedc.luzern.core.resolver.javaparser.converter.JavaParserTagConverterRegistrar;
import com.github.hopedc.luzern.core.resolver.javaparser.converter.ParamTagConverter;
import com.github.hopedc.luzern.core.resolver.javaparser.converter.TagNamesConstants;
import com.github.hopedc.luzern.core.tag.DocTag;
import com.github.hopedc.luzern.core.utils.ClassMapperUtils;
import com.github.hopedc.luzern.core.utils.CommentUtils;
import com.github.hopedc.luzern.core.utils.Constant;
import com.github.hopedc.luzern.spring.framework.ParamInfo;
import com.github.hopedc.luzern.spring.framework.SpringApiAction;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.beans.PropertyDescriptor;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dongchao
 * @date 2019-06-02
 * @desc
 */
public class SpringResolver implements Resolver {

    private Logger log = LoggerFactory.getLogger(SpringResolver.class);


    /**
     *
     * @param files
     * @return
     */
    @Override
    public List<ApiModule> resolve(List<String> files) {

        //先缓存类文件信息
        for (String file : files) {
            try (FileInputStream in = new FileInputStream(file)) {
                CompilationUnit cu = JavaParser.parse(in);
                if (cu.getTypes().size() <= 0) {
                    continue;
                }

                TypeDeclaration typeDeclaration = cu.getTypes().get(0);
                final Class<?> moduleType = Class.forName(cu.getPackageDeclaration().get().getNameAsString() + "." + typeDeclaration.getNameAsString());
                IgnoreApi ignoreApi = moduleType.getAnnotation(IgnoreApi.class);
                if (ignoreApi == null) {
                    //缓存"包名+类名"跟对应的.java文件的位置映射关系
                    ClassMapperUtils.put(moduleType.getName(), file);
                    //缓存"类名"跟对应的.java文件的位置映射关系
                    ClassMapperUtils.put(moduleType.getSimpleName(), file);
                }
            } catch (Exception e) {
                log.warn("读取文件失败:{}, {}", file, e.getMessage());
            }
        }

        List<ApiModule> apiModules = new LinkedList<>();

        for (String file : files) {
            try (FileInputStream in = new FileInputStream(file)) {

                CompilationUnit cu = JavaParser.parse(in);
                if (cu.getTypes().size() <= 0) {
                    continue;
                }
                // 类
                TypeDeclaration typeDeclaration = cu.getTypes().get(0);
                final Class<?> moduleType = Class.forName(cu.getPackageDeclaration().get().getNameAsString() + "." + typeDeclaration.getNameAsString());

                IgnoreApi ignoreApi = moduleType.getAnnotation(IgnoreApi.class);
                if (ignoreApi != null) {
                    continue;
                }

                if(moduleType.getAnnotation(Controller.class) == null
                        && moduleType.getAnnotation(RestController.class) == null){
                    continue;
                }


                final ApiModule apiModule = new ApiModule();
                apiModule.setType(moduleType);

                // todo 注释部分需要考虑换行问题
                if (typeDeclaration.getComment().isPresent()) {
                    String commentText = CommentUtils.parseCommentText(typeDeclaration.getComment().get().getContent());
                    commentText = commentText.split("\n")[0].split("\r")[0];
                    apiModule.setComment(commentText);
                }
                // 请求路径统一前缀
                String[] prefixPaths = getPrefixPathsStrings(moduleType);

                new VoidVisitorAdapter<Void>() {
                    @Override
                    public void visit(MethodDeclaration m, Void arg) {
                        Method method = parseToMethod(moduleType, m);
                        if (method == null) {
                            log.warn("查找不到方法:{}.{}", moduleType.getSimpleName(), m.getNameAsString());
                            return;
                        }

                        // 处理IgnoreApi注解
                        if (method.getAnnotation(IgnoreApi.class) != null) {
                            return;
                        }

                        Map<String, DocTag> docTagMap = getTagMap(m);

                        SpringApiAction apiAction = new SpringApiAction();
                        if (m.getComment().isPresent()) {
                            apiAction.setComment(CommentUtils.parseCommentText(m.getComment().get().getContent()));
                        }
                        apiAction.setName(m.getNameAsString());


                        // 组装uris和methods
                        buildPathMapping(method, apiAction, prefixPaths);

                        // 组装入参
                        buildParams(method, m, docTagMap, apiAction);

                        // 组装出参
                        buildReturnParams(method, apiAction);


                        apiAction.setMethod(method);
                        apiAction.setMethodDeclaration(m);
                        apiModule.getApiActions().add(apiAction);

                        super.visit(m, arg);
                    }
                }.visit(cu, null);

                apiModules.add(apiModule);

            } catch (Exception e) {
                log.error("解析{}失败{}", file, e);
                continue;
            }



        }
        return apiModules;
    }

    private Map<String, DocTag> getTagMap(MethodDeclaration m) {
        List<String> comments = CommentUtils.asCommentList(StringUtils.defaultIfBlank(m.getComment().get().getContent(), ""));

        Map<String, DocTag> docTagMap = new HashMap<>();

        for (int i = 0; i < comments.size(); i++) {
            String c = comments.get(i);
            String tagType = CommentUtils.getTagType(c);
            if (StringUtils.isBlank(tagType)) {
                continue;
            }
            JavaParserTagConverter converter = JavaParserTagConverterRegistrar.getInstance().getConverter(tagType);
            DocTag docTag = converter.converter(c);
            if (docTag != null) {
                docTagMap.put(docTag.getTagName(), docTag);
            } else {
                log.warn("识别不了:{}", c);
            }
        }
        return docTagMap;
    }

    private void buildReturnParams(Method method, SpringApiAction apiAction) {
        Class returnClass = method.getReturnType();
        Map<String, String> commentMap = analysisFieldComments(returnClass);
        List<ParamInfo> returnParam = new ArrayList<>();
        if(returnClass.isPrimitive()){
            ParamInfo paramInfo = new ParamInfo();
            paramInfo.setParamName(returnClass.getName());
            //todo
            paramInfo.setParamDesc("");
            paramInfo.setRequire(true);
            paramInfo.setParamType(returnClass.getTypeName());
            returnParam.add(paramInfo);
        }else{
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(returnClass);
            ParamInfo paramInfo = new ParamInfo();
            List<ParamInfo> properties = new ArrayList<>();
            paramInfo.setParamType("object");
            // todo
//            paramInfo.setParamDesc(prarmMap.get(parameter.getNameAsString()));
//            paramInfo.setParamName(parameter.getNameAsString());
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                //排除掉class属性
                if ("class".equals(propertyDescriptor.getName())) {
                    continue;
                }
                ParamInfo propertie = new ParamInfo();
                propertie.setParamName(propertyDescriptor.getName());
                propertie.setParamDesc(commentMap.get(propertyDescriptor.getName()));
                propertie.setParamType(propertyDescriptor.getPropertyType().getSimpleName());
                // todo
                propertie.setRequire(true);
                properties.add(propertie);
            }
            paramInfo.setProperties(properties);
            returnParam.add(paramInfo);
        }

        apiAction.setReturnParam(returnParam);
    }

    private String[] getPrefixPathsStrings(Class<?> moduleType) {
        // 在类上RequestMapping注解
        RequestMapping moduleTypeAnnontation = moduleType.getAnnotation(RequestMapping.class);
        // RequestMapping注解的path和value相互AliasFor
        return moduleTypeAnnontation.value().length > 0?moduleTypeAnnontation.value():moduleTypeAnnontation.path();
    }


    private void buildParams(Method method, MethodDeclaration m, Map<String, DocTag> docTagMap, SpringApiAction apiAction) {
        List<ParamInfo> paramInfoList = new ArrayList<>();
        NodeList<Parameter> nodeList = m.getParameters();
        Map<String, String> prarmMap = new HashMap<>();
        for(Map.Entry entry : docTagMap.entrySet()){
            if(TagNamesConstants.paramTag.equals(entry.getKey())){
                com.github.hopedc.luzern.tag.ParamTagImpl paramTag = (com.github.hopedc.luzern.tag.ParamTagImpl)entry.getValue();
                prarmMap.put(paramTag.getParamName(), paramTag.getParamDesc());
            }
        }
        for(Parameter parameter : nodeList){
            ParamInfo paramInfo = new ParamInfo();
            if(parameter.getType().isPrimitiveType()){
                paramInfo.setParamType(parameter.getType().toString());
                paramInfo.setParamName(parameter.getName().toString());
                paramInfo.setParamDesc(prarmMap.get(parameter.getName()));
                if(org.apache.commons.collections.CollectionUtils.isNotEmpty(parameter.getAnnotations())){
                    for(AnnotationExpr annotationExpr : parameter.getAnnotations()){
                        if(annotationExpr.getNameAsString().equals("NotNull")){
                            paramInfo.setRequire(true);
                        }
                        if(annotationExpr.getNameAsString().equals("RequestParam")){
                            if(annotationExpr.isSingleMemberAnnotationExpr()){
                                paramInfo.setParamName(annotationExpr.asSingleMemberAnnotationExpr().getNameAsString());
                            }
                            paramInfo.setRequire(true);
                            List<Node> childNodes = annotationExpr.getChildNodes();
                            for(Node node : childNodes){
                                if(node instanceof MemberValuePair){
                                    if("value".equals(((MemberValuePair) node).getNameAsString())){
                                        paramInfo.setParamName(((MemberValuePair) node).getValue().toString());
                                    }
                                    if("required".equals(((MemberValuePair) node).getNameAsString())){
                                        paramInfo.setRequire("true".equals(((MemberValuePair) node).getValue().toString()));
                                    }
                                }
                            }

                        }
                    }
                }
            }else{

                List<ParamInfo> properties = new ArrayList<>();
                paramInfo.setParamType("object");
                paramInfo.setParamDesc(prarmMap.get(parameter.getNameAsString()));
                paramInfo.setParamName(parameter.getNameAsString());

                for(java.lang.reflect.Parameter parameter1 : method.getParameters()){
                    Class paramClass = parameter1.getType();
                    Map<String, String> commentMap = analysisFieldComments(paramClass);
//                    String comment = commentMap.get(parameter1.getName());
//                    paramInfo.setParamDesc(comment);
                    paramInfo.setParamName(parameter1.getName());
                    PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(paramClass);
                    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                        //排除掉class属性
                        if ("class".equals(propertyDescriptor.getName())) {
                            continue;
                        }
                        ParamInfo propertie = new ParamInfo();
                        propertie.setParamName(propertyDescriptor.getName());
                        propertie.setParamDesc(commentMap.get(propertyDescriptor.getName()));
                        propertie.setParamType(propertyDescriptor.getPropertyType().getSimpleName());
                        // todo
                        propertie.setRequire(true);
                        properties.add(propertie);
                    }
                    paramInfo.setProperties(properties);
                }
            }
            paramInfoList.add(paramInfo);
        }
        apiAction.setParam(paramInfoList);
    }

    private void buildPathMapping(Method method, SpringApiAction apiAction, String[] prefixPaths) {
        RequestMapping requestMappingAnnotation = method.getAnnotation(RequestMapping.class);
        if (requestMappingAnnotation != null) {
            apiAction.setUris(getUris(prefixPaths, requestMappingAnnotation.value()));
            apiAction.setMethods(Arrays.stream(requestMappingAnnotation.method()).map(Enum::name).collect(Collectors.toList()));
        }

        PostMapping postMappingAnnotation = method.getAnnotation(PostMapping.class);
        if (postMappingAnnotation != null) {
            apiAction.setUris(getUris(prefixPaths, postMappingAnnotation.value()));
            apiAction.setMethods(CollectionUtils.arrayToList(new String[]{RequestMethod.POST.name()}));
        }

        GetMapping getMappingAnnotation = method.getAnnotation(GetMapping.class);
        if (getMappingAnnotation != null) {
            apiAction.setUris(getUris(prefixPaths, postMappingAnnotation.value()));
            apiAction.setMethods(CollectionUtils.arrayToList(new String[]{RequestMethod.GET.name()}));
        }

        PutMapping putMappingAnnotation = method.getAnnotation(PutMapping.class);
        if (putMappingAnnotation != null) {
            apiAction.setUris(getUris(prefixPaths, putMappingAnnotation.value()));
            apiAction.setMethods(CollectionUtils.arrayToList(new String[]{RequestMethod.PUT.name()}));
        }

        DeleteMapping deleteMappingAnnotation = method.getAnnotation(DeleteMapping.class);
        if (deleteMappingAnnotation != null) {
            apiAction.setUris(getUris(prefixPaths, deleteMappingAnnotation.value()));
            apiAction.setMethods(CollectionUtils.arrayToList(new String[]{RequestMethod.DELETE.name()}));
        }

        PatchMapping patchMappingAnnotation = method.getAnnotation(PatchMapping.class);
        if (patchMappingAnnotation != null) {
            apiAction.setUris(getUris(prefixPaths, patchMappingAnnotation.value()));
            apiAction.setMethods(CollectionUtils.arrayToList(new String[]{RequestMethod.PATCH.name()}));
        }
    }

    private List<String> getUris(String[] prefixPaths, String[] methodPaths) {
        List<String> uris = new ArrayList<>();
        for (String prefixPath : prefixPaths) {
            prefixPath = prefixPath.endsWith("/")?prefixPath:prefixPath+"/";
            for (String methodPath : methodPaths) {
                methodPath = methodPath.startsWith("/")?methodPath.substring(1, methodPath.length()):methodPath;
                uris.add(prefixPath + methodPath);
            }
        }
        return uris;
    }


    /**
     * 获取指定方法的所有入参类型,便于反射
     *
     * @param declaration
     * @return
     */
    private static Method parseToMethod(Class type, MethodDeclaration declaration) {
        List<Parameter> parameters = declaration.getParameters();
        parameters = parameters == null ? new ArrayList<Parameter>(0) : parameters;
        Method[] methods = type.getDeclaredMethods();
        for (Method m : methods) {
            if (!m.getName().equals(declaration.getNameAsString())) {
                continue;
            }
            if (m.getParameterTypes().length != parameters.size()) {
                continue;
            }

            boolean b = true;

            for (int j = 0; j < m.getParameterTypes().length; j++) {
                Class<?> paramClass = m.getParameterTypes()[j];
                Type ptype = parameters.get(j).getType();
                if (ptype == null) {
                    continue;
                }
                String paranTypeName = ptype.toString();
                int index = paranTypeName.lastIndexOf(".");
                if (index > 0) {
                    paranTypeName = paranTypeName.substring(index + 1);
                }
                //处理泛型
                index = paranTypeName.indexOf("<");
                if (index > 0) {
                    paranTypeName = paranTypeName.substring(0, index);
                }

                if (!paramClass.getSimpleName().equals(paranTypeName)) {
                    b = false;
                    break;
                }
            }
            if (b) {
                return m;
            }
        }
        return null;
    }





    public Map<String, String> analysisFieldComments(Class<?> classz) {

        final Map<String, String> commentMap = new HashMap(10);

        List<Class> classes = new LinkedList<>();

        Class nowClass = classz;

        //获取所有的属性注释(包括父类的)
        while (true) {
            classes.add(0, nowClass);
            if (Object.class.equals(nowClass) || Object.class.equals(nowClass.getSuperclass())) {
                break;
            }
            nowClass = nowClass.getSuperclass();
        }

        //反方向循环,子类属性注释覆盖父类属性
        for (Class clz : classes) {
            String path = ClassMapperUtils.getPath(clz.getSimpleName());
            if (StringUtils.isBlank(path)) {
                continue;
            }
            try (FileInputStream in = new FileInputStream(path)) {
                CompilationUnit cu = JavaParser.parse(in);

                new VoidVisitorAdapter<Void>() {
                    @Override
                    public void visit(FieldDeclaration n, Void arg) {
                        String name = n.getVariable(0).getName().asString();

                        String comment = "";
                        if (n.getComment().isPresent()) {
                            comment = n.getComment().get().getContent();
                        }

                        if (name.contains("=")) {
                            name = name.substring(0, name.indexOf("=")).trim();
                        }

                        commentMap.put(name, CommentUtils.parseCommentText(comment));
                    }
                }.visit(cu, null);

            } catch (Exception e) {
                log.warn("读取java原文件失败:{}", path, e.getMessage(), e);
            }
        }

        return commentMap;
    }


    public static void main(String[] args) {
        String[] strs = {"1", "2"};
        Map<String, String> map = Arrays.stream(strs).collect(Collectors.toMap(String::trim, p->p));

        for (Map.Entry e:map.entrySet()) {
            System.out.printf(e.getKey() +":" +e.getValue());

        }




    }
}
