package com.github.hopedc.luzern.test;

import com.github.hopedc.luzern.spring.resolver.SpringResolver;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.TypeDeclaration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dongchao
 * @date 2019-05-19
 * @desc
 */
public class Test {

    public static void main(String[] args) throws FileNotFoundException {
        String file1 = "/Users/dongchao/dc/github/Luzern/luzern-spring-test/src/main/java/com/github/hopedc/luzern/test/controller/UserController.java";
        String file2 = "/Users/dongchao/dc/github/Luzern/luzern-spring-test/src/main/java/com/github/hopedc/luzern/test/vo/User.java";
//        FileInputStream in = new FileInputStream(file);
//        CompilationUnit cu = JavaParser.parse(in);
//        TypeDeclaration typeDeclaration = cu.getTypes().get(0);
//        System.out.printf(typeDeclaration.getComment().get().getContent());
        List<String> files = new ArrayList();
        files.add(file1);
        files.add(file2);
        SpringResolver springResolver = new SpringResolver();
        springResolver.resolve(files);
//        typeDeclaration.getme

    }
}
