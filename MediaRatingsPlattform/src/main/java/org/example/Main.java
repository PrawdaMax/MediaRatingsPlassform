package org.example;

import org.example.pkgDB.Database;
import org.example.pkgServer.Server;
import org.example.pkgService.Service;

/*
Junit Tests (verbessern und neue schreiben)
Put to Patch

Responses einheitlich
JSON Ausgabe (Passwörter, keine Status Codes gleich wie response message)

Token benutzung bei erstellung/bearbeitung

logging

mehr Klassen: Validators

Feedback Abgabe allgemein:
Controller ist context -> Pfad
controller unterscheidet zwischen methoden und weist zu

Controller getMediaEntry

Router -> Handler -> Controller
--------------------------
Datenbank
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