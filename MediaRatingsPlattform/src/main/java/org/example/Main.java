package org.example;

import org.example.pkgDB.Database;
import org.example.pkgService.Server;
import org.example.pkgService.Service;
import org.example.pkgUI.Cli;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        try  {
            Database database = new Database();
            Service service = new Service(database);
            Server server = new Server(8080, service);
            Cli cli = new Cli(service);

            server.start();
            //cli.StartDemo();

            UUID uuid = UUID.randomUUID();
            System.out.println(uuid.toString());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}