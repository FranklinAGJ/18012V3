import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Menu {
    private String[] pizzaMenu = {"Cheese Burst Pizza", "Veggie Pizza", "Paneer Pizza", "Pepperoni Pizza"};
    private double[] pizzaPrices = {250, 150, 200, 400};
    private String[] toppingsMenu = {"Mushrooms", "Onions", "Bell Peppers", "Olives", "Bacon"};
    private double[] toppingPrices = {20, 30, 25, 10, 50};

    public String[] getPizzaMenu() {
        return pizzaMenu;
    }

    public double getPizzaPrice(int pizzaIndex) {
        return pizzaPrices[pizzaIndex - 1];
    }

    public String[] getToppingsMenu() {
        return toppingsMenu;
    }

    public double getToppingPrice(int toppingIndex) {
        return toppingPrices[toppingIndex - 1];
    }
}

class OrderManager {
    private List<String> orderedPizzas = new ArrayList<>();
    private List<String> orderedSizes = new ArrayList<>();
    private List<Integer> quantities = new ArrayList<>();
    private List<List<String>> orderedToppings = new ArrayList<>();
    private List<Double> orderPrices = new ArrayList<>();

    public double processOrder(int pizzaChoice, String size, int quantity, Menu menu, Scanner scanner) {
        double pizzaPrice = menu.getPizzaPrice(pizzaChoice);
        double totalPrice = pizzaPrice * quantity;
        String pizzaName = menu.getPizzaMenu()[pizzaChoice - 1];

        List<String> toppings = new ArrayList<>();
        System.out.println("\n--- Available Toppings ---");
        String[] toppingsMenu = menu.getToppingsMenu();
        for (int i = 0; i < toppingsMenu.length; i++) {
            System.out.printf("%d. %s - INR %.2f%n", (i + 1), toppingsMenu[i], menu.getToppingPrice(i + 1));
        }

        while (true) {
            System.out.print("Choose a topping (or type 0 to finish): ");
            String input = scanner.nextLine();  // Read input as a string

           
            try {
                int toppingChoice = Integer.parseInt(input);

                if (toppingChoice == 0) {
                    break;  // Exit the loop if the user types 0
                }

                if (toppingChoice > 0 && toppingChoice <= toppingsMenu.length) {
                    toppings.add(toppingsMenu[toppingChoice - 1]);
                    totalPrice += menu.getToppingPrice(toppingChoice) * quantity;
                } else {
                    System.out.println("Invalid topping choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number or 0 to finish.");
            }
        }

        orderedPizzas.add(pizzaName);
        orderedSizes.add(size);
        quantities.add(quantity);
        orderedToppings.add(toppings);
        orderPrices.add(totalPrice);

        return totalPrice;
    }

    public void printOrderSummary() {
        for (int i = 0; i < orderedPizzas.size(); i++) {
            System.out.printf("Pizza: %s | Size: %s | Quantity: %d | Price: INR %.2f%n",
                    orderedPizzas.get(i),
                    orderedSizes.get(i),
                    quantities.get(i),
                    orderPrices.get(i));
            if (!orderedToppings.get(i).isEmpty()) {
                System.out.println("Toppings: " + String.join(", ", orderedToppings.get(i)));
            }
        }
    }

    public double calculateTotalBill() {
        double totalBill = 0;
        for (double price : orderPrices) {
            totalBill += price;
        }
        return totalBill;
    }

    public List<String> getOrderedPizzas() {
        return orderedPizzas;
    }

    public List<String> getOrderedSizes() {
        return orderedSizes;
    }

    public List<Integer> getQuantities() {
        return quantities;
    }

    public List<List<String>> getOrderedToppings() {
        return orderedToppings;
    }

    public List<Double> getOrderPrices() {
        return orderPrices;
    }
}

public class PizzaShop18012V3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (!username.equals("pizza") || !password.equals("pizza")) {
            System.out.println("Invalid username or password. Exiting...");
            return;
        }

        System.out.println("\n--- Welcome to Pizza Palace ---\n");

        Menu menu = new Menu();
        OrderManager orderManager = new OrderManager();

        while (true) {
            System.out.println("--- Pizza Menu ---");
            for (int i = 0; i < menu.getPizzaMenu().length; i++) {
                System.out.printf("%d. %s - INR %.2f%n", (i + 1), menu.getPizzaMenu()[i], menu.getPizzaPrice(i + 1));
            }
            System.out.println("6. Exit");

            System.out.print("Select a pizza by number or exit: ");
            int pizzaChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (pizzaChoice == 6) {
                System.out.println("\nExiting the program. Thank you for visiting Pizza Palace!");
                break;
            }

            if (pizzaChoice < 1 || pizzaChoice > 5) {
                System.out.println("Invalid choice. Please try again.");
                continue;
            }

            System.out.print("Choose pizza size (small/medium/large): ");
            String size = scanner.nextLine().toLowerCase();

            System.out.print("Enter the quantity: ");
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            double orderTotal = orderManager.processOrder(pizzaChoice, size, quantity, menu, scanner);

            System.out.printf("Price: INR %.2f%n", orderTotal);

            System.out.print("Would you like to order more? (yes/no): ");
            String choice = scanner.nextLine();
            if (!choice.equalsIgnoreCase("yes")) {
                System.out.print("\nEnter your name: ");
                String customerName = scanner.nextLine();

                String customerEmail;
                while (true) {
                    System.out.print("Enter your email: ");
                    customerEmail = scanner.nextLine();
                    if (isValidEmail(customerEmail)) {
                        break;
                    } else {
                        System.out.println("Invalid email. Please enter a valid email address.");
                    }
                }

                System.out.println("\n--- Your Order Summary ---");
                orderManager.printOrderSummary();
                System.out.println("\n--- Bill ---");
                System.out.printf("Customer Name: %s%n", customerName);
                System.out.printf("Email: %s%n", customerEmail);
                System.out.printf("Total Bill: INR %.2f%n", orderManager.calculateTotalBill());
                System.out.println("\nThank you for ordering from Pizza Palace!");

                // Insert order into the database
                insertOrderToDatabase(customerName, customerEmail, orderManager);

                break;
            }
        }

        scanner.close();
    }

 
    private static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    
    private static void insertOrderToDatabase(String customerName, String customerEmail, OrderManager orderManager) {
        try {
            // Load the MySQL JDBC driver explicitly
            Class.forName("com.mysql.cj.jdbc.Driver");

            String url = "jdbc:mysql://localhost:3306/pizzashop"; // Database URL
            String dbUsername = "root"; 
            String dbPassword = "";     

            
            try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                String query = "INSERT INTO orders (customer_name, customer_email, pizza_name, pizza_size, quantity, toppings, total_price) VALUES (?, ?, ?, ?, ?, ?, ?)";

                
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                  
                    for (int i = 0; i < orderManager.getOrderedPizzas().size(); i++) {
                        stmt.setString(1, customerName);
                        stmt.setString(2, customerEmail);
                        stmt.setString(3, orderManager.getOrderedPizzas().get(i));
                        stmt.setString(4, orderManager.getOrderedSizes().get(i));
                        stmt.setInt(5, orderManager.getQuantities().get(i));
                        stmt.setString(6, String.join(", ", orderManager.getOrderedToppings().get(i)));
                        stmt.setDouble(7, orderManager.getOrderPrices().get(i));
                        stmt.addBatch();
                    }

                    
                    stmt.executeBatch();
                    System.out.println("Order has been saved to the database.");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
