package richpath.pathparser

import android.graphics.Path
import android.util.Log
import richpath.pathparser.PathParserCompat.copyOfRange
import kotlin.math.*

/**
 * Each PathDataNode represents one command in the "d" attribute of the svg
 * file.
 * An array of PathDataNode can represent the whole "d" attribute.
 */
class PathDataNode(var type: Char, val params: FloatArray) {

    constructor(n: PathDataNode) : this(n.type, copyOfRange(n.params, 0, n.params.size))

    companion object {
        private const val LOGTAG = "PathDataNode"

        /**
         * Convert an array of PathDataNode to Path.
         *
         * @param node The source array of PathDataNode.
         * @param path The target Path object.
         */
        fun nodesToPath(node: Array<PathDataNode>, path: Path) {
            val current = FloatArray(6)
            var previousCommand = 'm'
            (node.indices).forEach { index ->
                addCommand(path, current, previousCommand, node[index].type, node[index].params)
                previousCommand = node[index].type
            }
        }

        private fun addCommand(path: Path, current: FloatArray,
                               previousCommand: Char, cmd: Char, params: FloatArray) {
            var previousCmd = previousCommand
            var incr = 2
            var currentX = current[0]
            var currentY = current[1]
            var ctrlPointX = current[2]
            var ctrlPointY = current[3]
            var currentSegmentStartX = current[4]
            var currentSegmentStartY = current[5]
            var reflectiveCtrlPointX: Float
            var reflectiveCtrlPointY: Float

            when (cmd) {
                'z', 'Z' -> {
                    path.close()
                    // Path is closed here, but we need to move the pen to the
                    // closed position. So we cache the segment's starting position,
                    // and restore it here.
                    currentX = currentSegmentStartX
                    currentY = currentSegmentStartY
                    ctrlPointX = currentSegmentStartX
                    ctrlPointY = currentSegmentStartY
                    path.moveTo(currentX, currentY)
                }
                'm', 'M', 'l', 'L', 't', 'T' -> incr = 2
                'h', 'H', 'v', 'V' -> incr = 1
                'c', 'C' -> incr = 6
                's', 'S', 'q', 'Q' -> incr = 4
                'a', 'A' -> incr = 7
            }

            for (k in params.indices step incr) {
                when (cmd) {
                    'm' -> { // moveto - Start a new sub-path (relative)
                        currentX += params[k + 0]
                        currentY += params[k + 1]
                        if (k > 0) {
                            // According to the spec, if a moveto is followed by multiple
                            // pairs of coordinates, the subsequent pairs are treated as
                            // implicit lineto commands.
                            path.rLineTo(params[k + 0], params[k + 1])
                        } else {
                            path.rMoveTo(params[k + 0], params[k + 1])
                            currentSegmentStartX = currentX
                            currentSegmentStartY = currentY
                        }
                    }
                    'M' -> { // moveto - Start a new sub-path
                        currentX = params[k + 0]
                        currentY = params[k + 1]
                        if (k > 0) {
                            // According to the spec, if a moveto is followed by multiple
                            // pairs of coordinates, the subsequent pairs are treated as
                            // implicit lineto commands.
                            path.lineTo(params[k + 0], params[k + 1])
                        } else {
                            path.moveTo(params[k + 0], params[k + 1])
                            currentSegmentStartX = currentX
                            currentSegmentStartY = currentY
                        }
                    }
                    'l' -> { // lineto - Draw a line from the current point (relative)
                        path.rLineTo(params[k + 0], params[k + 1])
                        currentX += params[k + 0]
                        currentY += params[k + 1]
                    }
                    'L' -> { // lineto - Draw a line from the current point
                        path.lineTo(params[k + 0], params[k + 1])
                        currentX = params[k + 0]
                        currentY = params[k + 1]
                    }
                    'h' -> { // horizontal lineto - Draws a horizontal line (relative)
                        path.rLineTo(params[k + 0], 0f)
                        currentX += params[k + 0]
                    }
                    'H' -> { // horizontal lineto - Draws a horizontal line
                        path.lineTo(params[k + 0], currentY)
                        currentX = params[k + 0]
                    }
                    'v' -> { // vertical lineto - Draws a vertical line from the current point (r)
                        path.rLineTo(0f, params[k + 0])
                        currentY += params[k + 0]
                    }
                    'V' -> { // vertical lineto - Draws a vertical line from the current point
                        path.lineTo(currentX, params[k + 0])
                        currentY = params[k + 0]
                    }
                    'c' -> { // curveto - Draws a cubic Bézier curve (relative)
                        path.rCubicTo(params[k + 0], params[k + 1], params[k + 2], params[k + 3],
                                params[k + 4], params[k + 5])

                        ctrlPointX = currentX + params[k + 2]
                        ctrlPointY = currentY + params[k + 3]
                        currentX += params[k + 4]
                        currentY += params[k + 5]
                    }
                    'C' -> { // curveto - Draws a cubic Bézier curve
                        path.cubicTo(params[k + 0], params[k + 1], params[k + 2], params[k + 3],
                                params[k + 4], params[k + 5])
                        currentX = params[k + 4]
                        currentY = params[k + 5]
                        ctrlPointX = params[k + 2]
                        ctrlPointY = params[k + 3]
                    }
                    's' -> { // smooth curveto - Draws a cubic Bézier curve (reflective cp)
                        reflectiveCtrlPointX = 0f
                        reflectiveCtrlPointY = 0f
                        if (previousCmd == 'c' || previousCmd == 's'
                                || previousCmd == 'C' || previousCmd == 'S') {
                            reflectiveCtrlPointX = currentX - ctrlPointX
                            reflectiveCtrlPointY = currentY - ctrlPointY
                        }
                        path.rCubicTo(reflectiveCtrlPointX, reflectiveCtrlPointY,
                                params[k + 0], params[k + 1],
                                params[k + 2], params[k + 3])

                        ctrlPointX = currentX + params[k + 0]
                        ctrlPointY = currentY + params[k + 1]
                        currentX += params[k + 2]
                        currentY += params[k + 3]
                    }
                    'S' -> { // shorthand/smooth curveto Draws a cubic Bézier curve(reflective cp)
                        reflectiveCtrlPointX = currentX
                        reflectiveCtrlPointY = currentY
                        if (previousCmd == 'c' || previousCmd == 's'
                                || previousCmd == 'C' || previousCmd == 'S') {
                            reflectiveCtrlPointX = 2 * currentX - ctrlPointX
                            reflectiveCtrlPointY = 2 * currentY - ctrlPointY
                        }
                        path.cubicTo(reflectiveCtrlPointX, reflectiveCtrlPointY,
                                params[k + 0], params[k + 1],
                                params[k + 2], params[k + 3])

                        ctrlPointX = params[k + 0]
                        ctrlPointY = params[k + 1]
                        currentX += params[k + 2]
                        currentY += params[k + 3]
                    }
                    'q' -> { // Draws a quadratic Bézier (relative)
                        path.rQuadTo(params[k + 0], params[k + 1], params[k + 2], params[k + 3])
                        ctrlPointX = currentX + params[k + 0]
                        ctrlPointY = currentY + params[k + 1]
                        currentX += params[k + 2]
                        currentY += params[k + 3]
                    }
                    'Q' -> { // Draws a quadratic Bézier
                        path.quadTo(params[k + 0], params[k + 1], params[k + 2], params[k + 3])
                        ctrlPointX = params[k + 0]
                        ctrlPointY = params[k + 1]
                        currentX += params[k + 2]
                        currentY += params[k + 3]
                    }
                    't' -> { // Draws a quadratic Bézier curve(reflective control point)(relative)
                        reflectiveCtrlPointX = 0f
                        reflectiveCtrlPointY = 0f
                        if (previousCmd == 'q' || previousCmd == 't'
                                || previousCmd == 'Q' || previousCmd == 'T') {
                            reflectiveCtrlPointX = currentX - ctrlPointX
                            reflectiveCtrlPointY = currentY - ctrlPointY
                        }
                        path.rQuadTo(reflectiveCtrlPointX, reflectiveCtrlPointY,
                                params[k + 0], params[k + 1])
                        ctrlPointX = currentX + reflectiveCtrlPointX
                        ctrlPointY = currentY + reflectiveCtrlPointY
                        currentX += params[k + 0]
                        currentY += params[k + 1]
                    }
                    'T' -> { // Draws a quadratic Bézier curve (reflective control point)
                        reflectiveCtrlPointX = currentX
                        reflectiveCtrlPointY = currentY
                        if (previousCmd == 'q' || previousCmd == 't'
                                || previousCmd == 'Q' || previousCmd == 'T') {
                            reflectiveCtrlPointX = 2 * currentX - ctrlPointX
                            reflectiveCtrlPointY = 2 * currentY - ctrlPointY
                        }
                        path.quadTo(reflectiveCtrlPointX, reflectiveCtrlPointY,
                                params[k + 0], params[k + 1])
                        ctrlPointX = reflectiveCtrlPointX
                        ctrlPointY = reflectiveCtrlPointY
                        currentX = params[k + 0]
                        currentY = params[k + 1]
                    }
                    'a' -> { // Draws an elliptical arc
                        // (rx ry x-axis-rotation large-arc-flag sweep-flag x y)
                        drawArc(path,
                                currentX,
                                currentY,
                                params[k + 5] + currentX,
                                params[k + 6] + currentY,
                                params[k + 0],
                                params[k + 1],
                                params[k + 2],
                                params[k + 3] != 0f,
                                params[k + 4] != 0f)
                        currentX += params[k + 5]
                        currentY += params[k + 6]
                        ctrlPointX = currentX
                        ctrlPointY = currentY
                    }
                    'A' -> { // Draws an elliptical arc
                        drawArc(path,
                                currentX,
                                currentY,
                                params[k + 5],
                                params[k + 6],
                                params[k + 0],
                                params[k + 1],
                                params[k + 2],
                                params[k + 3] != 0f,
                                params[k + 4] != 0f)
                        currentX = params[k + 5]
                        currentY = params[k + 6]
                        ctrlPointX = currentX
                        ctrlPointY = currentY
                    }
                }
                previousCmd = cmd
            }
            current[0] = currentX
            current[1] = currentY
            current[2] = ctrlPointX
            current[3] = ctrlPointY
            current[4] = currentSegmentStartX
            current[5] = currentSegmentStartY
        }

        private fun drawArc(p: Path,
                            x0: Float,
                            y0: Float,
                            x1: Float,
                            y1: Float,
                            a: Float,
                            b: Float,
                            theta: Float,
                            isMoreThanHalf: Boolean,
                            isPositiveArc: Boolean) {

            /* Convert rotation angle from degrees to radians */
            val thetaD = Math.toRadians(theta.toDouble())
            /* Pre-compute rotation matrix entries */
            val cosTheta = cos(thetaD)
            val sinTheta = sin(thetaD)
            /* Transform (x0, y0) and (x1, y1) into unit space */
            /* using (inverse) rotation, followed by (inverse) scale */
            val x0p = (x0 * cosTheta + y0 * sinTheta) / a
            val y0p = (-x0 * sinTheta + y0 * cosTheta) / b
            val x1p = (x1 * cosTheta + y1 * sinTheta) / a
            val y1p = (-x1 * sinTheta + y1 * cosTheta) / b

            /* Compute differences and averages */
            val dx = x0p - x1p
            val dy = y0p - y1p
            val xm = (x0p + x1p) / 2
            val ym = (y0p + y1p) / 2
            /* Solve for intersecting unit circles */
            val dsq = dx * dx + dy * dy
            if (dsq == 0.0) {
                Log.w(LOGTAG, " Points are coincident")
                return  /* Points are coincident */
            }
            val disc = 1.0 / dsq - 1.0 / 4.0
            if (disc < 0.0) {
                Log.w(LOGTAG, "Points are too far apart $dsq")
                val adjust = (sqrt(dsq) / 1.99999).toFloat()
                drawArc(p, x0, y0, x1, y1, a * adjust,
                        b * adjust, theta, isMoreThanHalf, isPositiveArc)
                return  /* Points are too far apart */
            }
            val s = sqrt(disc)
            val sdx = s * dx
            val sdy = s * dy
            var cx: Double
            var cy: Double
            if (isMoreThanHalf == isPositiveArc) {
                cx = xm - sdy
                cy = ym + sdx
            } else {
                cx = xm + sdy
                cy = ym - sdx
            }

            val eta0 = atan2(y0p - cy, x0p - cx)

            val eta1 = atan2(y1p - cy, x1p - cx)

            var sweep = eta1 - eta0
            if (isPositiveArc != sweep >= 0) {
                if (sweep > 0) {
                    sweep -= 2 * Math.PI
                } else {
                    sweep += 2 * Math.PI
                }
            }

            cx *= a
            cy *= b
            val tcx = cx
            cx = cx * cosTheta - cy * sinTheta
            cy = tcx * sinTheta + cy * cosTheta

            arcToBezier(p, cx, cy, a.toDouble(), b.toDouble(), x0.toDouble(), y0.toDouble(), thetaD, eta0, sweep)

        }

        /**
         * Converts an arc to cubic Bezier segments and records them in p.
         *
         * @param p     The target for the cubic Bezier segments
         * @param cx    The x coordinate center of the ellipse
         * @param cy    The y coordinate center of the ellipse
         * @param a     The radius of the ellipse in the horizontal direction
         * @param b     The radius of the ellipse in the vertical direction
         * @param e1x   E(eta1) x coordinate of the starting point of the arc
         * @param e1y   E(eta2) y coordinate of the starting point of the arc
         * @param theta The angle that the ellipse bounding rectangle makes with horizontal plane
         * @param start The start angle of the arc on the ellipse
         * @param sweep The angle (positive or negative) of the sweep of the arc on the ellipse
         */
        private fun arcToBezier(p: Path,
                                cx: Double,
                                cy: Double,
                                a: Double,
                                b: Double,
                                e1x: Double,
                                e1y: Double,
                                theta: Double,
                                start: Double,
                                sweep: Double) {
            // Taken from equations at: http://spaceroots.org/documents/ellipse/node8.html
            // and http://www.spaceroots.org/documents/ellipse/node22.html

            // Maximum of 45 degrees per cubic Bezier segment
            val numSegments = ceil(abs(sweep * 4 / Math.PI)).toInt()

            var e1x = e1x
            var e1y = e1y
            var eta1 = start
            val cosTheta = cos(theta)
            val sinTheta = sin(theta)
            val cosEta1 = cos(eta1)
            val sinEta1 = sin(eta1)
            var ep1x = (-a * cosTheta * sinEta1) - (b * sinTheta * cosEta1)
            var ep1y = (-a * sinTheta * sinEta1) + (b * cosTheta * cosEta1)

            val anglePerSegment = sweep / numSegments
            for (i in 0 until numSegments) {
                val eta2 = eta1 + anglePerSegment
                val sinEta2 = sin(eta2)
                val cosEta2 = cos(eta2)
                val e2x = cx + (a * cosTheta * cosEta2) - (b * sinTheta * sinEta2)
                val e2y = cy + (a * sinTheta * cosEta2) + (b * cosTheta * sinEta2)
                val ep2x = -a * cosTheta * sinEta2 - b * sinTheta * cosEta2
                val ep2y = -a * sinTheta * sinEta2 + b * cosTheta * cosEta2
                val tanDiff2 = tan((eta2 - eta1) / 2)
                val alpha = sin(eta2 - eta1) * (sqrt(4 + 3 * tanDiff2 * tanDiff2) - 1) / 3
                val q1x = e1x + alpha * ep1x
                val q1y = e1y + alpha * ep1y
                val q2x = e2x - alpha * ep2x
                val q2y = e2y - alpha * ep2y

                // Adding this no-op call to workaround a proguard related issue.
                p.rLineTo(0f, 0f)
                p.cubicTo(q1x.toFloat(),
                        q1y.toFloat(),
                        q2x.toFloat(),
                        q2y.toFloat(),
                        e2x.toFloat(),
                        e2y.toFloat())
                eta1 = eta2
                e1x = e2x
                e1y = e2y
                ep1x = ep2x
                ep1y = ep2y
            }
        }
    }

    /**
     * The current PathDataNode will be interpolated between the
     * <code>nodeFrom</code> and <code>nodeTo</code> according to the
     * <code>fraction</code>.
     *
     * @param nodeFrom The start value as a PathDataNode.
     * @param nodeTo   The end value as a PathDataNode
     * @param fraction The fraction to interpolate.
     */
    fun interpolatePathDataNode(nodeFrom: PathDataNode, nodeTo: PathDataNode, fraction: Float) {
        (nodeFrom.params.indices).forEach { index ->
            params[index] = nodeFrom.params[index] * (1 - fraction) + nodeTo.params[index] * fraction
        }
    }
}