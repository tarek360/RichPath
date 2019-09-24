package richpathanimator

import android.animation.TypeEvaluator
import com.richpath.pathparser.PathDataNode
import com.richpath.pathparser.PathParserCompat

class PathEvaluator: TypeEvaluator<Array<PathDataNode>> {
    private var evaluatedNodes: Array<PathDataNode>? = null

    override fun evaluate(fraction: Float, startPathDataNodes: Array<PathDataNode>?, endPathDataNodes: Array<PathDataNode>?): Array<PathDataNode> {
        if (evaluatedNodes == null) {
            evaluatedNodes = PathParserCompat.deepCopyNodes(startPathDataNodes)
        }
        if (startPathDataNodes == null || endPathDataNodes == null) return evaluatedNodes!!

        val startNodeSize = startPathDataNodes.size
        for (i in 0 until startNodeSize) {
            val nodeParamSize = startPathDataNodes[i].params?.size ?: 0
            for (j in 0 until nodeParamSize) {
                val startFloat = startPathDataNodes[i].params[j]
                val value = startFloat + fraction * (endPathDataNodes[i].params[j] - startFloat)
                evaluatedNodes!![i].params[j] = value
            }
        }
        return evaluatedNodes!!
    }
}