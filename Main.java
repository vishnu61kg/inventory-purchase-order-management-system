import models.Product;
import models.Supplier;
import models.PurchaseOrder;
import services.InventoryService;
import services.SupplierService;
import services.PurchaseOrderService;

import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

public class Main {
    private static InventoryService inventoryService = new InventoryService();
    private static SupplierService supplierService = new SupplierService();
    private static PurchaseOrderService poService = new PurchaseOrderService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        seedData();
        
        while (true) {
            System.out.println("\n=== Inventory Purchase Order Management System ===");
            System.out.println("1. Product Management");
            System.out.println("2. Supplier Management");
            System.out.println("3. Purchase Order Management");
            System.out.println("4. Reports");
            System.out.println("5. Exit");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    productMenu();
                    break;
                case "2":
                    supplierMenu();
                    break;
                case "3":
                    purchaseOrderMenu();
                    break;
                case "4":
                    reportsMenu();
                    break;
                case "5":
                    System.out.println("Exiting system. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void productMenu() {
        while (true) {
            System.out.println("\n--- Product Management ---");
            System.out.println("1. Add Product");
            System.out.println("2. View All Products");
            System.out.println("3. Back to Main Menu");
            System.out.print("Select: ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    try {
                        System.out.print("Enter Product ID: ");
                        String id = scanner.nextLine();
                        System.out.print("Enter Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter Unit Price: ");
                        double price = Double.parseDouble(scanner.nextLine());
                        System.out.print("Enter Initial Stock: ");
                        int stock = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter Reorder Level: ");
                        int reorder = Integer.parseInt(scanner.nextLine());
                        
                        Product p = new Product(id, name, price, stock, reorder);
                        inventoryService.addProduct(p);
                        System.out.println("Product added successfully!");
                    } catch (Exception e) {
                        System.out.println("Error: Invalid input. " + e.getMessage());
                    }
                    break;
                case "2":
                    inventoryService.displayAllProducts();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void supplierMenu() {
        while (true) {
            System.out.println("\n--- Supplier Management ---");
            System.out.println("1. Add Supplier");
            System.out.println("2. View All Suppliers");
            System.out.println("3. Back to Main Menu");
            System.out.print("Select: ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    System.out.print("Enter Supplier ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Enter Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Contact Number: ");
                    String contact = scanner.nextLine();
                    
                    try {
                        Supplier s = new Supplier(id, name, contact);
                        supplierService.addSupplier(s);
                        System.out.println("Supplier added successfully!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case "2":
                    supplierService.displayAllSuppliers();
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void purchaseOrderMenu() {
        while (true) {
            System.out.println("\n--- Purchase Order Management ---");
            System.out.println("1. Create Purchase Order");
            System.out.println("2. View All Purchase Orders");
            System.out.println("3. Receive/Fulfill Order");
            System.out.println("4. Back to Main Menu");
            System.out.print("Select: ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    createPO();
                    break;
                case "2":
                    poService.displayAllOrders();
                    break;
                case "3":
                    System.out.print("Enter Order ID to fulfill: ");
                    String orderId = scanner.nextLine();
                    poService.fulfillOrder(orderId, inventoryService);
                    break;
                case "4":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    private static void createPO() {
        System.out.print("Enter Supplier ID for this order: ");
        String supplierId = scanner.nextLine();
        Optional<Supplier> optSupplier = supplierService.getSupplier(supplierId);
        
        if (!optSupplier.isPresent()) {
            System.out.println("Supplier not found!");
            return;
        }
        
        String orderId = "PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        PurchaseOrder po = new PurchaseOrder(orderId, optSupplier.get());
        
        while (true) {
            System.out.print("Enter Product ID to add to order (or 'done' to finish): ");
            String prodId = scanner.nextLine();
            if (prodId.equalsIgnoreCase("done")) {
                break;
            }
            
            Optional<Product> optProduct = inventoryService.getProduct(prodId);
            if (!optProduct.isPresent()) {
                System.out.println("Product not found!");
                continue;
            }
            
            try {
                System.out.print("Enter quantity: ");
                int qty = Integer.parseInt(scanner.nextLine());
                if (qty <= 0) {
                    System.out.println("Quantity must be positive.");
                    continue;
                }
                po.addItem(optProduct.get(), qty);
                System.out.println("Item added.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity.");
            }
        }
        
        if (!po.getItems().isEmpty()) {
            poService.createPurchaseOrder(po);
            System.out.println("Purchase Order created successfully: " + orderId);
        } else {
            System.out.println("Order creation cancelled. No items added.");
        }
    }

    private static void reportsMenu() {
        while (true) {
            System.out.println("\n--- Reports ---");
            System.out.println("1. Low Stock Report");
            System.out.println("2. Inventory Valuation");
            System.out.println("3. Back to Main Menu");
            System.out.print("Select: ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    System.out.println("--- Low Stock Products ---");
                    inventoryService.displayLowStockProducts();
                    break;
                case "2":
                    double totalValue = inventoryService.getAllProducts().stream()
                            .mapToDouble(p -> p.getUnitPrice() * p.getStockQuantity())
                            .sum();
                    System.out.printf("Total Inventory Value: $%.2f\n", totalValue);
                    break;
                case "3":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    // Seed some initial data for easier testing
    private static void seedData() {
        try {
            inventoryService.addProduct(new Product("P001", "Laptop", 899.99, 10, 5));
            inventoryService.addProduct(new Product("P002", "Mouse", 19.99, 50, 10));
            inventoryService.addProduct(new Product("P003", "Keyboard", 49.99, 3, 10)); // Low stock
            
            supplierService.addSupplier(new Supplier("S001", "TechSupplies Inc.", "123-456-7890"));
            supplierService.addSupplier(new Supplier("S002", "Global Electronics", "987-654-3210"));
        } catch (Exception e) {
            System.out.println("Error seeding data: " + e.getMessage());
        }
    }
}
