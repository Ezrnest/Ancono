@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.timelives.java.utilities

import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.VPos
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.ArcType
import javafx.scene.shape.FillRule
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.text.Font
import javafx.scene.text.FontSmoothingType
import javafx.scene.text.TextAlignment
import javafx.stage.Stage
import java.awt.Point
import java.awt.image.BufferedImage
import java.util.function.Consumer


/*
 * Created at 2018/12/3 21:46
 * @author  liyicheng
 */
class EasyCanvas(val width: Double, val height: Double, title: String) {
    constructor(width: Double, height: Double) : this(width, height, "Canvas")
    constructor(width: Int, height: Int, title: String = "Canvas") : this(width.toDouble(), height.toDouble(), title)

    private val stage: Stage
    private val canvas: Canvas
    private val graphics: GraphicsContext

    init {
        val pair = EasyDrawingWindow.createCanvas(width, height, title)
        stage = pair.first
        canvas = pair.second
        graphics = canvas.graphicsContext2D
    }

    fun show() {
        Platform.runLater { stage.show() }
    }

    fun close() {
        Platform.runLater { stage.close() }
    }

    fun isShowing(): Boolean {
        return stage.isShowing
    }

    fun draw(f : Consumer<Canvas>){
        Platform.runLater { f.accept(canvas) }
    }

    fun getBackingCanvas():Canvas{
        return canvas
    }

    fun getBackingStage() : Stage{
        return  stage
    }

    /**
     * Clears the canvas.
     */
    fun clear(){
        Platform.runLater {
            graphics.clearRect(0.0,0.0,canvas.width,canvas.height)
        }
    }

    /** @see GraphicsContext.setFill */
    fun setFill(p: Paint) {
        graphics.fill = p
    }

    /** @see GraphicsContext.setStroke */
    fun setStroke(p: Paint) {
        graphics.stroke = p
    }

    /** @see GraphicsContext.setLineWidth */
    fun setLineWidth(lw: Double) {
        graphics.lineWidth = lw
    }

    /** @see GraphicsContext.setLineCap */
    fun setLineCap(cap: StrokeLineCap) {
        graphics.lineCap = cap
    }

    /** @see GraphicsContext.setLineJoin */
    fun setLineJoin(join: StrokeLineJoin) {
        graphics.lineJoin = join
    }

    /** @see GraphicsContext.setMiterLimit */
    fun setMiterLimit(ml: Double) {
        graphics.miterLimit = ml
    }

    /** @see GraphicsContext.setLineDashes */
    fun setLineDashes(vararg dashes: Double) {
        graphics.setLineDashes(*dashes)
    }

    /** @see GraphicsContext.setLineDashOffset */
    fun setLineDashOffset(dashOffset: Double) {
        graphics.lineDashOffset = dashOffset
    }

    /** @see GraphicsContext.setFont */
    fun setFont(f: Font) {
        graphics.font = f
    }

    /** @see GraphicsContext.setFontSmoothingType */
    fun setFontSmoothingType(fontsmoothing: FontSmoothingType) {
        graphics.fontSmoothingType = fontsmoothing
    }

    /** @see GraphicsContext.setTextAlign */
    fun setTextAlign(align: TextAlignment) {
        graphics.textAlign = align
    }

    /** @see GraphicsContext.setTextBaseline */
    fun setTextBaseline(baseline: VPos) {
        graphics.textBaseline = baseline
    }

    /** @see GraphicsContext.setFillRule */
    fun setFillRule(fillRule: FillRule) {
        graphics.fillRule = fillRule
    }

    /** @see GraphicsContext.fillText */
    fun fillText(text: String, x: Double, y: Double) {
        Platform.runLater {
            graphics.fillText(text, x, y)
        }
    }

    /** @see GraphicsContext.strokeText */
    fun strokeText(text: String, x: Double, y: Double) {
        Platform.runLater {
            graphics.strokeText(text, x, y)
        }
    }

    /** @see GraphicsContext.fillText */
    fun fillText(text: String, x: Double, y: Double, maxWidth: Double) {
        Platform.runLater {
            graphics.fillText(text, x, y, maxWidth)
        }
    }

    /** @see GraphicsContext.strokeText */
    fun strokeText(text: String, x: Double, y: Double, maxWidth: Double) {
        Platform.runLater {
            graphics.strokeText(text, x, y, maxWidth)
        }
    }

    /** @see GraphicsContext.clearRect */
    fun clearRect(x: Double, y: Double, w: Double, h: Double) {
        Platform.runLater {
            graphics.clearRect(x, y, w, h)
        }
    }

