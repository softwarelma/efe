package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;

public class EfeServerLevels {

    private final TYPE type;
    private final int[] arrayLevel;

    public enum TYPE {
        STATE, WINDOW, LIMIT
    }

    public EfeServerLevels(int numberOfLevels) throws EpeAppException {
        if (numberOfLevels < 1)
            throw new EpeAppException("The numberOfLevels is < 1: " + numberOfLevels);
        this.type = TYPE.STATE;
        this.arrayLevel = new int[numberOfLevels];
        for (int i = 0; i < this.arrayLevel.length; i++)
            this.arrayLevel[i] = -1;
    }

    public EfeServerLevels(TYPE type, int[] arrayLevel) throws EpeAppException {
        EpeAppUtils.checkNull("type", type);
        EpeAppUtils.checkNull("arrayLevel", arrayLevel);
        this.type = type;
        this.arrayLevel = new int[arrayLevel.length];
        this.validateNoState();
        for (int i = 0; i < arrayLevel.length; i++)
            this.arrayLevel[i] = arrayLevel[i];
    }

    private void validateNoState() throws EpeAppException {
        EpeAppUtils.checkNull("this.type", this.type.equals(TYPE.STATE) ? null : this.type);

        if (this.type.equals(TYPE.WINDOW)) {
            EpeAppUtils.checkEquals("this.arrayLevel[0]", "-1", this.arrayLevel[0] + "", "-1");
            for (int i = 0; i < this.arrayLevel.length; i++)
                if (this.arrayLevel[i] < 1)
                    throw new EpeAppException("Excepting the first, each window must be > 0");
        } else if (this.type.equals(TYPE.LIMIT)) {
            if (this.arrayLevel[0] == 0 || this.arrayLevel[0] < -1)
                throw new EpeAppException("The first limit must be -1 or > 0");
            for (int i = 0; i < this.arrayLevel.length; i++)
                if (this.arrayLevel[i] < 1)
                    throw new EpeAppException("Excepting the first, each limit must be > 0");
        } else {
            throw new EpeAppException("Unknown type: " + this.type);
        }
    }

    public boolean tryStart() {
        if (this.arrayLevel[0] == -1) {
            for (int i = 0; i < this.arrayLevel.length; i++)
                this.arrayLevel[i] = 0;
            return true;
        }

        return false;
    }

    public void increment(EfeServerSheet sheet) throws EpeAppException {
        EpeAppUtils.checkEquals("this.type", "TYPE.STATE", this.type, TYPE.STATE);
        if (this.tryStart())
            return;
        this.arrayLevel[0]++;
        for (int i = 1; i < this.arrayLevel.length; i++)
            if (!this.incrementLevelAndIsModule(sheet, i))
                return;
    }

    private boolean incrementLevelAndIsModule(EfeServerSheet sheet, int i) throws EpeAppException {
        this.arrayLevel[i]++;
        this.arrayLevel[i] = this.arrayLevel[i] % sheet.getLevelWindow(i);
        return this.arrayLevel[i] == 0;
    }

    public int getLevel(int i) throws EpeAppException {
        EpeAppUtils.checkRange(i, 0, this.arrayLevel.length, false, true, "level index", null);
        return this.arrayLevel[i];
    }

    public int getNumberOfLevels() {
        return this.arrayLevel.length;
    }

}
