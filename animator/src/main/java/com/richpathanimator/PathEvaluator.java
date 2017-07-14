package com.richpathanimator;

import android.animation.TypeEvaluator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tarek on 7/14/17.
 */

public class PathEvaluator implements TypeEvaluator<String> {

    private String pathHolder;
    private String startPath;
    private String endPath;

    private List<Float> startFloats = new ArrayList<>();
    private List<Float> endFloats = new ArrayList<>();

    @Override
    public String evaluate(float fraction, String startPath, String endPath) {


        if (pathHolder == null
                || !this.startPath.equals(startPath) || !this.endPath.equals(endPath)) {

            this.pathHolder = endPath;
            this.startPath = startPath;
            this.endPath = endPath;

            startFloats.clear();
            endFloats.clear();

            Pattern p = Pattern.compile("[-]?[0-9]*\\.?[0-9]+");

            Matcher startMatcher = p.matcher(startPath);
            Matcher endMatcher = p.matcher(endPath);

            while (startMatcher.find() && endMatcher.find()) {
                startFloats.add(Float.parseFloat(startMatcher.group()));
                endFloats.add(Float.parseFloat(endMatcher.group()));
                pathHolder = p.matcher(pathHolder).replaceFirst("#");
            }
        }

        String evaluatedPath = pathHolder;

        for (int i = 0; i < startFloats.size(); i++) {

            float startFloat = startFloats.get(i);
            float value = startFloat + fraction * (endFloats.get(i) - startFloat);

            evaluatedPath = evaluatedPath.replaceFirst("#", String.valueOf(value));
        }

        return evaluatedPath;
    }

}
