package com.softwarelma.efe.server;

import java.util.ArrayList;
import java.util.List;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;
import com.softwarelma.epe.p3.generic.EpeGenericFinalReplace;

public class EfeServerHistory {

    private final List<EfeServerPoint2D> listPoint2D = new ArrayList<>();

    public void add(EfeServerPoint2D point2D) throws EpeAppException {
        this.listPoint2D.add(point2D);
    }

    public String inject(String text) throws EpeAppException {
        EpeAppUtils.checkNull("text", text);
        text = EpeGenericFinalReplace.replace(true, text, "history.size", this.listPoint2D.size() + "");

        for (int i = this.listPoint2D.size() - 1; i >= 0 && text.contains("${history."); i--) {
            EfeServerPoint2D point = this.listPoint2D.get(i);
            text = EpeGenericFinalReplace.replace(true, text, "history." + i + ".x", point.getX() + "");
            text = EpeGenericFinalReplace.replace(true, text, "history.-" + (this.listPoint2D.size() - i) + ".x",
                    point.getX() + "");
            text = EpeGenericFinalReplace.replace(true, text, "history." + i + ".y", point.getY() + "");
            text = EpeGenericFinalReplace.replace(true, text, "history.-" + (this.listPoint2D.size() - i) + ".y",
                    point.getY() + "");
        }

        return text;
    }

}
