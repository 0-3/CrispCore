package network.reborn.core.Module.Games.UltraHardcoreReddit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ethan on 12/16/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scenario {

    String name() default "ScenarioName";

    String enableMethod() default "onEnable";

    String disableMethod() default "onDisable";

    String getMenuItem() default "getMenuItem";

}
