package com.richpathanimator

import android.animation.TypeEvaluator
import com.richpath.pathparser.PathDataNode
import com.richpath.pathparser.PathParserCompat

class PathEvaluator: TypeEvaluator<Array<PathDataNode>?> {
    private var evaluatedNodes: Array<PathDataNode>? = null

    override fun evaluate(fraction: Float, startPathDataNodes: Array<PathDataNode>?, endPathDataNodes: Array<PathDataNode>?): Array<PathDataNode>? {
        if (startPathDataNodes == null || endPathDataNodes == null) return null
        val evaluatedNodes = this.evaluatedNodes ?: PathParserCompat.deepCopyNodes(startPathDataNodes)
        this.evaluatedNodes = evaluatedNodes

        val startNodeSize = startPathDataNodes.size
        for (i in 0 until startNodeSize) {
            val nodeParamSize = startPathDataNodes[i].params.size
            for (j in 0 until nodeParamSize) {
                val startFloat = startPathDataNodes[i].params[j]
                val value = startFloat + fraction * (endPathDataNodes[i].params[j] - startFloat)
                evaluatedNodes[i].params[j] = value
            }
        }
        return evaluatedNodes
    }
}
