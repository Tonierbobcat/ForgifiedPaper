package com.loficostudios.forgified.paper.gui.unnamed;

import io.netty.util.internal.UnstableApi;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiStatus.Experimental
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Configurable {
    String name() default "";
    boolean mutable() default true;
    boolean nullable() default true;
}
