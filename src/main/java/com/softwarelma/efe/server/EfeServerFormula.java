package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;

public class EfeServerFormula {

    // TODO once pre
    // TODO once post
    // TODO main one
    // TODO condition one
    // TODO list (for cond)

    private String phase;
    private boolean mavenLike = true;
    private final String pre;
    private final String text;

    public EfeServerFormula(String pre, String text) throws EpeAppException {
        EpeAppUtils.checkNull("text", text);
        this.pre = pre;
        this.text = text;
        this.phase = this.pre == null ? "text" : "pre";
    }

    public boolean isMavenLike() {
        return mavenLike;
    }

    public void setMavenLike(boolean mavenLike) {
        this.mavenLike = mavenLike;
    }

    public String getPre() {
        return this.pre;
    }

    public String getText() {
        return this.text;
    }

    public String getTextByPhase() {
        if (this.phase.equals("pre")) {
            this.phase = "text";
            return this.getPre();
        }

        return this.getText();
    }

}
