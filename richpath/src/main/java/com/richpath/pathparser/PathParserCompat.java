/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.richpath.pathparser;

import android.graphics.Path;

import java.util.ArrayList;

// This class is a duplicate from the PathParserCompat.java of frameworks/base, with slight
// update on incompatible API like copyOfRange().
public class PathParserCompat {

    // Copy from Arrays.copyOfRange() which is only available from API level 9.

    /**
     * Copies elements from {@code original} into a new array, from indexes start (inclusive) to
     * end (exclusive). The original order of elements is preserved.
     * If {@code end} is greater than {@code original.length}, the result is padded
     * with the value {@code 0.0f}.
     *
     * @param original the original array
     * @param start    the start index, inclusive
     * @param end      the end index, exclusive
     * @return the new array
     * @throws ArrayIndexOutOfBoundsException if {@code start < 0 || start > original.length}
     * @throws IllegalArgumentException       if {@code start > end}
     * @throws NullPointerException           if {@code original == null}
     */
    static float[] copyOfRange(float[] original, int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException();
        }
        int originalLength = original.length;
        if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end - start;
        int copyLength = Math.min(resultLength, originalLength - start);
        float[] result = new float[resultLength];
        System.arraycopy(original, start, result, 0, copyLength);
        return result;
    }

    /**
     * @param pathData The string representing a path, the same as "d" string in svg file.
     * @return the generated Path object.
     */
    static Path createPathFromPathData(String pathData) {
        Path path = new Path();
        createPathFromPathData(path, pathData);
        return path;
    }

    static void createPathFromPathData(Path path, String pathData) {
        PathDataNode[] nodes = createNodesFromPathData(pathData);
        if (nodes != null) {
            try {
                PathDataNode.nodesToPath(nodes, path);
            } catch (RuntimeException e) {
                throw new RuntimeException("Error in parsing " + pathData, e);
            }
        }
    }

    /**
     * @param pathData The string representing a path, the same as "d" string in svg file.
     * @return an array of the PathDataNode.
     */
    public static PathDataNode[] createNodesFromPathData(String pathData) {
        if (pathData == null) {
            return null;
        }
        int start = 0;
        int end = 1;

        ArrayList<PathDataNode> list = new ArrayList<PathDataNode>();
        while (end < pathData.length()) {
            end = nextStart(pathData, end);
            String s = pathData.substring(start, end).trim();
            if (s.length() > 0) {
                float[] val = getFloats(s);
                addNode(list, s.charAt(0), val);
            }

            start = end;
            end++;
        }
        if ((end - start) == 1 && start < pathData.length()) {
            addNode(list, pathData.charAt(start), new float[0]);
        }
        return list.toArray(new PathDataNode[list.size()]);
    }

    /**
     * @param source The array of PathDataNode to be duplicated.
     * @return a deep copy of the <code>source</code>.
     */
    public static PathDataNode[] deepCopyNodes(PathDataNode[] source) {
        if (source == null) {
            return null;
        }
        PathDataNode[] copy = new PathDataNode[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = new PathDataNode(source[i]);
        }
        return copy;
    }

    /**
     * @param nodesFrom The source path represented in an array of PathDataNode
     * @param nodesTo   The target path represented in an array of PathDataNode
     * @return whether the <code>nodesFrom</code> can morph into <code>nodesTo</code>
     */
    public static boolean canMorph(PathDataNode[] nodesFrom, PathDataNode[] nodesTo) {
        if (nodesFrom == null || nodesTo == null) {
            return false;
        }

        if (nodesFrom.length != nodesTo.length) {
            return false;
        }

        for (int i = 0; i < nodesFrom.length; i++) {
            if (nodesFrom[i].type != nodesTo[i].type
                    || nodesFrom[i].params.length != nodesTo[i].params.length) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param nodes paths represented in an array of an array of PathDataNode
     * @return whether the <code>nodesFrom</code> can morph into <code>nodesTo</code>
     */
    public static boolean canMorph(PathDataNode[]... nodes) {

        for (PathDataNode[] pathDataNode : nodes) {
            if (pathDataNode == null) {
                return false;
            }
        }

        for (int i = 0; i < nodes.length - 1; i++) {
            if (nodes[i].length != nodes[i + 1].length) {
                return false;
            }
        }

        for (int i = 0; i < nodes.length - 1; i++) {
            for (int j = 0; j < nodes[i].length; j++) {
                if (nodes[i][j].type != nodes[i + 1][j].type
                        || nodes[i][j].params.length != nodes[i + 1][j].params.length) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Update the target's data to match the source.
     * Before calling this, make sure canMorph(target, source) is true.
     *
     * @param target The target path represented in an array of PathDataNode
     * @param source The source path represented in an array of PathDataNode
     */
    public static void updateNodes(PathDataNode[] target, PathDataNode[] source) {
        for (int i = 0; i < source.length; i++) {
            target[i].type = source[i].type;
            for (int j = 0; j < source[i].params.length; j++) {
                target[i].params[j] = source[i].params[j];
            }
        }
    }

    private static int nextStart(String s, int end) {
        char c;

        while (end < s.length()) {
            c = s.charAt(end);
            // Note that 'e' or 'E' are not valid path commands, but could be
            // used for floating point numbers' scientific notation.
            // Therefore, when searching for next command, we should ignore 'e'
            // and 'E'.
            if ((((c - 'A') * (c - 'Z') <= 0) || ((c - 'a') * (c - 'z') <= 0))
                    && c != 'e' && c != 'E') {
                return end;
            }
            end++;
        }
        return end;
    }

    private static void addNode(ArrayList<PathDataNode> list, char cmd, float[] val) {
        list.add(new PathDataNode(cmd, val));
    }

    private static class ExtractFloatResult {
        // We need to return the position of the next separator and whether the
        // next float starts with a '-' or a '.'.
        int mEndPosition;
        boolean mEndWithNegOrDot;

        ExtractFloatResult() {
        }
    }

    /**
     * Parse the floats in the string.
     * This is an optimized version of parseFloat(s.split(",|\\s"));
     *
     * @param s the string containing a command and list of floats
     * @return array of floats
     */
    private static float[] getFloats(String s) {
        if (s.charAt(0) == 'z' | s.charAt(0) == 'Z') {
            return new float[0];
        }
        try {
            float[] results = new float[s.length()];
            int count = 0;
            int startPosition = 1;
            int endPosition = 0;

            ExtractFloatResult result = new ExtractFloatResult();
            int totalLength = s.length();

            // The startPosition should always be the first character of the
            // current number, and endPosition is the character after the current
            // number.
            while (startPosition < totalLength) {
                extract(s, startPosition, result);
                endPosition = result.mEndPosition;

                if (startPosition < endPosition) {
                    results[count++] = Float.parseFloat(
                            s.substring(startPosition, endPosition));
                }

                if (result.mEndWithNegOrDot) {
                    // Keep the '-' or '.' sign with next number.
                    startPosition = endPosition;
                } else {
                    startPosition = endPosition + 1;
                }
            }
            return copyOfRange(results, 0, count);
        } catch (NumberFormatException e) {
            throw new RuntimeException("error in parsing \"" + s + "\"", e);
        }
    }

    /**
     * Calculate the position of the next comma or space or negative sign
     *
     * @param s      the string to search
     * @param start  the position to start searching
     * @param result the result of the extraction, including the position of the
     *               the starting position of next number, whether it is ending with a '-'.
     */
    private static void extract(String s, int start, ExtractFloatResult result) {
        // Now looking for ' ', ',', '.' or '-' from the start.
        int currentIndex = start;
        boolean foundSeparator = false;
        result.mEndWithNegOrDot = false;
        boolean secondDot = false;
        boolean isExponential = false;
        for (; currentIndex < s.length(); currentIndex++) {
            boolean isPrevExponential = isExponential;
            isExponential = false;
            char currentChar = s.charAt(currentIndex);
            switch (currentChar) {
                case ' ':
                case ',':
                    foundSeparator = true;
                    break;
                case '-':
                    // The negative sign following a 'e' or 'E' is not a separator.
                    if (currentIndex != start && !isPrevExponential) {
                        foundSeparator = true;
                        result.mEndWithNegOrDot = true;
                    }
                    break;
                case '.':
                    if (!secondDot) {
                        secondDot = true;
                    } else {
                        // This is the second dot, and it is considered as a separator.
                        foundSeparator = true;
                        result.mEndWithNegOrDot = true;
                    }
                    break;
                case 'e':
                case 'E':
                    isExponential = true;
                    break;
            }
            if (foundSeparator) {
                break;
            }
        }
        // When there is nothing found, then we put the end position to the end
        // of the string.
        result.mEndPosition = currentIndex;
    }
}