    /** @see GraphicsContext.fillRect */
    fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        Platform.runLater {
            graphics.fillRect(x, y, w, h)
        }
    }

    /** @see GraphicsContext.strokeRect */
    fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        Platform.runLater {
            graphics.strokeRect(x, y, w, h)
        }
    }

    /** @see GraphicsContext.fillOval */
    fun fillOval(x: Double, y: Double, w: Double, h: Double) {
        Platform.runLater {
            graphics.fillOval(x, y, w, h)
        }
    }

    /** @see GraphicsContext.strokeOval */
    fun strokeOval(x: Double, y: Double, w: Double, h: Double) {
        Platform.runLater {
            graphics.strokeOval(x, y, w, h)
        }
    }

    /** @see GraphicsContext.fillArc */
    fun fillArc(x: Double, y: Double, w: Double, h: Double, startAngle: Double, arcExtent: Double, closure: ArcType) {
        Platform.runLater {
            graphics.fillArc(x, y, w, h, startAngle, arcExtent, closure)
        }
    }

    /** @see GraphicsContext.strokeArc */
    fun strokeArc(x: Double, y: Double, w: Double, h: Double, startAngle: Double, arcExtent: Double, closure: ArcType) {
        Platform.runLater {
            graphics.strokeArc(x, y, w, h, startAngle, arcExtent, closure)
        }
    }

    /** @see GraphicsContext.fillRoundRect */
    fun fillRoundRect(x: Double, y: Double, w: Double, h: Double, arcWidth: Double, arcHeight: Double) {
        Platform.runLater {
            graphics.fillRoundRect(x, y, w, h, arcWidth, arcHeight)
        }
    }

    /** @see GraphicsContext.strokeRoundRect */
    fun strokeRoundRect(x: Double, y: Double, w: Double, h: Double, arcWidth: Double, arcHeight: Double) {
        Platform.runLater {
            graphics.strokeRoundRect(x, y, w, h, arcWidth, arcHeight)
        }
    }

    /** @see GraphicsContext.strokeLine */
    fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        Platform.runLater {
            graphics.strokeLine(x1, y1, x2, y2)
        }
    }

    fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
        Platform.runLater {
            graphics.strokeLine(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
        }
    }
    /** @see GraphicsContext.strokeLine */
    fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double, c: Color) {
        Platform.runLater {
            val t = graphics.stroke
            graphics.stroke = c
            graphics.strokeLine(x1, y1, x2, y2)
            graphics.stroke = t
        }
    }

    fun drawLine(p1: Point, p2: Point) {
        drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY())
    }


    fun drawLine(p1: Point, p2: Point, c: Color) {
        drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY(), c)
    }

    fun drawLine(p1:Point,p2 : Point, color : java.awt.Color){
        drawLine(p1,p2, Color.rgb(color.red,color.green,color.blue,color.alpha/255.0))
    }

    /** @see GraphicsContext.fillPolygon */
    fun fillPolygon(xPoints: DoubleArray, yPoints: DoubleArray, nPoints: Int) {
        Platform.runLater {
            graphics.fillPolygon(xPoints, yPoints, nPoints)
        }
    }

    /** @see GraphicsContext.strokePolygon */
    fun strokePolygon(xPoints: DoubleArray, yPoints: DoubleArray, nPoints: Int) {
        Platform.runLater {
            graphics.strokePolygon(xPoints, yPoints, nPoints)
        }
    }

    /** @see GraphicsContext.strokePolyline */
    fun strokePolyline(xPoints: DoubleArray, yPoints: DoubleArray, nPoints: Int) {
        Platform.runLater {
            graphics.strokePolyline(xPoints, yPoints, nPoints)
        }
    }

    /** @see GraphicsContext.drawImage */
    fun drawImage(img: Image, x: Double, y: Double) {
        Platform.runLater {
            graphics.drawImage(img, x, y)
        }
    }

    /** @see GraphicsContext.drawImage */
    fun drawImage(bufferedImage: BufferedImage, x: Int, y: Int) {
        drawImage(SwingFXUtils.toFXImage(bufferedImage, null), x.toDouble(), y.toDouble())
    }

    /** @see GraphicsContext.drawImage */
    fun drawImage(img: Image, x: Double, y: Double, w: Double, h: Double) {
        Platform.runLater {
            graphics.drawImage(img, x, y, w, h)
        }
    }

    /** @see GraphicsContext.drawImage */
    fun drawImage(img: Image, sx: Double, sy: Double, sw: Double, sh: Double, dx: Double, dy: Double, dw: Double, dh: Double) {
        Platform.runLater {
            graphics.drawImage(img, sx, sy, sw, sh, dx, dy, dw, dh)
        }
    }

}

//fun main(args: Array<String>) {
//    val canvas = EasyCanvas(500,500)
//    canvas.drawLine(0.0,0.0,10.0,10.0)
//    canvas.show()
//}