package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;

public class EfeServerFormula {

    private final String text;

    public EfeServerFormula(String text) throws EpeAppException {
        EpeAppUtils.checkNull("text", text);
        this.text = text;
    }

    public String getText() throws EpeAppException {
        return this.text;
    }

}
