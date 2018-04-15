package com.softwarelma.efe.server;

import java.util.Arrays;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;

public class EfeServerLevels {

    private final Type type;
    private final int[] arrayLevel;

    public enum Type {
        index, cycle, edge
    }

    public EfeServerLevels(int numberOfLevels) throws EpeAppException {
        if (numberOfLevels < 1)
            throw new EpeAppException("The numberOfLevels is < 1: " + numberOfLevels);
        this.type = Type.index;
        this.arrayLevel = new int[numberOfLevels];
        // for (int i = 0; i < this.arrayLevel.length; i++)
        // this.arrayLevel[i] = -1;
    }

    @Override
    public String toString() {
        return "[type=" + type + ", arrayLevel=" + Arrays.toString(arrayLevel) + "]";
    }

    public EfeServerLevels(Type type, int[] arrayLevel) throws EpeAppException {
        EpeAppUtils.checkNull("type", type);
        EpeAppUtils.checkNull("arrayLevel", arrayLevel);
        this.type = type;
        this.arrayLevel = new int[arrayLevel.length];
        // validateNoIndex(this.type, arrayLevel);
        for (int i = 0; i < arrayLevel.length; i++)
            this.arrayLevel[i] = arrayLevel[i];
    }

    // TODO ?
    private static void validateNoIndex(Type type, int[] arrayLevel) throws EpeAppException {
        EpeAppUtils.checkNull("type", type.equals(Type.index) ? null : type);

        if (type.equals(Type.cycle)) {
            EpeAppUtils.checkEquals("arrayLevel[0] (" + arrayLevel[0] + ")", "-1", arrayLevel[0] + "", "-1");
            for (int i = 1; i < arrayLevel.length; i++)
                if (arrayLevel[i] < 1)
                    throw new EpeAppException(
                            "Excepting the first, each cycle must be > 0. Found c." + i + " = " + arrayLevel[i]);
        } else if (type.equals(Type.edge)) {
            if (arrayLevel[0] == 0 || arrayLevel[0] < -1)
                throw new EpeAppException("The first edge must be -1 or > 0. Found e.0 = " + arrayLevel[0]);
            for (int i = 1; i < arrayLevel.length; i++)
                if (arrayLevel[i] < 1)
                    throw new EpeAppException(
                            "Excepting the first, each edge must be > 0. Found e." + i + " = " + arrayLevel[i]);
        } else {
            throw new EpeAppException("Unknown type: " + type);
        }
    }

    // public boolean tryStart() {
    // if (this.arrayLevel[0] == -1) {
    // for (int i = 0; i < this.arrayLevel.length; i++)
    // this.arrayLevel[i] = 0;
    // return true;
    // }
    //
    // return false;
    // }

    public void checkTypeIndex() throws EpeAppException {
        EpeAppUtils.checkEquals("this.type", Type.index.toString(), this.type, Type.index);
    }

    public boolean isModuleAndIncrementLevel(EfeServerSheet sheet) throws EpeAppException {
        this.checkTypeIndex();
        // if (this.tryStart())
        // return true;
        this.arrayLevel[0]++;
        boolean isModule = false;

        for (int i = 1; i < this.arrayLevel.length; i++) {
            if (this.isModuleAndIncrementLevel(sheet, i)) {
                isModule = true;
            } else {
                return isModule;
            }
        }

        return true;
    }

    private boolean isModuleAndIncrementLevel(EfeServerSheet sheet, int i) throws EpeAppException {
        boolean isModule = this.arrayLevel[i] == 0;
        this.arrayLevel[i]++;
        this.arrayLevel[i] = this.arrayLevel[i] % (sheet.getLevelCycle(i) + 1);
        return isModule;
    }

    public int getLevel(int i) throws EpeAppException {
        EpeAppUtils.checkRange(i, 0, this.arrayLevel.length, false, true, "level index", null);
        return this.arrayLevel[i];
    }

    public int getNumberOfLevels() {
        return this.arrayLevel.length;
    }

}
