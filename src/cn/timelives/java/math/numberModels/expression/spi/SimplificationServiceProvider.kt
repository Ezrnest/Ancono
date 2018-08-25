package cn.timelives.java.math.numberModels.expression.spi

import cn.timelives.java.math.numberModels.expression.SimpleStrategy
import cn.timelives.java.math.numberModels.expression.simplification.SimplificationKotlin
import cn.timelives.java.math.numberModels.expression.simplification.SimplificationStrategy

class SimplificationServiceProvider : SimplificationService {

    override fun getStrategies(): List<SimplificationStrategy> {
        val list = mutableListOf<SimpleStrategy>()
        SimplificationKotlin.addDefault(list)
        return list
    }

    override fun getTags(): List<String> {
        return emptyList()
    }

    override fun getProperties(): Map<String, String> {
        return emptyMap()
    }
}
