package com.softwarelma.efe.server;

import java.util.ArrayList;
import java.util.List;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;

public class EfeServerFormula {

    // TODO list pre
    // TODO list post
    // TODO condition one
    // TODO list (for cond)

    private boolean mavenLike = true;
    // private final List<String> listPre;
    // private final String selector;
    // private final List<String> listText;
    private final String text;

    public EfeServerFormula(
            /* List<String> listPre, String selector, List<String> listText */String text) throws EpeAppException {
        // EpeAppUtils.checkEmptyList("listText", listText);
        // this.listPre = new ArrayList<>(listPre);
        // this.selector = selector;
        // this.listText = new ArrayList<>(listText);
        EpeAppUtils.checkEmpty("text", text);
        this.text = text;
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
