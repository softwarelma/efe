package com.softwarelma.efe.server;

import java.util.ArrayList;
import java.util.List;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;
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
 * i: index
 * 
 * c: cycle
 * 
 * e: edge
 * 
 * s: size
 * 
 * m: enable maven-like, ${var} instead of var
 * 
 * !m: disable maven-like
 * 
 * p: enable printing
 * 
 * !p: disable printing
 */
public class EfeServerExecutor {

    public EfeServerPoints start() throws EpeAppException {
        EfeServerSheet sheet = this.retrieveFakeSheet();
        EfeServerFormula formula = this.retrieveFakeFormula();
        EfeServerIndex index = new EfeServerIndex(sheet.getNumberOfLevels());
        EfeServerPoints points = this.execAll(sheet, formula, index);
        System.out.println(points);
        return points;
    }

    private EfeServerSheet retrieveFakeSheet() throws EpeAppException {
        int[] arrayLevelCycle = new int[] { -1, 5 };
        int[] arrayLevelEdge = new int[] { 15, 5 };
        EfeServerPoint2D point = new EfeServerPoint2D(200, 200);
        EfeServerSheet sheet = new EfeServerSheet(point, arrayLevelCycle, arrayLevelEdge);
        return sheet;
    }

    private EfeServerFormula retrieveFakeFormula() throws EpeAppException {
        String pre = "x = 10 \n"//
                + "y = 190";
        String text = "x = ${-1.x} - ${-2.x} == 0 ? ${-1.x} + 10 : ${-1.x} \n"//
                + "y = ${-1.y} - ${-2.y} == 0 ? ${-1.y} - 10 : ${-1.y}";
        EfeServerFormula formula = new EfeServerFormula(pre, text);
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
        EfeServerPoint2D point2D = this.execJS(sheet, formula, index, points);
        if (point2D == null)
            return false;
        index.increment(sheet);
        points.add(point2D);
        if (sheet.isFinished(index))
            return false;
        return true;
    }

    private EfeServerPoint2D execJS(EfeServerSheet sheet, EfeServerFormula formula, EfeServerIndex index,
            EfeServerPoints points) throws EpeAppException {
        EfeServerPoint2D point2D;
        List<List<String>> listListStr = EpeGenericFinalProp_text_to_list_list
                .propTextToListList(formula.getTextByPhase());
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
            while (partialFormula.contains("${"))// TODO !maven?
                partialFormula = EpeGenericFinalReplace.replace(formula.isMavenLike(), partialFormula,
                        listVarNameCalculated, listVarValue);
            partialFormula = sheet.inject(formula.isMavenLike(), partialFormula);
            partialFormula = index.inject(formula.isMavenLike(), partialFormula);
            partialFormula = points.inject(formula.isMavenLike(), partialFormula);
            String varValue = EpeGenericFinalCalc.retrieveCalc(partialFormula);
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
