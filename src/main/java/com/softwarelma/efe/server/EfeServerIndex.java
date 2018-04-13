package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;

public class EfeServerIndex {

    private final EfeServerLevels levelsState;

    public EfeServerIndex(int numberOfLevels) throws EpeAppException {
        this.levelsState = new EfeServerLevels(numberOfLevels);
    }

    public String inject(String text) throws EpeAppException {
        return null;// TODO
    }

    public void increment(EfeServerSheet sheet) throws EpeAppException {
        this.levelsState.increment(sheet);
    }

    public int getLevelState(int i) throws EpeAppException {
        return this.levelsState.getLevel(i);
    }

}
