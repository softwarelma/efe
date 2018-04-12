package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;

public class EfeServerSheet {

    private final EfeServerLevels levelsWindow;
    private final EfeServerLevels levelsLimit;

    public EfeServerSheet(int[] arrayLevelWindow, int[] arrayLevelLimit) throws EpeAppException {
        this.levelsWindow = new EfeServerLevels(EfeServerLevels.TYPE.WINDOW, arrayLevelWindow);
        this.levelsLimit = new EfeServerLevels(EfeServerLevels.TYPE.LIMIT, arrayLevelLimit);
        EpeAppUtils.checkEquals("arrayLevelWindow.length", "arrayLevelLimit.length", arrayLevelWindow.length + "",
                arrayLevelLimit.length + "");
    }

    public String inject(String text) throws EpeAppException {
        return null;// TODO
    }

    public boolean isFinished(EfeServerState state) throws EpeAppException {
        if (this.getLevelLimit(0) != -1 && state.getLevelState(0) >= this.getLevelLimit(0))
            return true;
        boolean finished = false;

        for (int i = this.getNumberOfLevels() - 1; i > 0; i--) {
            if (this.getLevelLimit(i) != -1) {
                if (state.getLevelState(i) < this.getLevelLimit(i)) {
                    return false;
                } else {
                    finished = true;
                }
            }
        }

        return finished;
    }

    public int getNumberOfLevels() {
        return this.levelsWindow.getNumberOfLevels();
    }

    public int getLevelWindow(int i) throws EpeAppException {
        return this.levelsWindow.getLevel(i);
    }

    public int getLevelLimit(int i) throws EpeAppException {
        return this.levelsLimit.getLevel(i);
    }

}
