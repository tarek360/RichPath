package richpath

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.appcompat.widget.AppCompatImageView
import com.richpath.R
import richpath.pathparser.PathParser
import richpath.model.Vector
import org.xmlpull.v1.XmlPullParserException
import richpath.util.XmlParser
import java.io.IOException
import kotlin.math.min

class RichPathView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : AppCompatImageView(context, attrs, defStyleAttr) {

    constructor(context: Context?, attrs: AttributeSet?): this(context, attrs, 0)

    private lateinit var vector: Vector
    private var richPathDrawable: RichPathDrawable? = null
    var onPathClickListener: RichPath.OnPathClickListener? = null

    init {
        init()
        setupAttributes(attrs)
    }

    private fun init() {
        //Disable hardware
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        // Obtain a typed array of attributes
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.RichPathView, 0, 0)
        // Extract custom attributes into member variables
        val resID: Int
        try {
            resID = typedArray.getResourceId(R.styleable.RichPathView_vector, -1)
        } finally { // TypedArray objects are shared and must be recycled.
            typedArray.recycle()
        }

        if (resID != -1) {
            setVectorDrawable(resID)
        }
    }

    /**
     * Set a VectorDrawable resource ID.
     *
     * @param resId the resource ID for VectorDrawableCompat object.
     */
    @SuppressLint("ResourceType")
    fun setVectorDrawable(@DrawableRes resId: Int) {
        val xpp = context.resources.getXml(resId)
        vector = Vector()
        try {
            XmlParser.parseVector(vector, xpp, context)
            richPathDrawable = RichPathDrawable(vector, scaleType)
            setImageDrawable(richPathDrawable)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val vector = vector ?: return
        val desiredWidth = vector.width
        val desiredHeight = vector.height

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //Measure Width
        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                //Must be this size
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                //Can't be bigger than...
                min(desiredWidth.toInt(), widthSize)
            }
            else -> {
                //Be whatever you want
                desiredWidth.toInt()
            }
        }

        //Measure Height
        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                //Must be this size
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                //Can't be bigger than...
                min(desiredHeight.toInt(), heightSize)
            }
            else -> {
                //Be whatever you want
                desiredHeight.toInt()
            }
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height)
    }

    fun findAllRichPaths(): Array<RichPath> {
        return richPathDrawable?.findAllRichPaths() ?: arrayOf()
    }

    fun findRichPathByName(name: String): RichPath? {
        return richPathDrawable?.findRichPathByName(name)
    }

    /**
     * find the first [RichPath] or null if not found
     * <p>
     * This can be in handy if the vector consists of 1 path only
     *
     * @return the [RichPath] object found or null
     */
    fun findFirstRichPath(): RichPath? {
        return richPathDrawable?.findFirstRichPath()
    }

    /**
     * find [RichPath] by its index or null if not found
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
     * @return the [RichPath] object found or null
     */
    fun findRichPathByIndex(@IntRange(from = 0) index: Int): RichPath? {
        return richPathDrawable?.findRichPathByIndex(index)
    }

    fun addPath(path: String) {
        richPathDrawable?.addPath(PathParser.createPathFromPathData(path))
    }

    fun addPath(path: Path) {
        richPathDrawable?.addPath(path)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_UP -> {
                performClick()
            }
        }

        richPathDrawable?.getTouchedPath(event)?.let { richPath ->
            richPath.onPathClickListener?.onClick(richPath)
            this.onPathClickListener?.onClick(richPath)
        }
        return true
    }
}