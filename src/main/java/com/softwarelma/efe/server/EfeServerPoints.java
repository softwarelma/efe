package com.softwarelma.efe.server;

import java.util.ArrayList;
import java.util.List;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;
import com.softwarelma.epe.p3.generic.EpeGenericFinalReplace;

public class EfeServerPoints {

    private final List<List<EfeServerPoint2D>> listListPoint = new ArrayList<>();

    @Override
    public String toString() {
        return this.listListPoint.toString();
    }

    public String getListPointAsString(int i) throws EpeAppException {
        EpeAppUtils.checkRange(i, 0, this.listListPoint.size(), false, true);
        List<EfeServerPoint2D> listPoint = this.listListPoint.get(i);
        StringBuilder sb = new StringBuilder();
        String sep = "";

        for (EfeServerPoint2D point : listPoint) {
            sb.append(sep);
            sep = " ";
            sb.append(point.getX());
            sb.append(",");
            sb.append(point.getY());
        }

        return sb.toString();
    }

    public int size() {
        return this.listListPoint.size();
    }

    public void add(EfeServerPoint2D point, boolean isModule) throws EpeAppException {
        EpeAppUtils.checkNull("point", point);
        List<EfeServerPoint2D> listPoint;

        if (isModule) {
            listPoint = new ArrayList<>();
            this.listListPoint.add(listPoint);
        } else {
            listPoint = this.getListPoint();
        }

        listPoint.add(point);
    }

    private List<EfeServerPoint2D> getListPoint() throws EpeAppException {
        EpeAppUtils.checkRange(this.listListPoint.size(), 1, this.listListPoint.size(), false, false);
        return this.listListPoint.get(this.listListPoint.size() - 1);
    }

    // TODO mavenLike
    public String inject(boolean mavenLike, String text) throws EpeAppException {
        EpeAppUtils.checkNull("text", text);
        int size = this.listListPoint.isEmpty() ? 0 : this.getListPoint().size();
        text = EpeGenericFinalReplace.replace(mavenLike, text, "s", size + "");

        for (int i = 0; i < size && this.containsPoint(text); i++) {
            EfeServerPoint2D point = this.getListPoint().get(i);
            text = EpeGenericFinalReplace.replace(mavenLike, text, i + ".x", point.getX() + "");
            text = EpeGenericFinalReplace.replace(mavenLike, text, "-" + (size - i) + ".x", point.getX() + "");
            text = EpeGenericFinalReplace.replace(mavenLike, text, i + ".y", point.getY() + "");
            text = EpeGenericFinalReplace.replace(mavenLike, text, "-" + (size - i) + ".y", point.getY() + "");
        }

        return text;
    }

    private boolean containsPoint(String text) {
        return text.contains(".x") || text.contains(".y");
        // || text.contains(".z");
    }

}
