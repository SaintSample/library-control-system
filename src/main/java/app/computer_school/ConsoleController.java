package app.computer_school;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ConsoleController
{
    public void run()
    {
        Scanner scanner = new Scanner(System.in);

        String welcome = """
        |---------------------------------------------------------------|
        |------------------Welcome to the club, buddy!------------------|
        |---------------------------------------------------------------|""";

        System.out.println(welcome);

        System.out.println("Menu:");

        String[] userItem = {"user", "User administration section"};
        String[] bookItem = {"book", "Book administration section"};

        ArrayList<String[]> menuItemsCollection = new ArrayList<>();
        menuItemsCollection.add(userItem);
        menuItemsCollection.add(bookItem);

        for (String[] item : menuItemsCollection) {
            System.out.println(String.format(
                    "%s:  %s",
                    item[0],
                    item[1]
            ));
        }

        System.out.println("|---------------------------------------------------------------|");

        System.out.print("Enter the command: ");

        String input = scanner.nextLine();

        if (!this.checkMenuItems(menuItemsCollection, input)) {
            System.out.println("Entered command is invalid: " + input);

            return;
        }

        System.out.println("Entered command: " + input);
    }

    private boolean checkMenuItems(ArrayList<String[]> menuItems, String searchItem)
    {
        for (String[] item : menuItems) {
            if (item[0].equals(searchItem)) {
                return true;
            }
        }

        return false;
    }
}
