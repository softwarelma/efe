package com.softwarelma.efe.server;

import java.util.ArrayList;
import java.util.List;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;
import com.softwarelma.epe.p3.generic.EpeGenericFinalReplace;

public class EfeServerPoints {

    private final List<EfeServerPoint2D> listPoint2D = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String sep = "";

        for (EfeServerPoint2D point : this.listPoint2D) {
            sb.append(sep);
            sep = " ";
            sb.append(point.getX());
            sb.append(",");
            sb.append(point.getY());
        }

        return sb.toString();
    }

    public void add(EfeServerPoint2D point2D) throws EpeAppException {
        System.out.println("x=" + point2D.getX() + ", y=" + point2D.getY());
        this.listPoint2D.add(point2D);
    }

    // TODO mavenLike
    public String inject(boolean mavenLike, String text) throws EpeAppException {
        EpeAppUtils.checkNull("text", text);
        text = EpeGenericFinalReplace.replace(mavenLike, text, "s", this.listPoint2D.size() + "");

        for (int i = 0; i < this.listPoint2D.size() && this.containsPoint(text); i++) {
            EfeServerPoint2D point = this.listPoint2D.get(i);
            text = EpeGenericFinalReplace.replace(mavenLike, text, i + ".x", point.getX() + "");
            text = EpeGenericFinalReplace.replace(mavenLike, text, "-" + (this.listPoint2D.size() - i) + ".x",
                    point.getX() + "");
            text = EpeGenericFinalReplace.replace(mavenLike, text, i + ".y", point.getY() + "");
            text = EpeGenericFinalReplace.replace(mavenLike, text, "-" + (this.listPoint2D.size() - i) + ".y",
                    point.getY() + "");
        }

        return text;
    }

    private boolean containsPoint(String text) {
        return text.contains(".x") || text.contains(".y");
        // || text.contains(".z");
    }

}
