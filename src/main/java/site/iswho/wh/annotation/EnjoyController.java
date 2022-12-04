package site.iswho.wh.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//作用范围:用在接口或类上
@Target({ElementType.TYPE})
// 注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Retention(RetentionPolicy.RUNTIME)
//该注解将被包含在javadoc中
@Documented
//  @Inherited：说明子类可以继承父类中的该注解
public @interface EnjoyController {
  String value() default "";
}
