package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;
import com.softwarelma.epe.p3.generic.EpeGenericFinalClean_comment;

public class EfeServerFormula {

    private boolean mavenLike = true;
    private boolean smoothing = true;// FIXME false
    private final String text;

    public EfeServerFormula(String text) throws EpeAppException {
        EpeAppUtils.checkEmpty("text", text);
        this.text = EpeGenericFinalClean_comment.cleanComment(text);
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

    public boolean isSmoothing() {
        return smoothing;
    }

    public void setSmoothing(boolean smoothing) {
        this.smoothing = smoothing;
    }

    public String getText() {
        return text;
    }

}
