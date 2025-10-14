package org.example;

import org.example.pkgServer.pkgToken.JWTUtil;
import org.example.pkgDB.Database;
import org.example.pkgServer.Server;
import org.example.pkgService.Service;
import org.example.pkgUI.Cli;

/*
Get Recomentations by Content
 */

public class Main {
    public static void main(String[] args) {
        try  {
            Database database = new Database();
            Service service = new Service(database);
            Server server = new Server(8080, service);

            server.start();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}