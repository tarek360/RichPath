package richpath.pathparser

import android.graphics.Path
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

object PathParserCompatApi21 {

    /**
     * @param pathData The string representing a path, the same as "d" string in svg file.
     * @return the generated Path object.
     */
    fun createPathFromPathData(pathData: String?): Path? {
        try {
            val method = getCreatePathFromPathDataMethod() ?: return null
            val obj = method.invoke(null, pathData)
            return obj as? Path
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getCreatePathFromPathDataMethod(): Method? {
        try {
            return Class.forName("android.util.PathParser")
                    .getDeclaredMethod("createPathFromPathData", String::class.java)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
        return null
    }
}