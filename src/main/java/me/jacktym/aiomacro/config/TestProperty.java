package me.jacktym.aiomacro.config;

import kotlin.annotation.AnnotationTarget;
import kotlin.annotation.Retention;
import kotlin.annotation.Target;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Retention()
@Target(
        allowedTargets = {AnnotationTarget.FIELD, AnnotationTarget.FUNCTION}
)
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.FIELD, ElementType.METHOD})
public @interface TestProperty {
    //PropertyType type();
    String type();

    String name();

    String i18nName() default "";

    String category();

    String i18nCategory() default "";

    String subcategory() default "";

    String i18nSubcategory() default "";

    String description() default "";

    int min() default 0;

    int max() default 0;

    float minF() default 0.0F;

    float maxF() default 0.0F;

    int decimalPlaces() default 1;

    int increment() default 1;

    String[] options() default {};

    boolean allowAlpha() default true;

    String placeholder() default "";

    boolean protectedText() default false;

    boolean triggerActionOnInitialization() default true;

    boolean hidden() default false;

    String[] searchTags() default {};
}