package com.softwarelma.efe.server;

import java.util.ArrayList;
import java.util.List;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;
import com.softwarelma.epe.p3.generic.EpeGenericFinalCalc;
import com.softwarelma.epe.p3.generic.EpeGenericFinalProp_text_to_list_list;
import com.softwarelma.epe.p3.generic.EpeGenericFinalReplace;

public class EfeServerExecuter {

    public EfeServerHistory start() throws EpeAppException {
        EfeServerSheet sheet = new EfeServerSheet();// TODO
        String text = null;// TODO
        EfeServerFormula formula = new EfeServerFormula(text);
        EfeServerState state = new EfeServerState();// TODO
        return this.execAll(sheet, formula, state);
    }

    private EfeServerHistory execAll(EfeServerSheet sheet, EfeServerFormula formula, EfeServerState state)
            throws EpeAppException {
        EfeServerHistory history = new EfeServerHistory();
        while (execOnce(sheet, formula, state, history))
            ;
        return history;
    }

    private boolean execOnce(EfeServerSheet sheet, EfeServerFormula formula, EfeServerState state,
            EfeServerHistory history) throws EpeAppException {
        EpeAppUtils.checkNull("sheet", sheet);
        EpeAppUtils.checkNull("formula", formula);
        EpeAppUtils.checkNull("state", state);
        EpeAppUtils.checkNull("history", history);
        String text = formula.getText();
        text = sheet.inject(text);
        text = state.inject(text);
        text = history.inject(text);
        EfeServerPoint2D point2D = execJS(text);
        if (point2D == null)
            return false;
        state.increment();
        history.add(point2D);
        return true;
    }

    private EfeServerPoint2D execJS(String text) throws EpeAppException {
        EpeAppUtils.checkNull("text", text);
        EfeServerPoint2D point2D;
        List<List<String>> listListStr = EpeGenericFinalProp_text_to_list_list.propTextToListList(text);
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
            while (partialFormula.contains("${"))
                partialFormula = EpeGenericFinalReplace.replace(true, partialFormula, listVarNameCalculated,
                        listVarValue);
            EpeAppUtils.checkNull("partialFormula", partialFormula);
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
