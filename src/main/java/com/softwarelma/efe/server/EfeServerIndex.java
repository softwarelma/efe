package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;
import com.softwarelma.epe.p3.generic.EpeGenericFinalReplace;

public class EfeServerIndex {

    private final EfeServerLevels levelsIndex;

    public EfeServerIndex(int numberOfLevels) throws EpeAppException {
        this.levelsIndex = new EfeServerLevels(numberOfLevels);
    }

    @Override
    public String toString() {
        return levelsIndex.toString();
    }

    public String inject(boolean mavenLike, String text) throws EpeAppException {
        return inject(mavenLike, text, "i", this.levelsIndex);
    }

    public static String inject(boolean mavenLike, String text, String prefix, EfeServerLevels levels)
            throws EpeAppException {
        EpeAppUtils.checkNull("text", text);
        text = EpeGenericFinalReplace.replace(mavenLike, text, prefix + ".s", levels.getNumberOfLevels() + "");

        for (int i = 0; i < levels.getNumberOfLevels(); i++) {
            text = EpeGenericFinalReplace.replace(mavenLike, text, prefix + "." + i, levels.getLevel(i) + "");
            text = EpeGenericFinalReplace.replace(mavenLike, text, prefix + ".-" + (levels.getNumberOfLevels() - i),
                    levels.getLevel(i) + "");
        }

        return text;
    }

    public boolean isModuleAndIncrementLevel(EfeServerSheet sheet) throws EpeAppException {
        return this.levelsIndex.isModuleAndIncrementLevel(sheet);
    }

    public int getLevelIndex(int i) throws EpeAppException {
        return this.levelsIndex.getLevel(i);
    }

}
