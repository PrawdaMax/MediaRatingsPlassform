package org.example;

import org.example.pkgDB.Database;
import org.example.pkgService.Server;
import org.example.pkgService.Service;
import org.example.pkgUI.Cli;

public class Main {
    public static void main(String[] args) {
        try  {
            Database database = new Database();
            Service service = new Service(database);
            Server server = new Server(8080, service);
            Cli cli = new Cli(service);

            server.startServer();
            //cli.StartDemo();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}