package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;
import com.softwarelma.epe.p1.app.EpeAppUtils;
import com.softwarelma.epe.p3.generic.EpeGenericFinalReplace;

public class EfeServerSheet {

    private final EfeServerPoint2D pointSize;
    private final EfeServerLevels levelsCycle;
    private final EfeServerLevels levelsEdge;

    @Override
    public String toString() {
        return "EfeServerSheet [\n\tpointSize=" + pointSize + ", \n\tlevelsCycle=" + levelsCycle + ", \n\tlevelsEdge="
                + levelsEdge + "]";
    }

    public EfeServerSheet(EfeServerPoint2D pointSize, int[] arrayLevelCycle, int[] arrayLevelEdge)
            throws EpeAppException {
        EpeAppUtils.checkNull("pointSize", pointSize);
        this.pointSize = pointSize;
        this.levelsCycle = new EfeServerLevels(EfeServerLevels.Type.cycle, arrayLevelCycle);
        this.levelsEdge = new EfeServerLevels(EfeServerLevels.Type.edge, arrayLevelEdge);
        EpeAppUtils.checkEquals("arrayLevelCycle.length", "arrayLevelEdge.length", arrayLevelCycle.length + "",
                arrayLevelEdge.length + "");
    }

    public String inject(boolean mavenLike, String text) throws EpeAppException {
        text = EpeGenericFinalReplace.replace(mavenLike, text, "w", this.pointSize.getX() + "");
        text = EpeGenericFinalReplace.replace(mavenLike, text, "h", this.pointSize.getY() + "");
        text = EfeServerIndex.inject(mavenLike, text, "c", this.levelsCycle);
        text = EfeServerIndex.inject(mavenLike, text, "e", this.levelsEdge);
        return text;
    }

    public boolean isFinished(EfeServerIndex index) throws EpeAppException {
        if (this.getLevelEdge(0) != -1 && index.getLevelIndex(0) >= this.getLevelEdge(0))
            return true;
        boolean finished = false;

        for (int i = this.getNumberOfLevels() - 1; i > 0; i--) {
            if (this.getLevelEdge(i) != -1) {
                if (index.getLevelIndex(i) < this.getLevelEdge(i)) {
                    return false;
                } else {
                    finished = true;
                }
            }
        }

        return finished;
    }

    public int getNumberOfLevels() {
        return this.levelsCycle.getNumberOfLevels();
    }

    public int getLevelCycle(int i) throws EpeAppException {
        return this.levelsCycle.getLevel(i);
    }

    public int getLevelEdge(int i) throws EpeAppException {
        return this.levelsEdge.getLevel(i);
    }

}
