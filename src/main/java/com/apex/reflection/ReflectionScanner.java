package com.apex.reflection;

import com.apex.core.Constants;
import com.apex.exception.ReflectionException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class ReflectionScanner {
    protected static class BeanKey {
        private String name;
        private Class<?> clazz;

        public BeanKey(String name, Class<?> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public boolean equals(Object object) {
            if (object == null || getClass() != object.getClass()) return false;
            BeanKey beanKey = (BeanKey) object;
            return Objects.equals(name, beanKey.name) && Objects.equals(clazz, beanKey.clazz);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, clazz);
        }
    }

    private static final Map<BeanKey, Object> classObjectMap = new HashMap<>();

    public static void autocreationResolver() {
        List<Class<?>> classes = ClassFinder.find(Constants.PACKAGE_FOR_SCAN);
        for (Class<?> clazz : classes) {
            Object bean;
            if (clazz.isAnnotationPresent(AutoCreation.class)) {
                try {
                    bean = clazz.getDeclaredConstructor().newInstance();
                    AutoCreation annotation = clazz.getAnnotation(AutoCreation.class);
                    registerBean(annotation.name(), clazz, bean);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                         NoSuchMethodException e) {
                    throw
                            new ReflectionException("Unable to create obj of class " + clazz.getSimpleName() +
                                    ". Make sure it's contains no args constructor");
                }
            }
        }
    }

    public static void registerBean(String name, Class<?> clazz, Object bean) {
        if (name.isBlank()) {
            name = clazz.getSimpleName();
        }

        addNewBean(name, clazz, bean);

        for (Class<?> iface : clazz.getInterfaces()) {
            addNewBean(name, iface, bean);
        }

        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            addNewBean(name, superclass, bean);
        }
    }

    public static void wireContext() throws InvocationTargetException, IllegalAccessException {
        for (Object bean : new HashSet<>(classObjectMap.values())) {
            fieldsInjectionResolver(bean);
        }

        for (Object bean : new HashSet<>(classObjectMap.values())) {
            parametersInjectionResolver(bean);
        }
    }

    public static void fieldsInjectionResolver(Object object) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(AutoInject.class)) continue;
            field.setAccessible(true);
            AutoInject annotation = field.getAnnotation(AutoInject.class);
            Object bean = findCompatibleBean(annotation.name(), field.getType());
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
                    AutoInject annotation = parameter.getAnnotation(AutoInject.class);
                    Object bean = findCompatibleBean(annotation.name(), parameter.getType());
                    parametersForCall[i] = bean;
                }
            }
            if (hasAnnotation)
                method.invoke(object, parametersForCall);
        }
    }

    public static Object findCompatibleBean(String name, Class<?> paramClass) {
        Object found = findBeanByNameAndClass(name, paramClass);
        if (found != null) return found;
        if (!name.isBlank())
            throw new ReflectionException("Bean of type " + paramClass.getSimpleName() + " with name " + name + " not found");

        found = findAssignableBeanByClass(paramClass);
        if (found == null) throw new ReflectionException("Not found bean with type " + paramClass.getSimpleName());
        return found;
    }

    public static Object findBeanByNameAndClass(String name, Class<?> paramClass) {
        if (name.isBlank()) name = paramClass.getSimpleName();
        BeanKey k = new BeanKey(name, paramClass);
        if (classObjectMap.containsKey(k)) {
            return classObjectMap.get(k);
        }
        return null;
    }

    public static Object findAssignableBeanByClass(Class<?> paramClass) {
        BeanKey assignable = null;
        for (BeanKey eK : classObjectMap.keySet()) {
            if (paramClass.isAssignableFrom(eK.getClazz())) {
                if (assignable == null) {
                    assignable = eK;
                } else {
                    if (Objects.equals(classObjectMap.get(assignable), classObjectMap.get(eK))) continue;
                    throw new ReflectionException(
                            String.format("Found more than 1 candidates to become injected into field with type %s. It's %s and %s ",
                                    paramClass.getSimpleName(), assignable.getClazz().getSimpleName(), eK.getClazz().getSimpleName()));
                }
            }
        }
        if (assignable == null) return null;
        return classObjectMap.get(assignable);
    }

    private static void addNewBean(String name, Class<?> clazz, Object bean) {
        BeanKey k = new BeanKey(name, clazz);
        if (classObjectMap.containsKey(k))
            throw new ReflectionException(
                    "Collision exception. Bean of this type and name already exists: " + name + " - name " + clazz.getSimpleName() + " - class"
            );
        classObjectMap.put(k, bean);
    }
}
