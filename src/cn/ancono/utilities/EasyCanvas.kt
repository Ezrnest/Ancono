@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package cn.ancono.utilities

import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.geometry.Point2D
import javafx.geometry.VPos
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
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
    var transform: Affine
        set(value) {
            if (Platform.isFxApplicationThread()) {
                graphics.transform = value
            } else {
                Platform.runLater { graphics.transform = value }
            }
        }
        /**
         * Gets a clone of the current transform.
         */
        get() = graphics.transform.clone()


    init {
        val pair = EasyDrawingWindow.createCanvas(width, height, title)
        stage = pair.first
        canvas = pair.second
        graphics = canvas.graphicsContext2D
    }

    fun show() {
        if (Platform.isFxApplicationThread()) {
            stage.show()
        } else {
            Platform.runLater { stage.show() }
        }
    }

    fun close() {
        if (Platform.isFxApplicationThread()) {
            stage.close()
        } else {
            Platform.runLater { stage.close() }
        }
    }

    fun isShowing(): Boolean {
        return stage.isShowing
    }

    fun draw(f: Consumer<GraphicsContext>) {
        if (Platform.isFxApplicationThread()) {
            f.accept(graphics)
        } else {
            Platform.runLater { f.accept(graphics) }
        }
    }

    fun draw(f: (GraphicsContext) -> Unit) {
        if (Platform.isFxApplicationThread()) {
            f(graphics)
        } else {
            Platform.runLater { f(graphics) }
        }
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
        if (Platform.isFxApplicationThread()) {
            graphics.clearRect(0.0, 0.0, canvas.width, canvas.height)
        } else {
            Platform.runLater { graphics.clearRect(0.0, 0.0, canvas.width, canvas.height) }
        }
    }

    /** @see GraphicsContext.setFill */
    fun setFill(p: Paint) {
        if (Platform.isFxApplicationThread()) {
            graphics.fill = p
        } else {
            Platform.runLater { graphics.fill = p }
        }
    }

    /** @see GraphicsContext.setStroke */
    fun setStroke(p: Paint) {
        if (Platform.isFxApplicationThread()) {
            graphics.stroke = p
        } else {
            Platform.runLater { graphics.stroke = p }
        }
    }

    /** @see GraphicsContext.setLineWidth */
    fun setLineWidth(lw: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.lineWidth = lw
        } else {
            Platform.runLater { graphics.lineWidth = lw }
        }
    }

    /** @see GraphicsContext.setLineCap */
    fun setLineCap(cap: StrokeLineCap) {
        if (Platform.isFxApplicationThread()) {
            graphics.lineCap = cap
        } else {
            Platform.runLater { graphics.lineCap = cap }
        }
    }

    /** @see GraphicsContext.setLineJoin */
    fun setLineJoin(join: StrokeLineJoin) {
        if (Platform.isFxApplicationThread()) {
            graphics.lineJoin = join
        } else {
            Platform.runLater { graphics.lineJoin = join }
        }
    }

    /** @see GraphicsContext.setMiterLimit */
    fun setMiterLimit(ml: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.miterLimit = ml
        } else {
            Platform.runLater { graphics.miterLimit = ml }
        }
    }

    /** @see GraphicsContext.setLineDashes */
    fun setLineDashes(vararg dashes: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.setLineDashes(*dashes)
        } else {
            Platform.runLater { graphics.setLineDashes(*dashes) }
        }
    }

    /** @see GraphicsContext.setLineDashOffset */
    fun setLineDashOffset(dashOffset: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.lineDashOffset = dashOffset
        } else {
            Platform.runLater { graphics.lineDashOffset = dashOffset }
        }
    }

    /** @see GraphicsContext.setFont */
    fun setFont(f: Font) {
        if (Platform.isFxApplicationThread()) {
            graphics.font = f
        } else {
            Platform.runLater { graphics.font = f }
        }
    }

    /** @see GraphicsContext.setFontSmoothingType */
    fun setFontSmoothingType(fontsmoothing: FontSmoothingType) {
        if (Platform.isFxApplicationThread()) {
            graphics.fontSmoothingType = fontsmoothing
        } else {
            Platform.runLater { graphics.fontSmoothingType = fontsmoothing }
        }
    }

    /** @see GraphicsContext.setTextAlign */
    fun setTextAlign(align: TextAlignment) {
        if (Platform.isFxApplicationThread()) {
            graphics.textAlign = align
        } else {
            Platform.runLater { graphics.textAlign = align }
        }
    }

    /** @see GraphicsContext.setTextBaseline */
    fun setTextBaseline(baseline: VPos) {
        if (Platform.isFxApplicationThread()) {
            graphics.textBaseline = baseline
        } else {
            Platform.runLater { graphics.textBaseline = baseline }
        }
    }

    /** @see GraphicsContext.setFillRule */
    fun setFillRule(fillRule: FillRule) {
        if (Platform.isFxApplicationThread()) {
            graphics.fillRule = fillRule
        } else {
            Platform.runLater { graphics.fillRule = fillRule }
        }
    }

    /** @see GraphicsContext.fillText */
    fun fillText(text: String, x: Double, y: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.fillText(text, x, y)
        } else {
            Platform.runLater { graphics.fillText(text, x, y) }
        }
    }

    /** @see GraphicsContext.strokeText */
    fun strokeText(text: String, x: Double, y: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.strokeText(text, x, y)
        } else {
            Platform.runLater { graphics.strokeText(text, x, y) }
        }
    }

    /** @see GraphicsContext.fillText */
    fun fillText(text: String, x: Double, y: Double, maxWidth: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.fillText(text, x, y, maxWidth)
        } else {
            Platform.runLater { graphics.fillText(text, x, y, maxWidth) }
        }
    }

    /** @see GraphicsContext.strokeText */
    fun strokeText(text: String, x: Double, y: Double, maxWidth: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.strokeText(text, x, y, maxWidth)
        } else {
            Platform.runLater { graphics.strokeText(text, x, y, maxWidth) }
        }
    }

    /** @see GraphicsContext.clearRect */
    fun clearRect(x: Double, y: Double, w: Double, h: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.clearRect(x, y, w, h)
        } else {
            Platform.runLater { graphics.clearRect(x, y, w, h) }
        }
    }

    /** @see GraphicsContext.fillRect */
    fun fillRect(x: Double, y: Double, w: Double, h: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.fillRect(x, y, w, h)
        } else {
            Platform.runLater { graphics.fillRect(x, y, w, h) }
        }
    }

    /** @see GraphicsContext.strokeRect */
    fun strokeRect(x: Double, y: Double, w: Double, h: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.strokeRect(x, y, w, h)
        } else {
            Platform.runLater { graphics.strokeRect(x, y, w, h) }
        }
    }

    /** @see GraphicsContext.fillOval */
    fun fillOval(x: Double, y: Double, w: Double, h: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.fillOval(x, y, w, h)
        } else {
            Platform.runLater { graphics.fillOval(x, y, w, h) }
        }
    }

    /** @see GraphicsContext.strokeOval */
    fun strokeOval(x: Double, y: Double, w: Double, h: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.strokeOval(x, y, w, h)
        } else {
            Platform.runLater { graphics.strokeOval(x, y, w, h) }
        }
    }

    /** @see GraphicsContext.fillArc */
    fun fillArc(x: Double, y: Double, w: Double, h: Double, startAngle: Double, arcExtent: Double, closure: ArcType) {
        if (Platform.isFxApplicationThread()) {
            graphics.fillArc(x, y, w, h, startAngle, arcExtent, closure)
        } else {
            Platform.runLater { graphics.fillArc(x, y, w, h, startAngle, arcExtent, closure) }
        }
    }

    /** @see GraphicsContext.strokeArc */
    fun strokeArc(x: Double, y: Double, w: Double, h: Double, startAngle: Double, arcExtent: Double, closure: ArcType) {
        if (Platform.isFxApplicationThread()) {
            graphics.strokeArc(x, y, w, h, startAngle, arcExtent, closure)
        } else {
            Platform.runLater { graphics.strokeArc(x, y, w, h, startAngle, arcExtent, closure) }
        }
    }

    /** @see GraphicsContext.fillRoundRect */
    fun fillRoundRect(x: Double, y: Double, w: Double, h: Double, arcWidth: Double, arcHeight: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.fillRoundRect(x, y, w, h, arcWidth, arcHeight)
        } else {
            Platform.runLater { graphics.fillRoundRect(x, y, w, h, arcWidth, arcHeight) }
        }
    }

    /** @see GraphicsContext.strokeRoundRect */
    fun strokeRoundRect(x: Double, y: Double, w: Double, h: Double, arcWidth: Double, arcHeight: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.strokeRoundRect(x, y, w, h, arcWidth, arcHeight)
        } else {
            Platform.runLater { graphics.strokeRoundRect(x, y, w, h, arcWidth, arcHeight) }
        }
    }

    /** @see GraphicsContext.strokeLine */
    fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.strokeLine(x1, y1, x2, y2)
        } else {
            Platform.runLater { graphics.strokeLine(x1, y1, x2, y2) }
        }
    }

    fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
        if (Platform.isFxApplicationThread()) {
            graphics.strokeLine(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble())
        } else {
            Platform.runLater { graphics.strokeLine(x1.toDouble(), y1.toDouble(), x2.toDouble(), y2.toDouble()) }
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
        if (Platform.isFxApplicationThread()) {
            graphics.fillPolygon(xPoints, yPoints, nPoints)
        } else {
            Platform.runLater { graphics.fillPolygon(xPoints, yPoints, nPoints) }
        }
    }

    /** @see GraphicsContext.strokePolygon */
    fun strokePolygon(xPoints: DoubleArray, yPoints: DoubleArray, nPoints: Int) {
        if (Platform.isFxApplicationThread()) {
            graphics.strokePolygon(xPoints, yPoints, nPoints)
        } else {
            Platform.runLater { graphics.strokePolygon(xPoints, yPoints, nPoints) }
        }
    }

    /** @see GraphicsContext.strokePolyline */
    fun strokePolyline(xPoints: DoubleArray, yPoints: DoubleArray, nPoints: Int) {
        if (Platform.isFxApplicationThread()) {
            graphics.strokePolyline(xPoints, yPoints, nPoints)
        } else {
            Platform.runLater { graphics.strokePolyline(xPoints, yPoints, nPoints) }
        }
    }

    /** @see GraphicsContext.drawImage */
    fun drawImage(img: Image, x: Double, y: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.drawImage(img, x, y)
        } else {
            Platform.runLater { graphics.drawImage(img, x, y) }
        }
    }

    /** @see GraphicsContext.drawImage */
    fun drawImage(bufferedImage: BufferedImage, x: Int, y: Int) {
        drawImage(SwingFXUtils.toFXImage(bufferedImage, null), x.toDouble(), y.toDouble())
    }

    /** @see GraphicsContext.drawImage */
    fun drawImage(img: Image, x: Double, y: Double, w: Double, h: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.drawImage(img, x, y, w, h)
        } else {
            Platform.runLater { graphics.drawImage(img, x, y, w, h) }
        }
    }

    /** @see GraphicsContext.drawImage */
    fun drawImage(img: Image, sx: Double, sy: Double, sw: Double, sh: Double, dx: Double, dy: Double, dw: Double, dh: Double) {
        if (Platform.isFxApplicationThread()) {
            graphics.drawImage(img, sx, sy, sw, sh, dx, dy, dw, dh)
        } else {
            Platform.runLater { graphics.drawImage(img, sx, sy, sw, sh, dx, dy, dw, dh) }
        }
    }

    private fun <T : Event> createHandlerWrapper(newHandler: EventHandler<in T>, originHandler: EventHandler<in T>?): EventHandler<in T> {
        return if (originHandler == null) {
            newHandler
        } else {
            EventHandler { event ->
                originHandler.handle(event)
                newHandler.handle(event)
            }
        }
    }

    private fun <T : Event> createHandlerWrapper(newHandler: (T) -> Unit, originHandler: EventHandler<in T>?): EventHandler<in T> {
        return if (originHandler == null) {
            EventHandler(newHandler)
        } else {
            EventHandler { event ->
                originHandler.handle(event)
                newHandler(event)
            }
        }
    }

    /**
     * Adds a key-pressed handler, this handler will be invoked at the last of event handler chain.
     */
    fun addKeyPressedHandler(handler: EventHandler<in KeyEvent>) {
        val origin = canvas.onKeyPressed
        canvas.onKeyPressed = createHandlerWrapper(handler, origin)
    }

    /**
     * Adds a key-pressed handler, this handler will be invoked at the last of event handler chain.
     */
    fun addKeyPressedHandler(handler: (KeyEvent) -> Unit) {
        val origin = canvas.onKeyPressed
        canvas.onKeyPressed = createHandlerWrapper(handler, origin)
    }

    /**
     * Adds a mouse-clicked handler, this handler will be invoked at the last of event handler chain.
     */
    fun addMouseClickedHandler(handler: EventHandler<in MouseEvent>) {
        val origin = canvas.onMouseClicked
        canvas.onMouseClicked = createHandlerWrapper(handler, origin)
    }

    /**
     * Adds a mouse-clicked handler, this handler will be invoked at the last of event handler chain.
     */
    fun addMouseClickedHandler(handler: (MouseEvent) -> Unit) {
        val origin = canvas.onMouseClicked
        canvas.onMouseClicked = createHandlerWrapper(handler, origin)
    }

    fun <T : Event> addEventHandler(eventType: EventType<T>, eventHandler: EventHandler<in T>) {
        canvas.addEventHandler(eventType, eventHandler)
    }

    fun <T : Event> addEventHandler(eventType: EventType<T>, eventHandler: (T) -> Unit) {
        canvas.addEventHandler(eventType, eventHandler)
    }

}

