package ru.hse.java.implementor;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.file.Paths;
import java.util.*;

public class MyImplementor implements Implementor {

    private Class<?> toImplementClass;
    private String className;
    private final String outputDirectory;
    private final StringBuilder result;

    public MyImplementor(final String outputDirectory) {
        result = new StringBuilder();
        this.outputDirectory = outputDirectory;
    }

    @Override
    public String implementFromDirectory(String directoryPath, String className) throws ImplementorException {
        URL url;
        try {
            url = Paths.get(directoryPath).toAbsolutePath().toUri().toURL();
        } catch (MalformedURLException e) {
            throw new ImplementorException("The url is not well formed", e);
        }
        try {
            ClassLoader classLoader = new URLClassLoader(new URL[]{url});
            toImplementClass = classLoader.loadClass(className);
            return generateClass(toImplementClass.getPackage());
        } catch (ClassNotFoundException e) {
            throw new ImplementorException("Input class not found", e);
        }
    }

    @Override
    public String implementFromStandardLibrary(String className) throws ImplementorException {
        try {
            ClassLoader classLoader = MyImplementor.class.getClassLoader();
            toImplementClass = classLoader.loadClass(className);
            return generateClass(null);
        } catch (ClassNotFoundException e) {
            throw new ImplementorException("Input class not found", e);
        }
    }

    private String generateClass(Package pkg) throws ImplementorException {
        if (Modifier.isFinal(toImplementClass.getModifiers())) {
            throw new ImplementorException("Class is final");
        }
        if (!toImplementClass.isInterface()) {
            boolean flag = false;
            for (Constructor<?> c : toImplementClass.getDeclaredConstructors()) {
                if (!Modifier.isPrivate(c.getModifiers())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                throw new ImplementorException("Private constructors");
            }
        }
        String implName = toImplementClass.getSimpleName() + "Impl";
        String newDirectory = "";
        if (pkg != null) {
            newDirectory = pkg.getName().replaceAll("\\.", File.separator);
        }
        File outputFile = Paths.get(outputDirectory, newDirectory, implName + ".java").toFile();
        try {
            outputFile.getParentFile().mkdirs();
            outputFile.createNewFile();
        } catch (IOException e) {
            throw new ImplementorException("Couldn't create file", e);
        }
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            className = implName;
            if (pkg != null) {
                createPackage(pkg);
                result.append("\n");
            }
            createClass();
            fileWriter.append(result);
        } catch (IOException e) {
            throw new ImplementorException("Couldn't write to file", e);
        }
        if (pkg != null) {
            return pkg.getName() + "." + implName;
        } else {
            return implName;
        }
    }

    private void createPackage(Package pkg) {
        String formatPackage = String.format("package %s;\n", pkg.getName());
        result.append(formatPackage);
    }

    private void createClass() {
        result.append("public class ").append(className).append(" ");
        result.append(toImplementClass.isInterface() ? "implements " : "extends ");
        result.append(toImplementClass.getCanonicalName()).append(" {\n");
        for (Method method : getAllMethods(toImplementClass)) {
            if (Modifier.isAbstract(method.getModifiers())) {
                result.append("\n");
                createMethod(method);
                result.append("}\n");
            }
        }
        result.append("}\n");
    }

    private void getMethods(List<Method> methods, List<Method> allMethodsList, Set<List<String>> allMethodsSet, boolean flag) {
        for (Method method : methods) {
            List<String> lst = new ArrayList<>();
            lst.add(method.getName());
            for (Class<?> c : method.getParameterTypes()) {
                lst.add(c.getCanonicalName());
            }
            if (!flag) {
                allMethodsSet.add(lst);
            } else {
                if (!allMethodsSet.contains(lst)) {
                    allMethodsSet.add(lst);
                    allMethodsList.add(method);
                }
            }
        }
    }

    private List<Method> getAllMethods(Class<?> clazz) {
        List<Method> allMethodsList = new ArrayList<>(Arrays.asList(clazz.getDeclaredMethods()));
        Set<List<String>> allMethodsSet = new HashSet<>();
        getMethods(allMethodsList, allMethodsList, allMethodsSet, false);
        for (Class<?> c : clazz.getInterfaces()) {
            getMethods(getAllMethods(c), allMethodsList, allMethodsSet, true);
        }
        if (clazz.getSuperclass() != null) {
            getMethods(getAllMethods(clazz.getSuperclass()), allMethodsList, allMethodsSet, true);
        }
        return allMethodsList;
    }

    private void createMethod(Method method) {
        Class<?> returnType = method.getReturnType();
        String returnTypeString = returnType.getCanonicalName();
        int type = Modifier.FINAL | Modifier.STATIC | Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
        StringBuilder builder = new StringBuilder();
        builder.append(Modifier.toString(method.getModifiers() & type)).append(" ");
        builder.append(returnTypeString).append(" ").append(method.getName()).append("(");
        Parameter[] parameters = method.getParameters();
        for (Parameter param : parameters) {
            builder.append(param.getType().getCanonicalName()).append(" ").append(param.getName()).append(",");
        }
        if (parameters.length > 0) {
            builder.setLength(builder.length() - 1);
        }
        builder.append(")").append(createExceptions(method.getExceptionTypes()));
        result.append(" ").append(builder).append(" {\n").append(" ");
        if (!returnType.isPrimitive()) {
            result.append(" return null;");
        } else if (returnTypeString.equals("boolean")) {
            result.append(" return true;");
        } else if (returnTypeString.equals("void")) {
            result.append(" return;");
        } else if (returnTypeString.equals("double") || returnTypeString.equals("false")) {
            result.append(" return 0.0;");
        } else {
            result.append(" return 0;");
        }
        result.append("\n ");
    }

    private StringBuilder createExceptions(Class<?>[] exceptions) {
        StringBuilder builder = new StringBuilder();
        if (exceptions.length > 0) {
            builder.append(" throws ");
            for (Class<?> e : exceptions) {
                builder.append(e.getCanonicalName()).append(", ");
            }
            builder.setLength(builder.length() - 2);
        }
        return builder;
    }
}