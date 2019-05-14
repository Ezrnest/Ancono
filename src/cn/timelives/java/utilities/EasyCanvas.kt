@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.timelives.java.utilities

import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.geometry.Point2D
import javafx.geometry.VPos
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.ArcType
import javafx.scene.shape.FillRule
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.text.Font
import javafx.scene.text.FontSmoothingType
import javafx.scene.text.TextAlignment
import javafx.scene.transform.Affine
import javafx.scene.transform.Transform
import javafx.stage.Stage
import java.awt.image.BufferedImage
import java.lang.Double.min
import java.lang.IllegalArgumentException
import java.util.function.Consumer
import kotlin.math.abs


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

    fun draw(f: Consumer<GraphicsContext>) {
        Platform.runLater { f.accept(graphics) }
    }

    fun draw(f: (GraphicsContext) -> Unit) {
        Platform.runLater { f(graphics) }
    }

    fun getBackingCanvas(): Canvas {
        return canvas
    }

    fun getBackingStage(): Stage {
        return stage
    }

    /**
     * Clears the canvas.
     */
    fun clear() {
        Platform.runLater {
            graphics.clearRect(0.0, 0.0, canvas.width, canvas.height)
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

    fun drawLine(p1: Point2D, p2: Point2D) {
        drawLine(p1.x, p1.y, p2.x, p2.y)
    }


    fun drawLine(p1: Point2D, p2: Point2D, c: Color) {
        drawLine(p1.x, p1.y, p2.x, p2.y, c)
    }

    fun drawLine(p1: Point2D, p2: Point2D, color: java.awt.Color) {
        drawLine(p1, p2, Color.rgb(color.red, color.green, color.blue, color.alpha / 255.0))
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

    /**
     * Adds a key-pressed handler, this handler will be invoked at the last of event handler chain.
     */
    fun addKeyPressedHandler(handler: EventHandler<in KeyEvent>) {
        val origin = canvas.onKeyPressed
        if (origin == null) {
            canvas.onKeyPressed = handler
        } else {
            canvas.onKeyPressed = EventHandler { event ->
                origin.handle(event)
                handler.handle(event)
            }
        }
    }

    /**
     * Adds a key-pressed handler, this handler will be invoked at the last of event handler chain.
     */
    fun addKeyPressedHandler(handler: (KeyEvent) -> Unit) {
        val origin = canvas.onKeyPressed
        if (origin == null) {
            canvas.onKeyPressed = EventHandler(handler)
        } else {
            canvas.onKeyPressed = EventHandler { event ->
                origin.handle(event)
                handler(event)
            }
        }
    }

    /**
     * Adds a mouse-clicked handler, this handler will be invoked at the last of event handler chain.
     */
    fun addMouseClickedHandler(handler: EventHandler<in MouseEvent>) {
        val origin = canvas.onMouseClicked
        if (origin == null) {
            canvas.onMouseClicked = handler
        } else {
            canvas.onMouseClicked = EventHandler { event ->
                origin.handle(event)
                handler.handle(event)
            }
        }
    }

    /**
     * Adds a mouse-clicked handler, this handler will be invoked at the last of event handler chain.
     */
    fun addMouseClickedHandler(handler: (MouseEvent) -> Unit) {
        val origin = canvas.onMouseClicked
        if (origin == null) {
            canvas.onMouseClicked = EventHandler(handler)
        } else {
            canvas.onMouseClicked = EventHandler { event ->
                origin.handle(event)
                handler(event)
            }
        }
    }

}

/**
 * Provides a zooming plugin for the easy canvas, which can provide proper zooming for the drawing and also user-friendly
 * view control.The drawing function must be set through method [setDrawer], which
 * will be called when necessary. An affine transformation, which can be used to locate the position of points in the canvas,
 * is provided for the drawer.
 *
 * **Coordinate systems**
 *
 * There are two coordinate systems involved in the zooming operation, one is the real coordinate system, which
 * is used by the drawer and whose direction of y-axis is upward,
 * and the other one is the coordinate system of the screen, which is used by the canvas(or graphics) and whose direction
 * of y-axis is downward. The affine transformation provided for drawer is used to transform a point in the real
 * coordinate system to the corresponding point the screen coordinate system.
 *
 * **Center point and scale**
 *
 * Other than the matrix and translation of the affine transformation, it is much easier to used the center point and
 * scale to describe the zooming. The center point is the point in the real coordinate system which will be shown exactly
 * at the center of the canvas on the screen. The scale determines the size of shapes shown on the screen. For example,
 * we have a line with a length of 1 in the real coordinate system and the scale of 200, then it is shown as 200 pixels
 * in length.
 *
 * **User interaction**
 * The user can use direction key to move the view and key '+' or '-' to zoom in or out,
 * which will result in changes of the affine transformation and redrawing. The percentage of moving and zooming can
 * be set via [zoomingFactor] and [movingFactor].
 *
 *
 */
class ZoomingPlugin(val canvas: EasyCanvas, centerX: Double = 0.0, centerY: Double = 0.0, scale: Double = -1.0) {
    private var aff: Affine
    private var drawer: (EasyCanvas, Affine) -> Unit = { _, _ -> Unit }
    /**
     * The scale of the zooming, which is equal to the determinant of the affine transformation.
     */
    var scale: Double
        get() = abs(aff.determinant())
        set(value) {
            require(value > 0)
            val origin = scale
            val s = value / origin
            val centerX = canvas.width / 2
            val centerY = canvas.height / 2
            aff.prependScale(s, s, centerX, centerY)
        }
    /**
     * Describes how much the scale should be multiplied(divided) when user wants to zoom in(out).
     */
    var zoomingFactor: Double = 1.5
        set(value) {
            if (value <= 1) {
                throw IllegalArgumentException("Factor must be bigger than 1!")
            }
            field = value
        }
    /**
     * Describes the percentage of translation to be add when zooming in.
     */
    var movingFactor: Double = 0.1
        set(value) {
            if (value <= 0 || value >= 1) {
                throw IllegalArgumentException("Factor must be in (0,1)")
            }
            field = value
        }

    init {
        val width = canvas.width / 2
        val height = canvas.height / 2
        val k = if (scale < 0) {
            min(width / 5, height / 5)
        } else {
            scale
        }
        val tx = -k * centerX + width
        val ty = -k * centerY + height
        aff = Transform.affine(k, 0.0, 0.0, -k, tx, ty)
        registerListener()
    }

    private fun registerListener() {

        canvas.addKeyPressedHandler(EventHandler { event ->
            when (event.code) {
                KeyCode.EQUALS, KeyCode.PLUS -> zoomIn()
                KeyCode.MINUS, KeyCode.UNDERSCORE -> zoomOut()
                KeyCode.UP -> moveToward(0, 1)
                KeyCode.DOWN -> moveToward(0, -1)
                KeyCode.RIGHT -> moveToward(-1, 0)
                KeyCode.LEFT -> moveToward(1, 0)
                else -> {
                }
            }
        })

    }

    /**
     * Zoom in without changing the center point.
     */
    fun zoomIn() {
        val centerX = canvas.width / 2
        val centerY = canvas.height / 2
        aff.prependScale(zoomingFactor, zoomingFactor, centerX, centerY)
        update()
    }

    fun zoomOut() {
        val centerX = canvas.width / 2
        val centerY = canvas.height / 2
        val factor = 1 / zoomingFactor
        aff.prependScale(factor, factor, centerX, centerY)
        update()
    }

    fun moveToward(x: Int, y: Int) {
        val tx = x * canvas.width * movingFactor
        val ty = y * canvas.height * movingFactor
        aff.prependTranslation(tx, ty)
        update()
    }

    fun update() {
        canvas.clear()
        drawer(canvas, aff)
    }

    fun setDrawer(f: (EasyCanvas, Affine) -> Unit) {
        drawer = f
        update()
    }

    /**
     * Translate to make the given point shown at the center.
     */
    fun setCenter(centerX: Double, centerY: Double) {
        val re = aff.transform(centerX, centerY)
        aff.prependTranslation(re.x, re.y)
        update()
    }

    /**
     * Translates the figure by (tx,ty) in the real coordinate system.
     */
    fun translateReal(tx: Double, ty: Double) {
        aff.appendTranslation(tx, ty)
        update()
    }

    /**
     * Translates the figure by (tx,ty) in the screen coordinate system.
     */
    fun translateView(tx: Double, ty: Double) {
        aff.prependTranslation(tx, ty)
        update()
    }

    /**
     * Sets the affine.
     */
    fun setAffine(affine: Affine) {
        aff = affine
        update()
    }

    /**
     * Gets a copy of the affine.
     */
    fun getAffine(): Affine {
        return aff.clone()
    }


}


//fun main(args: Array<String>) {
//    val canvas = EasyCanvas(500, 500)
//    val zooming = ZoomingPlugin(canvas)
//    zooming.setDrawer { can, aff ->
//
//        can.drawLine(aff.transform(0.0, 0.0), aff.transform(10.0, 10.0))
//    }
////    canvas.drawLine(0.0,0.0,10.0,10.0)
//    canvas.show()
//}