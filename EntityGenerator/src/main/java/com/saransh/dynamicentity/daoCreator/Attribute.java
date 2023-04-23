package com.saransh.dynamicentity.daoCreator;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Optional;


public class Attribute {
    private String name;
    private Class<?> type;


    private boolean nested;
    private List<Attribute> nestedAttributes;

    public Attribute(String name, Class<?> type, boolean nested, List<Attribute> nestedAttributes) {
        this.name = name;
        this.type = type;
        this.nested = nested;
        this.nestedAttributes = nestedAttributes;
    }

    public Attribute(JsonNode node) throws ClassNotFoundException {
        this.name = node.get("name").asText();
        this.type = Class.forName(node.get("type").asText());
        boolean nested = Optional.of(node.get("nested").asBoolean(false)).orElse(false);
        if (nested) {
            // Recursively parse nested attributes
            JsonNode nestedConfig = node.get("attributes");
            for (JsonNode nestedNode : nestedConfig) {
                this.nestedAttributes.add(new Attribute(nestedNode));
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isNested() {
        return nested;
    }

    public void setNested(boolean nested) {
        this.nested = nested;
    }

    public List<Attribute> getNestedAttributes() {
        return nestedAttributes;
    }

    public void setNestedAttributes(List<Attribute> nestedAttributes) {
        this.nestedAttributes = nestedAttributes;
    }
}
