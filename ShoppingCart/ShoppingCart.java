import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import java.util.*;
import java.io.*;

/**
    This application works like a shopping cart for an online book store.
    The application uses the BookPrices.txt file that was provided from the
    textbook's source code. The file contains the names and prices of 
    various books.

    Each line in the file contains the name of a book, followed by a comma,
    followed by the book's retail price. When the application begins 
    execution, it will read the contents of the file and store the book titles
    in a ListView. The user should be able to select a title from the list and
    add it to a "shopping cart", which is simply another ListView control. The
    application has buttons that allow the user to remove items from the 
    shopping cart, clear the shopping cart of all selections and check out. 
    When the user checks out, the application calculates and displays the 
    subtotal of all the books in the shopping cart, the sales tax (which is
    7% of the subtotal), and the total cost. 

    *Note* When an item is removed from the cart, or the cart is cleared, the
    Subtotal will be automatically updated to reflect only the cost of the 
    items still in the cart. The Tax and Total will not be automatically 
    updated. The Tax and Total will be updated when the Checkout button is 
    selected.

    @author Jeremy Hill
    @version 1.8.0_271
 */
public class ShoppingCart extends Application 
{
	// ArrayList to hold the prices of the books & prices in your cart
    ArrayList<Double> priceList, cartPriceList;
    // Arraylist to hold the titles of the books & titles in your cart
    ArrayList<String> bookList, cartBookList;
    // ListView for all books & ListView for your shopping cart
    ListView<String> bookView, cartView;
    // Labels for the gui to help users know what is what
    Label bookLabel, subTotalLabel, taxLabel, 
    	  totalLabel, emptyLabel, cartLabel;
   	// Holds the subtotal of your cart, the total tax and the total cost
    double subTotal = 0, tax = 0, total = 0;


    /**
    * The main method calls the Application class launch
    * @param args the command line arguments
    */
    public static void main(String[] args) 
    {
        launch(args);
    }


