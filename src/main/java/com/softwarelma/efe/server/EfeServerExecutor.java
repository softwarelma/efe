package com.softwarelma.efe.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;
import com.softwarelma.epe.p2.encodings.EpeEncodingsResponse;
import com.softwarelma.epe.p3.disk.EpeDiskFinalFread;
import com.softwarelma.epe.p3.disk.EpeDiskFinalFread_encoding;
import com.softwarelma.epe.p3.disk.EpeDiskFinalFwrite;
import com.softwarelma.epe.p3.generic.EpeGenericFinalCalc;
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
        EfeServerSheet sheet = this.retrieveFakeSheet();
        EfeServerFormula formula = this.retrieveFakeFormula();
        EfeServerIndex index = new EfeServerIndex(sheet.getNumberOfLevels());
        EfeServerPoints points = this.execAll(sheet, formula, index);
        this.writePage(points, sheet);
        return points;
    }

    private void writePage(EfeServerPoints points, EfeServerSheet sheet) throws EpeAppException {
        EfeServerPoints borderPoints = this.retrieveBorderPoints(sheet);
        String borderPointsStr = borderPoints.getListPointAsString(0);
        EpeEncodingsResponse response = EpeDiskFinalFread_encoding.fRead(false, "templates/template-page.html", null);
        String template = response.getFileContent();
        template = EpeGenericFinalReplace.replace(true, template, Arrays.asList("w", "h", "borderPoints"),
                Arrays.asList(sheet.getWidth() + "", sheet.getHeight() + "", borderPointsStr));

        for (int i = 0; i < points.size(); i++) {
            String pointsStr = points.getListPointAsString(i);
            System.out.println(i + " = " + pointsStr);
            String polyling = this.retrievePolyline();
            polyling = EpeGenericFinalReplace.replace(true, polyling, "points", pointsStr);
            template = EpeGenericFinalReplace.replace(true, template, "polyline", polyling + "\n\t\t${polyline}");
        }

        template = EpeGenericFinalReplace.replace(true, template, "polyline", "");
        String filename = "pages/efe.html";
        EpeDiskFinalFwrite.fWrite(false, filename, template, response.getEncoding(), false);
    }

    private String retrievePolyline() {
        return "<polyline points='${points}' \n\t\t\tstyle='fill:none;stroke:red;stroke-width:2' />";
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

    private EfeServerSheet retrieveFakeSheet() throws EpeAppException {
        int[] arrayLevelCycle = new int[] { -1, 4, 2 };
        int[] arrayLevelEdge = new int[] { -1, 4, 2 };
        EfeServerPoint2D pointSize = new EfeServerPoint2D(800, 450);
        EfeServerSheet sheet = new EfeServerSheet(pointSize, arrayLevelCycle, arrayLevelEdge);
        return sheet;
    }

    private EfeServerFormula retrieveFakeFormula() throws EpeAppException {
        // String pre0 = "x = 10 \n"//
        // + "y = 190";
        // String pre1 = "x = 20 \n"//
        // + "y = 190";
        // List<String> listPre = Arrays.asList(pre0, pre1);
        // String selector = null;
        // String text = "x = ${-1.x} - ${-2.x} == 0 ? ${-1.x} + 10 : ${-1.x}
        // \n"//
        // + "y = ${-1.y} - ${-2.y} == 0 ? ${-1.y} - 10 : ${-1.y}";
        // List<String> listText = Arrays.asList(text);

        String text = "x = ${i.1} % 2 == 0 ? ${i.1} * 10 : ${i.1} * 10 + 10 \n"//
                + "y = ${i.1} % 2 == 1 ? ${i.1} * 10 : ${i.1} * 10 + 10 \n"//
                + "y = ${y} + ${i.2} * 30 \n";//
        // + "x = ${x} + ${i.3} * 150";
        EfeServerFormula formula = new EfeServerFormula(text);

        // new EfeServerFormula(listPre, selector, listText);
        return formula;
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
        EfeServerPoint2D point = this.execJS(sheet, formula, index, points);
        if (point == null)
            return false;
        points.add(point, index.isModuleAndIncrementLevel(sheet));
        if (sheet.isFinished(index))
            return false;
        return true;
    }

    private EfeServerPoint2D execJS(EfeServerSheet sheet, EfeServerFormula formula, EfeServerIndex index,
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
            listVarNameCalculated.add(varName);
            listVarValue.add(varValue);
        }

        EpeAppUtils.checkNull("x", x);
        EpeAppUtils.checkNull("y", y);
        point2D = new EfeServerPoint2D(x, y);
        return point2D;
    }

}
