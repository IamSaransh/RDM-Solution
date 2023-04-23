package com.saransh.dynamicentity.daoCreator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

@Component
public class ConfigParser {

    @Autowired
    @Qualifier("yamlMapper")
    ObjectMapper objectMapper;

    @Autowired
    private ResourceLoader resourceLoader;






    public void involkingMethod() throws Exception {
        List<Attribute> attributes = getAttributes();
        List<CtClass> classesCreated = new ArrayList<>();

        // Create the parent class
        ClassPool pool = ClassPool.getDefault();
        CtClass parentClass = pool.makeClass("Attribute");
        classesCreated.add(parentClass);
        //created all the classes and stores them in first arguement list
        recursiveMethod(classesCreated, pool, attributes, parentClass);

        // Export the classes to a JAR file
        JarOutputStream jarOut = new JarOutputStream(new FileOutputStream("entity.jar"));
        for(CtClass clazz: classesCreated){
            jarOut.putNextEntry(new JarEntry(clazz.getName()+ ".class"));
            jarOut.write(clazz.toBytecode());
            jarOut.closeEntry();
        }
        jarOut.close();
    }


    public void recursiveMethod(List<CtClass> classesCreated, ClassPool pool, List<Attribute> attributes, CtClass parentClass) throws Exception {

        for (Attribute attribute : attributes) {
            if (!attribute.isNested()) {
                CtField ctField = new CtField(pool.get(List.class.getName()), attribute.getName(), parentClass);
                ctField.setModifiers(Modifier.PRIVATE);
                String typeName = attribute.getType().getTypeName();
                String internalName = "L" + typeName.replace(".", "/") + ";";
                ctField.setGenericSignature("Ljava/util/List<" + internalName + ">;");
                parentClass.addField(ctField);
                //create setter method for the list field
                CtMethod setterMethod = new CtMethod(CtClass.voidType, "set" + capitalizeFirstLetter(attribute.getName()), new CtClass[]{pool.get(List.class.getName())}, parentClass);
                setterMethod.setModifiers(Modifier.PUBLIC);
                setterMethod.setBody("{ this." + attribute.getName() + " = $1; }");
                parentClass.addMethod(setterMethod);
                //create getter method for the list field
                CtMethod getterMethod = new CtMethod(pool.get(List.class.getName()), "get" + capitalizeFirstLetter(attribute.getName()), new CtClass[]{}, parentClass);
                getterMethod.setModifiers(Modifier.PUBLIC);
                getterMethod.setBody("{ return this." + attribute.getName() + "; }");
                getterMethod.setGenericSignature("()Ljava/util/List<" + internalName + ">;");
                parentClass.addMethod(getterMethod);
            } else {
                // we gace a nested attribute
                CtClass nestedChildInsideParent = pool.makeClass(attribute.getName());
                classesCreated.add(nestedChildInsideParent);
                CtField ctField = new CtField(nestedChildInsideParent, nestedChildInsideParent.getName().toLowerCase(), parentClass);
                parentClass.addField(ctField);
                recursiveMethod(classesCreated, pool, attribute.getNestedAttributes(), nestedChildInsideParent);
            }
        }
    }

    private JsonNode readConfig(String resourceNameInClassPath) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + resourceNameInClassPath);
        InputStream inputStream = resource.getInputStream();
        return objectMapper.readTree(inputStream);
    }


    //TODO: make this method also recursive to supprot multiple nested within nested
    public List<Attribute> getAttributes() throws IOException, ClassNotFoundException {
        List<Attribute> attributes = new ArrayList<>();

        for (JsonNode node : readConfig("entityModel.yml")) {
            String name = node.get("name").asText();
            String typeName = node.get("type").asText();
            Class<?> type = Class.forName(typeName);
            boolean nested = node.get("nested").asBoolean(false);
            List<Attribute> nestedAttributes = new ArrayList<>();
            if (nested) {
                // Recursively parse nested attributes
                JsonNode nestedConfig = node.get("attributes");
                for (JsonNode nestedNode : nestedConfig) {
                    nestedAttributes.add(new Attribute(nestedNode));
                }
            }
            attributes.add(new Attribute(name, type, nested, nestedAttributes));
        }
        return attributes;
    }




    private String capitalizeFirstLetter(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        char firstChar = Character.toUpperCase(name.charAt(0));
        if (name.length() == 1) {
            return String.valueOf(firstChar);
        }
        return firstChar + name.substring(1);
    }

}
