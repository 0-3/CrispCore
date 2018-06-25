package network.reborn.core.Module.Games.UltraHardcoreReddit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ethan on 1/13/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UHCOption {

    String name() default "OptionName";

    String enableMethod() default "onEnable";

    String disableMethod() default "onDisable";

    String itemMethod() default "getMenuItem";

    //String startMethod() default "onStart";

}
