package com.softwarelma.efe.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.softwarelma.efe.main.EfeMainConstants;
import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;
import com.softwarelma.epe.p2.encodings.EpeEncodingsResponse;
import com.softwarelma.epe.p3.disk.EpeDiskFinalFread;
import com.softwarelma.epe.p3.disk.EpeDiskFinalFread_encoding;
import com.softwarelma.epe.p3.disk.EpeDiskFinalFwrite;
import com.softwarelma.epe.p3.generic.EpeGenericFinalCalc;
import com.softwarelma.epe.p3.generic.EpeGenericFinalClean_comment;
import com.softwarelma.epe.p3.generic.EpeGenericFinalProp_text_to_list_list;
import com.softwarelma.epe.p3.generic.EpeGenericFinalReplace;

/**
 * x: point.x
 * 
 * y: point.y
 * 
 * z: point.z
 * 
 * w: width
 * 
 * h: height
 * 
 * d: depth
 * 
 * i: index, TODO imaginary=
 * 
 * c: cycle
 * 
 * e: edge, TODO exponential?
 * 
 * s: size
 * 
 * m: enable maven-like, ${var} instead of var TODO
 * 
 * !m: disable maven-like TODO
 * 
 * p: enable printing TODO
 * 
 * !p: disable printing TODO
 */
public class EfeServerExecutor {

    public EfeServerPoints start() throws EpeAppException {
        EfeServerSheet sheet = this.retrieveSheet();
        EfeServerFormula formula = this.retrieveFormula();
        EfeServerIndex index = new EfeServerIndex(sheet.getNumberOfLevels());
        EfeServerPoints points = this.execAll(sheet, formula, index);
        this.writePage(points, sheet);
        return points;
    }

    private void writePage(EfeServerPoints points, EfeServerSheet sheet) throws EpeAppException {
        EfeServerPoints borderPoints = this.retrieveBorderPoints(sheet);
        String borderPointsStr = borderPoints.getListPointAsString(0);
        EpeEncodingsResponse response = EpeDiskFinalFread_encoding.fRead(false, EfeMainConstants.FILE_TEMPLATE_EFE_HTML,
                null);
        String template = response.getFileContent();
        template = EpeGenericFinalReplace.replace(true, template, Arrays.asList("w", "h", "borderPoints"),
                Arrays.asList(sheet.getWidth() + "", sheet.getHeight() + "", borderPointsStr));

        for (int i = 0; i < points.size(); i++) {
            String pointsStr = points.getListPointAsString(i);
            String polyling = this.retrievePolyline();
            polyling = EpeGenericFinalReplace.replace(true, polyling, "points", pointsStr);
            template = EpeGenericFinalReplace.replace(true, template, "polyline", polyling + "\n\t\t${polyline}");
        }

        template = EpeGenericFinalReplace.replace(true, template, "polyline", "");
        EpeDiskFinalFwrite.fWrite(false, EfeMainConstants.FILE_EFE_HTML, template, response.getEncoding(), false);
        System.out.println(points);
    }

    private String retrievePolyline() {
        return "<polyline points='${points}' \n\t\t\tstyle='fill:none;stroke:red;stroke-width:2;stroke-linejoin:round' />";
    }

    private EfeServerPoints retrieveBorderPoints(EfeServerSheet sheet) throws EpeAppException {
        EfeServerPoints borderPoints = new EfeServerPoints();
        borderPoints.add(new EfeServerPoint2D(0, 0), true);
        borderPoints.add(new EfeServerPoint2D(sheet.getWidth(), 0), false);
        borderPoints.add(new EfeServerPoint2D(sheet.getWidth(), sheet.getHeight()), false);
        borderPoints.add(new EfeServerPoint2D(0, sheet.getHeight()), false);
        borderPoints.add(new EfeServerPoint2D(0, 0), false);
        return borderPoints;
    }

