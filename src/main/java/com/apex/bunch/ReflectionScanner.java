package com.apex.bunch;

import com.apex.core.Constants;
import com.apex.exception.ReflectionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class ReflectionScanner {
    private static final Map<Class<?>, Object> classObjectMap = new HashMap<>();

    public static void autocreationResolver() {
        List<Class<?>> classes = ClassFinder.find(Constants.PACKAGE_FOR_SCAN);
        for (Class<?> clazz : classes) {
            Object bean;
            if (clazz.isAnnotationPresent(AutoCreation.class)) {
                try {
                    bean = clazz.getDeclaredConstructor().newInstance();
                    addNewBean(clazz, bean);

                    for (Class<?> iface : clazz.getInterfaces()) {
                        addNewBean(iface, bean);
                    }

                    Class<?> superclass = clazz.getSuperclass();
                    if (superclass != null && superclass != Object.class) {
                        addNewBean(superclass, bean);
                    }
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw
                            new ReflectionException("Untenable to create obj of class " + clazz.getSimpleName() +
                                    ". Make sure it's contains no args constructor");
                }
            }
        }
    }

    public static void wireContext() throws InvocationTargetException, IllegalAccessException {
        for (Object bean : new HashSet<>(classObjectMap.values())) {
            initializeObject(bean);
        }
    }

    public static void initializeObject(Object object) throws InvocationTargetException, IllegalAccessException {
        fieldsInjectionResolver(object);
        parametersInjectionResolver(object);
    }

    public static void fieldsInjectionResolver(Object object) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(AutoInject.class)) continue;
            field.setAccessible(true);
            Object bean = findCompatibleBean(field.getType());
            if (Objects.isNull(bean)) throw new ReflectionException("Not found bean with type " + field.getType());
            field.set(object, bean);
        }
    }

    public static void parametersInjectionResolver(Object object) throws InvocationTargetException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            Parameter[] parameters = method.getParameters();
            Object[] parametersForCall = new Object[method.getParameters().length];
            boolean hasAnnotation = false;

            for (int i = 0; i < method.getParameters().length; i++) {
                Parameter parameter = parameters[i];
                if (parameter.isAnnotationPresent(AutoInject.class)) {
                    hasAnnotation = true;
                    Object bean = findCompatibleBean(parameter.getType());
                    if (Objects.isNull(bean))
                        throw new ReflectionException("Not found bean with type " + parameter.getType());
                    parametersForCall[i] = bean;
                }
            }
            if (hasAnnotation)
                method.invoke(object, parametersForCall);
        }
    }

    private static Object findCompatibleBean(Class<?> paramClass) {
        if (classObjectMap.containsKey(paramClass)) {
            return classObjectMap.get(paramClass);
        }
        for (Class<?> beansClazz : classObjectMap.keySet()) {
            if (paramClass.isAssignableFrom(beansClazz)) return classObjectMap.get(beansClazz);
        }
        return null;
    }

    private static void addNewBean(Class<?> clazz, Object bean) {
        if (classObjectMap.containsKey(clazz))
            throw new ReflectionException("Collision exception. Bean of this type already exists: " + clazz.getSimpleName());
        classObjectMap.put(clazz, bean);
    }
}
