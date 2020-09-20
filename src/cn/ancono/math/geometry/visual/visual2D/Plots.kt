package cn.ancono.math.geometry.visual.visual2D

import cn.ancono.utilities.EasyCanvas
import cn.ancono.utilities.ZoomingPlugin
import java.awt.Color
import java.awt.geom.Rectangle2D
import java.util.function.DoubleUnaryOperator


/**
 * A wrapper class for plotting and showing.
 *
 *
 * Created by liyicheng at 2020-06-02 19:40
 */
class Plotting(width: Int = 500, height: Int = 500) {

    val canvas = EasyCanvas(width, height)
    val planeDrawer = PlaneDrawer()
    val zooming = ZoomingPlugin(canvas, width.toDouble() / 2, height.toDouble() / 2, scale = 1.0, filpY = false)
    val defaultColor = Color.RED


    var width = width
        set(value) {
            canvas.width = value.toDouble()
            field = value
            updateDrawing()
        }

    var height = height
        set(value) {
            canvas.height = value.toDouble()
            field = value
            updateDrawing()
        }

    var rect: Rectangle2D.Double = Rectangle2D.Double(0.0, 0.0, 1.0, 1.0)
        set(value) {
            field = value
            updateDrawing()
        }

    private fun updateDrawing() {
        val image = planeDrawer.draw(rect, width, height)
        zooming.setDrawer { it.drawImage(image, 0, 0) }
    }

    fun setArea(x1: Double, x2: Double, y1: Double, y2: Double) {
        val width = x2 - x1
        val height = y2 - y1
        rect = Rectangle2D.Double(x1, y1, width, height)
    }

    fun plotCurve(curve: SubstitutableCurve, c: Color = this.defaultColor) {
        planeDrawer.addCurve(c, curve)
        updateDrawing()
    }

    fun plotFunction(f: DoubleUnaryOperator, c: Color = this.defaultColor) {
        planeDrawer.addCurve(c) { x, y -> y - f.applyAsDouble(x) }
        updateDrawing()
    }

    fun show() {
        canvas.show()
    }


}
