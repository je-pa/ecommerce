package com.ecommerce.global.security.annotation;

import com.ecommerce.domain.member.type.Role.Authority;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.annotation.Secured;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Secured(Authority.GENERAL)
public @interface RoleGeneral {

}
