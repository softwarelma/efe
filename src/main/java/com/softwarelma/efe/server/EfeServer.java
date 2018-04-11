package com.softwarelma.efe.server;

import com.softwarelma.epe.p1.app.EpeAppException;

public class EfeServer {

    public void start() throws EpeAppException {
        EfeServerExecuter executer = new EfeServerExecuter();
        executer.start();
    }

}
