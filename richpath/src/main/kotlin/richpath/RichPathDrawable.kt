package richpath

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import androidx.annotation.IntRange
import richpath.listener.OnRichPathUpdatedListener
import richpath.pathparser.PathParser
import richpath.util.PathUtils
import richpath.model.Vector
import kotlin.math.min

class RichPathDrawable(private val vector: Vector?, private val scaleType: ImageView.ScaleType): Drawable() {

    private var width: Int = 0
    private var height: Int = 0

    init {
        listenToPathsUpdates()
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        bounds?.let {
            if (it.width() > 0 && it.height() > 0) {
                width = it.width()
                height = it.height()
                mapPaths()
            }
        }
    }

    internal fun mapPaths() {
        val vector = vector ?: return

        val centerX = width / 2f
        val centerY = height / 2f

        val matrix = Matrix()

        matrix.postTranslate(centerX - vector.currentWidth / 2,
                centerY - vector.currentHeight / 2)

        val widthRatio = width / vector.currentWidth
        val heightRatio = height / vector.currentHeight


        if (scaleType == ScaleType.FIT_XY) {
            matrix.postScale(widthRatio, heightRatio, centerX, centerY)
        } else {
            val ratio: Float = if (width < height) {
                widthRatio
            } else {
                heightRatio
            }
            matrix.postScale(ratio, ratio, centerX, centerY)
        }

        val absWidthRatio = width / vector.viewportWidth
        val absHeightRatio = height / vector.viewportHeight
        val absRatio = min(absWidthRatio, absHeightRatio)

        for (path in vector.paths) {
            path.mapToMatrix(matrix)
            path.scaleStrokeWidth(absRatio)
        }

        vector.currentWidth = width.toFloat()
        vector.currentHeight = height.toFloat()
    }

    fun findAllRichPaths(): Array<RichPath> {
        return vector?.paths?.toTypedArray() ?: arrayOf()
    }

    fun findRichPathByName(name: String): RichPath? {
        val vector = vector ?: return null
        for (path in vector.paths) {
            if (name == path.name) {
                return path
            }
        }
        return null
    }

    /**
     * find the first {@link RichPath} or null if not found
     * <p>
     * This can be in handy if the vector consists of 1 path only
     *
     * @return the {@link RichPath} object found or null
     */
    fun findFirstRichPath(): RichPath? {
        return findRichPathByIndex(0)
    }

    /**
     * find {@link RichPath} by its index or null if not found
     * <p>
     * Note that the provided index must be the flattened index of the path
     * <p>
     * example:
     * <pre>
     * {@code <vector>
     *     <path> // index = 0
     *     <path> // index = 1
     *     <group>
     *          <path> // index = 2
     *          <group>
     *              <path> // index = 3
     *          </group>
     *      </group>
     *      <path> // index = 4
     *   </vector>}
     * </pre>
     *
     * @param index the flattened index of the path
     * @return the {@link RichPath} object found or null
     */
    fun findRichPathByIndex(@IntRange(from = 0) index: Int): RichPath? {
        if (vector == null || index < 0 || index >= vector.paths.size) return null
        return vector.paths[index]
    }

    private fun listenToPathsUpdates() {
        val vector = vector ?: return
        for (path in vector.paths) {
            path.onRichPathUpdatedListener = object : OnRichPathUpdatedListener {
                override fun onPathUpdated() {
                    invalidateSelf()
                }
            }
        }
    }

    fun addPath(path: String) {
        addPath(PathParser.createPathFromPathData(path))
    }

    fun addPath(path: Path) {
        if (path is RichPath) {
            addPath(path)
        } else {
            addPath(RichPath(path))
        }
    }

    private fun addPath(path: RichPath) {
        val vector = vector ?: return

        vector.paths.add(path)
        path.onRichPathUpdatedListener = object : OnRichPathUpdatedListener {
            override fun onPathUpdated() {
                invalidateSelf()
            }
        }
        invalidateSelf()
    }

    fun getTouchedPath(event: MotionEvent?): RichPath? {
        val vector = vector ?: return null

        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                for (i in vector.paths.indices.reversed()) {
                    val richPath = vector.paths[i]
                    if (PathUtils.isTouched(richPath, event.x, event.y)) {
                        return richPath
                    }
                }
            }
        }

        return null
    }

    override fun draw(canvas: Canvas) {
        if (vector == null || vector.paths.size < 0) return

        for (path in vector.paths) {
            path.draw(canvas)
        }
    }

    override fun setAlpha(@IntRange(from = 0, to = 255) alpha: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }
}