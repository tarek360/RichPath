package richpath.model

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.Matrix
import richpath.util.XmlParser

class Group(context: Context, xpp: XmlResourceParser) {
    companion object {
        const val TAG_NAME = "group"
    }

    var rotation = 0f
        private set
    var pivotX = 0f
        private set
    var pivotY = 0f
        private set
    var scaleX = 1f
        private set
    var scaleY = 1f
        private set
    var translateX = 0f
        private set
    var translateY = 0f
        private set
    var name: String? = null
        private set
    private var matrix: Matrix? = null

    init {
        inflate(context, xpp)
    }

    private fun inflate(context: Context, xpp: XmlResourceParser) {

        name = XmlParser.getAttributeString(context, xpp, "name", name)

        rotation = XmlParser.getAttributeFloat(xpp, "rotation", rotation)

        scaleX = XmlParser.getAttributeFloat(xpp, "scaleX", scaleX)

        scaleY = XmlParser.getAttributeFloat(xpp, "scaleY", scaleY)

        translateX = XmlParser.getAttributeFloat(xpp, "translateX", translateX)

        translateY = XmlParser.getAttributeFloat(xpp, "translateY", translateY)

        pivotX = XmlParser.getAttributeFloat(xpp, "pivotX", pivotX) + translateX

        pivotY = XmlParser.getAttributeFloat(xpp, "pivotY", pivotY) + translateY
    }

    fun matrix(): Matrix {
        val matrix = this.matrix?.let { it } ?: run {
            Matrix().apply {
                postTranslate(-pivotX, -pivotY)
                postScale(scaleX, scaleY)
                postRotate(rotation, 0f, 0f)
                postTranslate(translateX + pivotX, translateY + pivotY)
            }
        }
        this.matrix = matrix
        return matrix
    }

    fun scale(matrix: Matrix) {
        matrix().postConcat(matrix)
    }
}