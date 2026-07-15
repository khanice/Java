import java.util.List;
import java.util.stream.Collectors;

public class FilterPractice {

    record Product(String name, String category, double price, boolean inStock) {}

    public static void main(String[] args) {

        List<Product> products = List.of(
            new Product("Laptop",     "Electronics", 85000, true),
            new Product("Phone",      "Electronics", 45000, false),
            new Product("Desk Chair", "Furniture",   12000, true),
            new Product("Headphones", "Electronics", 3500,  true),
            new Product("Bookshelf",  "Furniture",   8000,  false),
            new Product("Keyboard",   "Electronics", 2500,  true),
            new Product("Lamp",       "Furniture",   1500,  true),
            new Product("Tablet",     "Electronics", 32000, false)
        );
        //Get All the products that are in stock
        // String result = products.stream()
        //         .filter(Product::inStock)
        //         .map(Product::name)
        //         .collect(Collectors.joining(", "));

        //Get All Electronics
         //String result =  products.stream().filter(prod->prod.category=="Electronics").map(Product::name).collect(Collectors.joining(", "));


        // Q3: Get Electronics that are in stock AND cost less than 10000.
        //String result = products.stream().filter(prod->prod.inStock() && prod.price()<10000).map(Product::name).collect(Collectors.joining(", "));

        // Q4: Get products whose name has more than 5 characters.
        //String result = products.stream().filter(prod-> prod.name().length()>5).map(Product::name).collect(Collectors.joining(", "));

        // Q5: Get in-stock products that are NOT Electronics.
        //String result = products.stream().filter(prod->prod.inStock() && prod.category() != "Electronics").map(Product::name).collect(Collectors.joining(", "));

        // Q6: Get products where price is between 5000 and 50000 (inclusive),
        //String result = products.stream().filter(prod->prod.price()>5000 && prod.price()<=50000).map(Product::name).collect(Collectors.joining(", "));

       // From in-stock Electronics only, collect just the names as a comma-separated string.
       String result = products.stream().filter(prod->prod.category()=="Electronics").map(Product::name).collect(Collectors.joining(", "));

        System.out.println("Result: " + result);
    }
}