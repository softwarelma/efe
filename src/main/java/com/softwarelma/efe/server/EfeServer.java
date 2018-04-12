package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;

public class EfeServer {

    public void start() throws EpeAppException {
        EfeServerExecutor executer = new EfeServerExecutor();
        executer.start();
    }

}
