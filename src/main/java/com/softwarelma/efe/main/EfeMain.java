package com.softwarelma.efe.main;

import com.softwarelma.efe.server.EfeServer;
import com.softwarelma.epe.p1.app.EpeAppException;

public class EfeMain {

    public static void main(String[] args) {
        EfeServer server = new EfeServer();
        try {
            server.start();
        } catch (EpeAppException e) {
        } catch (Exception e) {
            new EpeAppException(e.getClass().getName(), e);
        }
    }

}
