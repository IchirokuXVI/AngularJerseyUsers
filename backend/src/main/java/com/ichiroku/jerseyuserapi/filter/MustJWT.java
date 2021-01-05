package com.ichiroku.jerseyuserapi.filter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.ws.rs.NameBinding;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface MustJWT {
}