    /**
    * The start method takes a Stage object as an argument. The start method
    * begins by calling the readBookFile method which reads the BookPrices.txt
    * file and saves the book's titles and prices into two ArrayLists. All of
    * the book titles will be displayed in a ListView for the user to select. 
    * Using different control buttons the user can add to, remove and clear
    * all books from their shopping cart (a second ListView). Once ready the 
    * user selects the checkout control button which calculates the tax and 
    * total cost of all the books currently in the user's shopping cart.
    * @param primaryStage Stage object to display scene
    * @throws IOException exception for calling readBookFile method
    */
    @Override
    public void start(Stage primaryStage) throws IOException 
    {
    	// ListView for all the books with SelectionMode MULTIPLE
        bookView = new ListView<String>();
        bookView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // ListView for books in the cart with SelectionMode MULTIPLE
        cartView = new ListView<String>();
        cartView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // ArrayList that holds the total cost of books in the cart
        cartPriceList = new ArrayList<Double>();
        // ArrayList that holds all of the book titles in the cart
        cartBookList = new ArrayList<String>();

        // Call the readBookFile method to get info from "BookPrices.txt"
        readBookFile();	

        // Create a button that adds selected books to the shopping cart
        Button addButton = new Button("Add To Cart");
        // Register an event handler for the addButton
        addButton.setOnAction(e -> 
        {
        	// Check if any of the books were selected from bookView
            if (bookView.getSelectionModel().getSelectedIndex() != -1) 
            {
            	// Holds the indices of selected book(s)
                ObservableList<Integer> selections = 
                			bookView.getSelectionModel().getSelectedIndices();

                // Loop through the selected books
                for (int index : selections)
                {
                	// Adds prices to the subtotal accumulator
                    subTotal += priceList.get(index);
                    // Adds the price to the cart price ArrayList
                    cartPriceList.add(priceList.get(index));
                    // Adds the book to the cart book ArrayList
                    cartBookList.add(bookList.get(index));
                }
                // Set the cart book ArrayList to the cart ListView
                cartView.getItems().setAll(cartBookList);
                // Update the subtotal Label
                subTotalLabel.setText(String.format("Subtotal: $%.2f",
                									 subTotal)); 
            }
        });

        // Create a button that removes selected books from the shopping cart
        Button removeButton = new Button("Remove From Cart");
        // Register an event handler for the removeButton
        removeButton.setOnAction(e -> 
        {
        	// Check if any of the books were selected from cartView
            if (cartView.getSelectionModel().getSelectedIndex() != -1) 
            {
            	// Holds the indices of selected book(s)
                ObservableList<Integer> selections = 
                			cartView.getSelectionModel().getSelectedIndices();

                // Removed books accumulator
                int booksRemoved = 0;  		
                // Loop through the selected books
                for (int index : selections)
                {
                	// Removes prices from the subtotal accumulator
                    subTotal -= cartPriceList.get(index - booksRemoved);
                    // Removes the price from the cart price ArrayList
                    cartPriceList.remove(index - booksRemoved);   
                    // Removes the book from the cart book ArrayList
                    cartBookList.remove(index - booksRemoved);
                    // Increment removed books accumulator
                    booksRemoved++;   		
                }
                // Set the cart book ArrayList to the cart ListView
                cartView.getItems().setAll(cartBookList);
                // Update the subtotal Label
                subTotalLabel.setText(String.format("Subtotal: $%.2f", 
                									 subTotal));
            }
        });
 
 		// Create a button that clears all books from the shopping cart
        Button clearButton = new Button("Clear Cart");
        // Register an event handler for the clearButton
        clearButton.setOnAction(e -> 
        {
        	// Set subtotal back to zero
            subTotal = 0;
            // Update the subtotal Label
            subTotalLabel.setText(String.format("Subtotal: $%.2f", subTotal));
            // Clear the cart ListView
            cartView.getItems().clear();
            // Clear the cart price ArrayList
            cartPriceList.clear();
            // Clear the cart book ArrayList
            cartBookList.clear();
        });

        // Create a button that calculates the tax and total of the cart
        Button checkButton = new Button("Checkout");
        // Register an event handler for the checkButton
        checkButton.setOnAction(e -> 
        {
        	// Holds the tax rate which is 7%
        	final double TAXRATEVALUE = 0.07;
        	// Holds the tax for your checkout
            tax = subTotal * TAXRATEVALUE;
            // Holds the total cost for your checkout
            total = subTotal + tax;
            // Update the subtotal, tax and total Labels
            subTotalLabel.setText(String.format("Subtotal: $%.2f", subTotal));
            taxLabel.setText(String.format("Tax: $%.2f", tax));
            totalLabel.setText(String.format("Total: $%.2f", total));
        });

        // Create the subtotal, tax and total Labels
        subTotalLabel = new Label("Subtotal:  $0.00");
        taxLabel = new Label("Tax:  $0.00");
        totalLabel = new Label("Total:  $0.00");
        // HBox holds the subtotal, tax and total Labels 
        HBox labelBox = new HBox(subTotalLabel, taxLabel, totalLabel);
        // Set HBox spacing
        labelBox.setSpacing(10);

        // Create bookView Label
        bookLabel = new Label("Pick a Book");
        // VBox holds bookView, it's Label & the label HBox
        VBox bookBox = new VBox(bookLabel, bookView, labelBox);
        // Set VBox spacing
        bookBox.setSpacing(10);

        // Create cartView Label & an empty Label for symmetry 
        emptyLabel = new Label(" ");
        cartLabel = new Label("Shopping Cart");
        // VBox holds cartView, it's Label & an empty Label
        VBox cartBox = new VBox(cartLabel, cartView, emptyLabel);
        // Set VBox spacing
        cartBox.setSpacing(10);

        // VBox holds all of the cartView control buttons
        VBox buttonBox = new VBox(removeButton, clearButton, checkButton);
        // Set VBox position and spacing
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);

        // root HBox holds all the other H/VBoxes and the addButton
        HBox root = new HBox(bookBox, addButton, cartBox, buttonBox);
        // Set root position, padding and spacing
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        // Create a scene and display it
        Scene mainScene = new Scene(root, 750, 450);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }


    
    // The readBookFile method uses a Scanner to read the BookPrices.txt file.
    // It saves the book titles into one ArrayList and the book prices into
    // another ArrayList. Then it passes the book title ArrayList into the 
    // bookView ListView which will display the book titles to the user.
    private void readBookFile() throws IOException 
    {
    	// Holds the prices of the books
        priceList = new ArrayList<Double>();
        // Holds the titles of the books
        bookList = new ArrayList<String>();
        // Holds the "BookPrices.txt" file
        File bookPrices = new File("BookPrices.txt");
        // Scanner to read the "BookPrices.txt" file
        Scanner input = new Scanner(bookPrices);

        // Check to see if the file has another line of text
        while (input.hasNext()) 
        {
        	// Holds the line of text currently being read
            String line = input.nextLine();
            // Get tokens using comma delimiter
            String[] tokens = line.split(",");
            // Holds the book's title
            String newBook = tokens[0].trim();
            // Holds the book's price
            double newPrice = Double.parseDouble(tokens[1].trim());
            // Add book to the book ArrayList
            bookList.add(newBook);
            // Add price to the price ArrayList
            priceList.add(newPrice);
        }
        // Add the book ArrayList to the book ListView
        bookView.getItems().setAll(bookList);
        // Close the file
        input.close();
    }
}