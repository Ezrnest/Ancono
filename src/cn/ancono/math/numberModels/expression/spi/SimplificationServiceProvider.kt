package cn.ancono.math.numberModels.expression.spi

import cn.ancono.math.numberModels.expression.SimpleStrategy
import cn.ancono.math.numberModels.expression.simplification.SimplificationKotlin
import cn.ancono.math.numberModels.expression.simplification.SimplificationStrategy

class SimplificationServiceProvider : SimplificationService {

    override fun getStrategies(): List<SimplificationStrategy> {
        val list = mutableListOf<SimpleStrategy>()
        SimplificationKotlin.addDefault(list)
//        println("Strategies loaded!")
        return list
    }

    override fun getTags(): List<String> {
        return emptyList()
    }

    override fun getProperties(): Map<String, String> {
        return emptyMap()
    }
}
