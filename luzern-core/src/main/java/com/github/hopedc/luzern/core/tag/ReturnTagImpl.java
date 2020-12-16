package com.github.hopedc.luzern.core.tag;

public class ReturnTagImpl extends DocTag<String>{

    private String value;

    public ReturnTagImpl(String tagName) {
        super(tagName);
    }

    public ReturnTagImpl(String tagName, String value) {
        super(tagName);
        this.value = value;
    }

    @Override
    public String getValues() {
        return value;
    }
}