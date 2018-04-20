package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;

public class EfeServerFormula {

    private boolean mavenLike = true;
    private final String text;

    public EfeServerFormula(String text) throws EpeAppException {
        EpeAppUtils.checkEmpty("text", text);
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }

    public boolean isMavenLike() {
        return mavenLike;
    }

    public void setMavenLike(boolean mavenLike) {
        this.mavenLike = mavenLike;
    }

    public String getText() {
        return text;
    }

    // public String getSelector() {
    // return selector;
    // }
    //
    // public String getTextByPhase() throws EpeAppException {
    // return EpeAppUtils.isEmptyList(this.listPre) ? this.listText.get(0) :
    // this.listPre.remove(0);
    // }

}
