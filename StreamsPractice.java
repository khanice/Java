import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
public class StreamsPractice {

    record Product(String name, String category, double price, boolean inStock) {}
    record Order(String customer, String product, int quantity, double totalAmount) {}

    public static void main(String[] args) {

        List<Product> products = List.of(
            new Product("Laptop",      "Electronics", 85000, true),
            new Product("Phone",       "Electronics", 45000, false),
            new Product("Desk Chair",  "Furniture",   12000, true),
            new Product("Headphones",  "Electronics", 3500,  true),
            new Product("Bookshelf",   "Furniture",   8000,  false),
            new Product("Keyboard",    "Electronics", 2500,  true),
            new Product("Lamp",        "Furniture",   1500,  true),
            new Product("Tablet",      "Electronics", 32000, false)
        );

        List<Order> orders = List.of(
            new Order("Alice", "Laptop",     1, 85000),
            new Order("Bob",   "Keyboard",   2, 5000),
            new Order("Alice", "Headphones", 1, 3500),
            new Order("Carol", "Desk Chair", 1, 12000),
            new Order("Bob",   "Laptop",     1, 85000),
            new Order("Carol", "Lamp",       3, 4500),
            new Order("Alice", "Tablet",     2, 64000),
            new Order("Bob",   "Phone",      1, 45000)
        );

        // Q1: Get a list of all product names (as List<String>)
        System.out.println("Answer 1 ->"+products.stream().map(Product::name).collect(Collectors.toList()));

        // Q2: Apply 18% GST to every price (price * 1.18), rounded to 2 decimals
        System.out.println("Answer 2 ->"+products.stream()
    .map(prod -> Math.round(prod.price() * 1.18 * 100) / 100.0)
    .collect(Collectors.toList()));
    // Q3: Get all product names in UPPERCASE
        System.out.println("Answer 3 ->"+ products.stream().map(Product::name).map(String::toUpperCase).collect(Collectors.toList()));
    // Q4: Get all product names sorted alphabetically
        System.out.println("Answer 4 ->" + products.stream().map(Product::name).sorted().collect(Collectors.toList()));

    // Q5: product names sorted by price cheapest → most expensive
        System.out.println("Answer 5 ->" + products.stream().sorted(Comparator.comparingDouble(Product::price)).map(Product::name).collect(Collectors.toList()));
    
    // Q6: Get a distinct list of customers who placed orders (no duplicates)
     System.out.println("Answers 6 ->" + orders.stream().map(Order::customer).distinct().collect(Collectors.toList()));
    //Q7 Calculate the total value of ALL orders combined
    System.out.println("Answer 7 ->" + orders.stream().map(Order::totalAmount).reduce(0.0,Double::sum));

    //Q8 Count how many orders Alice placed
    System.out.println("Answer 8 ->" + orders.stream().filter(ord->"Alice".equals(ord.customer())).count());

    // Q9: most expensive product price using reduce()
    System.out.println("Answer 9 ->" +products.stream()
    .map(Product::price)
    .reduce(0.0, (a, b) -> a > b ? a : b));

    // Q10: group products by category → Map<String, List<Product>>
    System.out.println("Answer 10->" + products.stream().collect(Collectors.groupingBy(Product::category)));



    }
}