    private EfeServerSheet retrieveSheet() throws EpeAppException {
        try {
            String text = EpeDiskFinalFread.fReadAsString(false, EfeMainConstants.FILE_SHEET, null);
            text = EpeGenericFinalClean_comment.cleanComment(text);
            List<List<String>> listListStr = EpeGenericFinalProp_text_to_list_list.propTextToListList(text);
            List<String> listKey = listListStr.get(0);
            List<String> listVal = listListStr.get(1);

            String c = listVal.get(listKey.indexOf("c"));
            String e = listVal.get(listKey.indexOf("e"));
            String w = listVal.get(listKey.indexOf("w"));
            String h = listVal.get(listKey.indexOf("h"));

            String[] arrayC = c.split("\\,");
            int[] arrayLevelCycle = new int[arrayC.length];
            for (int i = 0; i < arrayC.length; i++)
                arrayLevelCycle[i] = Integer.parseInt(EpeAppUtils.retrieveVisualTrim(arrayC[i]));

            String[] arrayE = e.split("\\,");
            int[] arrayLevelEdge = new int[arrayE.length];
            for (int i = 0; i < arrayE.length; i++)
                arrayLevelEdge[i] = Integer.parseInt(EpeAppUtils.retrieveVisualTrim(arrayE[i]));

            EfeServerPoint2D pointSize = new EfeServerPoint2D(Integer.parseInt(w), Integer.parseInt(h));
            EfeServerSheet sheet = new EfeServerSheet(pointSize, arrayLevelCycle, arrayLevelEdge);
            return sheet;
        } catch (Exception e) {
            throw new EpeAppException("retrieveSheet", e);
        }
    }

    private EfeServerFormula retrieveFormula() throws EpeAppException {
        try {
            String text = EpeDiskFinalFread.fReadAsString(false, EfeMainConstants.FILE_FORMULA, null);
            EfeServerFormula formula = new EfeServerFormula(text);
            return formula;
        } catch (Exception e) {
            throw new EpeAppException("retrieveFormula", e);
        }
    }

    private EfeServerPoints execAll(EfeServerSheet sheet, EfeServerFormula formula, EfeServerIndex index)
            throws EpeAppException {
        EfeServerPoints points = new EfeServerPoints();
        while (this.execOnce(sheet, formula, index, points))
            ;
        return points;
    }

    private boolean execOnce(EfeServerSheet sheet, EfeServerFormula formula, EfeServerIndex index,
            EfeServerPoints points) throws EpeAppException {
        EpeAppUtils.checkNull("sheet", sheet);
        EpeAppUtils.checkNull("formula", formula);
        EpeAppUtils.checkNull("index", index);
        EpeAppUtils.checkNull("points", points);
        EfeServerPoint2D point = this.execOnceJS(sheet, formula, index, points);
        EpeAppUtils.checkNull("point", point);
        points.add(point, index.getLevelIndex(1) == 0);
        return !index.incrementLevelAndIsFinished(sheet);
    }

    private EfeServerPoint2D execOnceJS(EfeServerSheet sheet, EfeServerFormula formula, EfeServerIndex index,
            EfeServerPoints points) throws EpeAppException {
        EfeServerPoint2D point2D;
        List<List<String>> listListStr = EpeGenericFinalProp_text_to_list_list.propTextToListList(formula.getText());// formula.getTextByPhase());
        List<String> listVarName = listListStr.get(0);
        List<String> listPartialFormula = listListStr.get(1);
        List<String> listVarNameCalculated = new ArrayList<>(listVarName.size());
        List<String> listVarValue = new ArrayList<>(listVarName.size());
        Integer x = null;
        Integer y = null;

        for (int i = 0; i < listVarName.size(); i++) {
            String varName = listVarName.get(i);
            EpeAppUtils.checkNull("varName", varName);
            String partialFormula = listPartialFormula.get(i);
            EpeAppUtils.checkNull("partialFormula", partialFormula);
            // while (partialFormula.contains("${"))// TODO !maven?
            partialFormula = EpeGenericFinalReplace.replace(formula.isMavenLike(), partialFormula,
                    listVarNameCalculated, listVarValue);
            partialFormula = sheet.inject(formula.isMavenLike(), partialFormula);
            partialFormula = index.inject(formula.isMavenLike(), partialFormula);
            partialFormula = points.inject(formula.isMavenLike(), partialFormula);
            // System.out.print(partialFormula + " --> ");// FIXME
            String varValue = EpeGenericFinalCalc.retrieveCalc(partialFormula);
            // System.out.println(varValue);// FIXME
            EpeAppUtils.checkNull("varValue", varValue);
            if (varName.equals("x"))
                x = EpeAppUtils.parseInt(varValue);
            if (varName.equals("y"))
                y = EpeAppUtils.parseInt(varValue);
            this.addVarNameAndValue(listVarNameCalculated, listVarValue, varName, varValue);
        }

        EpeAppUtils.checkNull("x", x);
        EpeAppUtils.checkNull("y", y);
        point2D = new EfeServerPoint2D(x, y);
        return point2D;
    }

    private void addVarNameAndValue(List<String> listVarNameCalculated, List<String> listVarValue, String varName,
            String varValue) {
        int ind = listVarNameCalculated.indexOf(varName);

        if (ind == -1) {
            listVarNameCalculated.add(varName);
            listVarValue.add(varValue);
        } else {
            listVarNameCalculated.set(ind, varName);
            listVarValue.set(ind, varValue);
        }
    }

}
