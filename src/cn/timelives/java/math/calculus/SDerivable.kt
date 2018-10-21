package cn.timelives.java.math.calculus

import cn.timelives.java.math.function.SVFunction

interface SDerivable<T, S : SVFunction<T>> : Derivable<T, T, S>, SVFunction<T> {
}