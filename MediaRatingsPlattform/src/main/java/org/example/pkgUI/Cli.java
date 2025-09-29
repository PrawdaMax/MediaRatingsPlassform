package org.example.pkgUI;
import java.util.List;
import java.util.Scanner;

import org.example.pkgObj.Rating;
import org.example.pkgService.Service;

//Apache Commons CLI

public class Cli {
    Service service;
    Scanner sc = new Scanner(System.in);

    public Cli (Service service) {
        this.service = service;
    }

    public void StartDemo() {
        System.out.println("Starting Demo...");
        while (true) {
            System.out.println("Add User...1");
            System.out.println("Delete User...2");
            System.out.println("Login...3");
            System.out.println("End...0");

            int choice = Integer.parseInt(sc.nextLine());
            switch(choice) {
                case 1:
                    addUser();
                    break;
                case 2:
                    DeleteUser();
                    break;
                case 3:
                    Login();
                    break;
                case 0:
                    return;
            }
        }
    }

    private void addUser() {
        System.out.println("Enter user name: ");
        String Name = sc.nextLine();
        System.out.println("Enter user password: ");
        String Password = sc.nextLine();
        service.addUser(Name, Password);
        System.out.println("User successfully added!");
    }

    private void DeleteUser() {
        System.out.println("Enter user name: ");
        String Name = sc.nextLine();
        System.out.println("Enter user password: ");
        String Password = sc.nextLine();
        service.deleteUser(Name, Password);
        System.out.println("User has been deleted.");
    }

    private void Login() {
        System.out.println("Enter user name: ");
        String Name = sc.nextLine();
        System.out.println("Enter user password: ");
        String Password = sc.nextLine();

        if (service.checkUser(Name, Password)) {
            while (true) {
                System.out.println("View All Media...1");
                System.out.println("Add Media...2");
                System.out.println("View User Ratings...3");
                System.out.println("Write Rating...4");
                System.out.println("Return...0");

                int choice = Integer.parseInt(sc.nextLine());
                switch(choice) {
                    case 1:
                        ViewAllMedia();
                        break;
                    case 2:
                        AddMedia(Name);
                        break;
                    case 3:
                        ViewUserRatings(Name, Password);
                        break;
                    case 4:
                        WriteRating(Name);
                        break;
                    case 0:
                        return;
                }
            }
        } else {
            System.out.println("Invalid username or password!");
        }
    }

    public void ViewAllMedia() {
        System.out.println("--------------");
        List<String> MediaNames = service.getMediaNames();
        System.out.println("All Media:");
        for (String name : MediaNames) {
            System.out.println(name);
            System.out.println("--------------");
        }
    }

    public void AddMedia(String Name) {
        System.out.println("Enter media name: ");
        String MediaName = sc.nextLine();
        System.out.println("Enter media Type: ");
        String MediaType = sc.nextLine();

        service.AddMedia(Name, MediaName, MediaType);
    }

    public void ViewUserRatings(String Name, String Password) {
        List<Rating> userRatings = service.getUserRatings(Name, Password);

        System.out.println("User ratings:");
        System.out.println("--------------");
        for (Rating rating : userRatings) {
            System.out.print("Media: ");
            System.out.println(rating.getMedianame());
            System.out.print("Your Comment: ");
            System.out.println(rating.getComment());
            System.out.println("--------------");
        }
    }

    public void WriteRating(String Name) {
        System.out.println("Media Name: ");
        String MediaName = sc.nextLine();
        System.out.println("Rating: ");
        int Rating = Integer.parseInt(sc.nextLine());
        System.out.println("Comment: ");
        String Comment = sc.nextLine();
        service.addRating(Name, MediaName, Rating, Comment);
    }
}
