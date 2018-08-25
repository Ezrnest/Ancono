package cn.timelives.java.math.numberModels.expression.spi;

import cn.timelives.java.math.numberModels.expression.simplification.SimplificationStrategy;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * Describes a simplification service.
 */
public interface SimplificationService {

    /**
     * Gets simplification strategies that the service provides.
     */
    @NotNull
    List<SimplificationStrategy> getStrategies();

    /**
     * Gets the tags to set on the calculator.
     */
    @NotNull
    List<String> getTags();

    /**
     * Gets the properties that should be set on the calculator.
     */
    @NotNull
    Map<String,String> getProperties();
}
