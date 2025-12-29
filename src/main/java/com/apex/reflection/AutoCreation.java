package com.apex.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
// для автоматического создания надо чтобы у класса был конструктор без аргументов
public @interface AutoCreation {
    String name() default "";
}