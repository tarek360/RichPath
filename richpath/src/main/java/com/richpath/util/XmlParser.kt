package com.richpath.util

import android.content.Context
import android.content.res.XmlResourceParser
import android.graphics.Paint
import android.graphics.Path
import androidx.core.content.ContextCompat
import com.richpath.model.Group
import com.richpath.model.Vector
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import com.richpath.RichPath
import java.io.IOException
import java.util.*


object XmlParser {
    private const val NAMESPACE = "http://schemas.android.com/apk/res/android"

    @Throws(IOException::class, XmlPullParserException::class)
    fun parseVector(vector: Vector, xpp: XmlResourceParser, context: Context) {
        val groupStack = Stack<Group>()
        var eventType = xpp.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = xpp.name
            if (eventType == XmlPullParser.START_TAG) {
                when (tagName) {
                    Vector.TAG_NAME -> parseVectorElement(vector, xpp, context)
                    Group.TAG_NAME -> {
                        val group = parseGroupElement(context, xpp)
                        if (!groupStack.empty()) {
                            group.scale(groupStack.peek().matrix())
                        }
                        groupStack.push(group)
                    }
                    RichPath.TAG_NAME -> {
                        parsePathElement(context, xpp)?.run {
                            if (!groupStack.empty()) {
                                applyGroup(groupStack.peek())
                            }
                            vector.paths.add(this)
                        }
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (Group.TAG_NAME == tagName) {
                    if (!groupStack.empty()) {
                        groupStack.pop()
                    }
                }
            }
            eventType = xpp.next()
        }
        xpp.close()
    }

    private fun parseVectorElement(vector: Vector, xpp: XmlResourceParser, context: Context) {
        vector.inflate(xpp, context)
    }

    private fun parseGroupElement(context: Context, xpp: XmlResourceParser): Group {
        return Group(context, xpp)
    }

    private fun parsePathElement(context: Context, xpp: XmlResourceParser): RichPath? {
        val pathData = getAttributeString(context, xpp, "pathData", null) ?: return null
        val path = RichPath(pathData)
        path.inflate(context, xpp)
        return path
    }

    fun getAttributeString(context: Context, xpp: XmlResourceParser, attributeName: String, defValue: String?): String? {
        val resourceId = getAttributeResourceValue(xpp, attributeName)
        val value: String? = if (resourceId != -1) {
            context.getString(resourceId)
        } else {
            getAttributeValue(xpp, attributeName)
        }
        return value ?: defValue
    }

    fun getAttributeFloat(xpp: XmlResourceParser, attributeName: String, defValue: Float): Float {
        return getAttributeValue(xpp, attributeName)?.toFloat() ?: defValue
    }

    fun getAttributeDimen(context: Context, xpp: XmlResourceParser, attributeName: String, defValue: Float): Float {
        val value = getAttributeValue(xpp, attributeName) ?: return defValue
        return Utils.dpToPixel(context, Utils.getDimenFromString(value))
    }

    fun getAttributeBoolean(xpp: XmlResourceParser, attributeName: String, defValue: Boolean): Boolean {
        return getAttributeValue(xpp, attributeName)?.toBoolean() ?: defValue
    }

    fun getAttributeInt(xpp: XmlResourceParser, attributeName: String, defValue: Int): Int {
        return getAttributeValue(xpp, attributeName)?.toInt() ?: defValue
    }

    fun getAttributeColor(context: Context, xpp: XmlResourceParser, attributeName: String, defValue: Int): Int {
        val resourceId = getAttributeResourceValue(xpp, attributeName)
        if (resourceId != -1) {
            return ContextCompat.getColor(context, resourceId)
        }
        return getAttributeValue(xpp, attributeName)?.let { Utils.getColorFromString(it) } ?: defValue
    }


    fun getAttributeStrokeLineCap(xpp: XmlResourceParser, attributeName: String, defValue: Paint.Cap): Paint.Cap {
        return getAttributeValue(xpp, attributeName)?.let { getStrokeLineCap(it.toInt(), defValue) } ?: defValue
    }

    fun getAttributeStrokeLineJoin(xpp: XmlResourceParser, attributeName: String, defValue: Paint.Join): Paint.Join {
        return getAttributeValue(xpp, attributeName)?.let { getStrokeLineJoin(it.toInt(), defValue) } ?: defValue
    }

    fun getAttributePathFillType(xpp: XmlResourceParser, attributeName: String, defValue: Path.FillType): Path.FillType {
        return getAttributeValue(xpp, attributeName)?.let { getPathFillType(it.toInt(), defValue) } ?: defValue
    }

    private fun getAttributeValue(xpp: XmlResourceParser, attributeName: String): String? {
        return xpp.getAttributeValue(NAMESPACE, attributeName)
    }

    private fun getAttributeResourceValue(xpp: XmlResourceParser, attributeName: String): Int {
        return xpp.getAttributeResourceValue(NAMESPACE, attributeName, -1)
    }

    private fun getStrokeLineCap(id: Int, defValue: Paint.Cap): Paint.Cap {
        return when (id) {
            0 -> Paint.Cap.BUTT
            1 -> Paint.Cap.ROUND
            2 -> Paint.Cap.SQUARE
            else -> defValue
        }
    }

    private fun getStrokeLineJoin(id: Int, defValue: Paint.Join): Paint.Join {
        return when (id) {
            0 -> Paint.Join.MITER
            1 -> Paint.Join.ROUND
            2 -> Paint.Join.BEVEL
            else -> defValue
        }
    }

    private fun getPathFillType(id: Int, defValue: Path.FillType): Path.FillType {
        return when (id) {
            0 -> Path.FillType.WINDING
            1 -> Path.FillType.EVEN_ODD
            2 -> Path.FillType.INVERSE_WINDING
            3 -> Path.FillType.INVERSE_EVEN_ODD
            else -> defValue
        }
    }

}