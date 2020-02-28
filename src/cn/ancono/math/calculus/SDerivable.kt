package cn.ancono.math.calculus

import cn.ancono.math.function.SVFunction

interface SDerivable<T, S : SVFunction<T>> : Derivable<T, T, S>, SVFunction<T> {
}