/**
 * Provides a zooming plugin for the easy canvas, which can provide proper zooming for the drawing and also user-friendly
 * view control. The drawing function must be set through method [setDrawer], which
 * will be called when necessary.
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
 * The user can use mouse dragging and mouse wheel to move and zoom, and can also use direction key to move the view and
 * key '+' or '-' to zoom in or out, which will result in changes of the affine transformation and redrawing.
 * The percentage of moving and zooming can
 * be set via [zoomingFactor] and [movingFactor].
 *
 * @param centerX the real x-coordinate of the center point on the screen
 * @param centerY the real y-coordinate of the center point on the screen
 * @param scale the scaling factor, small value to zoom out, large value to zoom in. If a negative value is given,
 * then a default value will be used instead.
 */
class ZoomingPlugin(val canvas: EasyCanvas, centerX: Double = 0.0, centerY: Double = 0.0, scale: Double = -1.0) {
    private var aff: Affine
    private var drawer: (EasyCanvas) -> Unit = { _ -> Unit }
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


    private var mousePos: Point2D = Point2D.ZERO

    init {
        val width = canvas.width / 2
        val height = canvas.height / 2
        val k = if (scale < 0) {
            min(width / 5, height / 5)
        } else {
            scale
        }
        val tx = -k * centerX + width
        val ty = k * centerY + height
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
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED) {
            mousePos = Point2D(it.x, it.y)
//            println("Pressed")
        }

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED) {
            //            println("Dragged")
            translateView(it.x - mousePos.x, it.y - mousePos.y)
            mousePos = Point2D(it.x, it.y)
        }

        canvas.addEventHandler(ScrollEvent.SCROLL) {
            if (it.deltaY > 0) {
                zoomIn(it.x, it.y)
            } else {
                zoomOut(it.x, it.y)
            }
        }
    }

    /**
     * Zoom in with the zooming factor set.
     * @param centerX the x-coordinate of the zooming center on the screen
     * @param centerY the y-coordinate of the zooming center on the screen
     */
    fun zoomIn(centerX: Double, centerY: Double) {
        aff.prependScale(zoomingFactor, zoomingFactor, centerX, centerY)
        update()
    }

    fun zoomOut(centerX: Double, centerY: Double) {
        val factor = 1 / zoomingFactor
        aff.prependScale(factor, factor, centerX, centerY)
        update()
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
        canvas.draw { gc ->
            gc.save()
            val tr = gc.transform
            tr.append(aff)
            gc.transform = tr
            drawer(canvas)
            gc.restore()
        }
    }

    fun setDrawer(f: (EasyCanvas) -> Unit) {
        drawer = f
        update()
    }

    fun setDrawer(f: Consumer<EasyCanvas>) {
        drawer = { can -> f.accept(can) }
        update()
    }

    /**
     * Translate to make the given point shown at the center.
     */
    fun setCenter(centerX: Double, centerY: Double) {
        val re = aff.transform(centerX, centerY)
        aff.prependTranslation(canvas.width / 2 - re.x, canvas.height / 2 - re.y)
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
//    val zooming = ZoomingPlugin(canvas, 2.0, 2.0)
//    zooming.setCenter(20.0, 10.0)
//    zooming.setDrawer { can ->
//        can.drawLine(10.0, 0.0, 100.0, 100.0)
//    }
//    canvas.show()
//}