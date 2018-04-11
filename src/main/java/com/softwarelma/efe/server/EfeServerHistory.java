package com.softwarelma.efe.server;

import java.util.ArrayList;
import java.util.List;

import com.softwarelma.epe.p1.app.EpeAppException;

public class EfeServerHistory {

    private final List<EfeServerPoint2D> listPoint2D = new ArrayList<>();

    public void add(EfeServerPoint2D point2D) throws EpeAppException {
        this.listPoint2D.add(point2D);
    }

    public String inject(String text) throws EpeAppException {
        // ${}, ${${}}, ..., while
        // history.size
        // history.3.x
        // v = ${history.size} - 1
        // v = ${v}
        // reverse: history.-1, history.-2
        return null;// TODO
    }

}
