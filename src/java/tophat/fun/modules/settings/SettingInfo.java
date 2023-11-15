package tophat.fun.modules.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SettingInfo {
    String name();
    boolean booleanDefault() default false;
    String stringDefault() default "";
    int intDefault() default 0;
}
