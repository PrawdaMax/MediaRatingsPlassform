package org.example;

import org.example.pkgDB.Database;
import org.example.pkgService.Service;
import org.example.pkgUI.Cli;

public class Main {
    public static void main(String[] args) {
        Database database = new Database();
        Service service = new Service(database);
        Cli cli = new Cli(service);

        cli.StartDemo();
    }
}