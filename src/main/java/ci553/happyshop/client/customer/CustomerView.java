package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.utility.StorageLocation;
import java.nio.file.Paths;
import java.util.List;
import ci553.happyshop.utility.UIStyle;
import ci553.happyshop.utility.WinPosManager;
import ci553.happyshop.utility.WindowBounds;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * The CustomerView is separated into two sections by a line :
 *
 * 1. Search Page â€“ Always visible, allowing customers to browse and search for products.
 * 2. the second page â€“ display either the Trolley Page or the Receipt Page
 *    depending on the current context. Only one of these is shown at a time.
 */

public class CustomerView  {
    public CustomerController cusController;

    private final int WIDTH = 1000;
    private final int HEIGHT = 700;
    private final int COLUMN_WIDTH = WIDTH / 2 - 10;

    private HBox hbRoot; // Top-level layout manager
    private VBox vbTrolleyPage;  //vbTrolleyPage and vbReceiptPage will swap with each other when need
    private VBox vbReceiptPage;

    TextField tfId; //for user input on the search page. Made accessible so it can be accessed or modified by CustomerModel
    TextField tfKeyword; // part of the product description
                         // for user input on the search page.
                         // Optional extension: made accessible so it can be accessed by CustomerModel

    //four UI controls needs updating when program going on
    private FlowPane productImages;
    private ImageView ivProduct; //image area in searchPage
    private Label lbProductText;//product text info in searchPage
    private TextArea taTrolley; //in trolley Page
    private TextArea taReceipt;//in receipt page

    // Holds a reference to this CustomerView window for future access and management
    // (e.g., positioning the removeProductNotifier when needed).
    private Stage viewWindow;

    public void start(Stage window) {
        VBox vbSearchPage = createSearchPage();
        vbTrolleyPage = CreateTrolleyPage();
        vbReceiptPage = createReceiptPage();

        // DIVIDER LINE //
        // Changed thickness and colour of diver line
        Line line = new Line(0, 0, 0, HEIGHT);
        line.setStrokeWidth(20);
        line.setStroke(Color.ROYALBLUE);

        VBox lineContainer = new VBox(line);
        lineContainer.setPrefWidth(4); // Give it some space
        lineContainer.setAlignment(Pos.CENTER);

        // BACKGROUND and BORDER //
        //Added a border and set colour to match divider line
        //Changed background to light grey
        hbRoot = new HBox(10, vbSearchPage, lineContainer, vbTrolleyPage); //initialize to show trolleyPage
        hbRoot.setAlignment(Pos.CENTER);
        hbRoot.setStyle(
                "-fx-background-color: linear-gradient("
                        + "from 0% 0% to 100% 100%, white 0%, lightblue 100%);"
                + "; -fx-border-color: royalblue;"
                + "-fx-border-width: 10px;"
                + "-fx-padding: 10px;");

        Scene scene = new Scene(hbRoot, WIDTH, HEIGHT);
        window.setScene(scene);
        window.setTitle("HappyShop Standard Customer Client");
        WinPosManager.registerWindow(window,WIDTH,HEIGHT); //calculate position x and y for this window
        window.show();
        cusController.doAction("Load Products");
        viewWindow=window;// Sets viewWindow to this window for future reference and management.
    }

    private VBox createSearchPage() {


        // SEARCH PANEL LABEL //
        Label laPageTitle = new Label("Search Products");
        laPageTitle.setStyle(UIStyle.labelTitleStyle
                + "; -fx-text-fill: royalblue;");

        // SEARCH TEXT BOX //
        tfId = new TextField(); //Creates text box
        tfId.setStyle(UIStyle.customerTextFieldStyle);

        // SEARCH BUTTON //
        Button btnSearch = new Button("\uD83D\uDD0D");
        btnSearch.setStyle(UIStyle.buttonStyle
                + "; -fx-background-color: white;"
                + "; -fx-border-color: royalblue");
        btnSearch.setOnAction(event -> cusController.doAction("Search"));
        tfId.setOnAction(event -> cusController.doAction("Search"));
        tfId.setPromptText("Enter a product description");

        // SEARCH ROW LAYOUT //
        HBox hbId = new HBox(10, btnSearch, tfId); //Aligns Search button next to Search Text Box
        hbId.setAlignment(Pos.CENTER);



        Button btnAddToTrolley = new Button("Add to Trolley");
        btnAddToTrolley.setStyle(UIStyle.buttonStyle);
        btnAddToTrolley.setOnAction(this::buttonClicked);
        HBox hbBtns = new HBox(10, btnAddToTrolley);

        ivProduct = new ImageView();       // Creates the image display
        ivProduct.setFitHeight(60);        // Maximum displayed height: 60 pixels
        ivProduct.setFitWidth(60);         // Maximum displayed width: 60 pixels
        ivProduct.setPreserveRatio(true);  // Keeps the image's proportions
        ivProduct.setSmooth(true);         // Uses smoother image scaling

        //Removed "Thank you" message

        lbProductText = new Label("");
        lbProductText.setWrapText(true);
        lbProductText.setMinHeight(Label.USE_PREF_SIZE);
        lbProductText.setStyle(UIStyle.labelMulLineStyle);

        // Display the product image and information together
        HBox hbSearchResult = new HBox(5, ivProduct, lbProductText);
        hbSearchResult.setAlignment(Pos.CENTER_LEFT);

        productImages = new FlowPane(15, 15);
        productImages.setAlignment(Pos.TOP_CENTER);
        ScrollPane gallery = new ScrollPane(productImages);
        gallery.setFitToWidth(true);
        gallery.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(gallery, Priority.ALWAYS);

        VBox vbSearchPage = new VBox(15, laPageTitle, hbId, hbBtns, hbSearchResult, gallery);
        vbSearchPage.setPrefWidth(COLUMN_WIDTH);
        vbSearchPage.setAlignment(Pos.TOP_CENTER);
        vbSearchPage.setStyle("-fx-padding: 15px;");

        return vbSearchPage;
    }

    private VBox CreateTrolleyPage() {
        Label laPageTitle = new Label("Basket");
        laPageTitle.setStyle(UIStyle.labelTitleStyle
                + "; -fx-text-fill: royalblue;");;

        taTrolley = new TextArea();
        taTrolley.setEditable(false);
        taTrolley.setPrefSize(WIDTH/2, HEIGHT-50);

        Button btnCancel = new Button("Cancel");
        btnCancel.setOnAction(this::buttonClicked);
        btnCancel.setStyle(UIStyle.buttonStyle);

        Button btnCheckout = new Button("Check Out");
        btnCheckout.setOnAction(this::buttonClicked);
        btnCheckout.setStyle(UIStyle.buttonStyle);

        HBox hbBtns = new HBox(10, btnCancel,btnCheckout);
        hbBtns.setStyle("-fx-padding: 15px;");
        hbBtns.setAlignment(Pos.CENTER);

        vbTrolleyPage = new VBox(15, laPageTitle, taTrolley, hbBtns);
        vbTrolleyPage.setPrefWidth(COLUMN_WIDTH);
        vbTrolleyPage.setAlignment(Pos.TOP_CENTER);
        vbTrolleyPage.setStyle("-fx-padding: 15px;");
        return vbTrolleyPage;
    }

    private VBox createReceiptPage() {
        Label laPageTitle = new Label("Receipt");
        laPageTitle.setStyle(UIStyle.labelTitleStyle);

        taReceipt = new TextArea();
        taReceipt.setEditable(false);
        taReceipt.setPrefSize(WIDTH/2, HEIGHT-50);

        Button btnCloseReceipt = new Button("OK & Close"); //btn for closing receipt and showing trolley page
        btnCloseReceipt.setStyle(UIStyle.buttonStyle);

        btnCloseReceipt.setOnAction(this::buttonClicked);

        vbReceiptPage = new VBox(15, laPageTitle, taReceipt, btnCloseReceipt);
        vbReceiptPage.setPrefWidth(COLUMN_WIDTH);
        vbReceiptPage.setAlignment(Pos.TOP_CENTER);
        vbReceiptPage.setStyle(UIStyle.rootStyleYellow);
        return vbReceiptPage;
    }


    private void buttonClicked(ActionEvent event) {
        Button btn = (Button)event.getSource();
        String action = btn.getText();
        if(action.equals("Add to Trolley")){
            showTrolleyOrReceiptPage(vbTrolleyPage); //ensure trolleyPage shows if the last customer did not close their receiptPage
        }
        if(action.equals("OK & Close")){
            showTrolleyOrReceiptPage(vbTrolleyPage);
        }
        cusController.doAction(action);
    }


    public void update(String productImageName, String productTextInfo, String trolley, String receipt) {
        boolean hasImage = productImageName != null;

        ivProduct.setImage(hasImage ? new Image(productImageName) : null);
        ivProduct.setVisible(hasImage);
        ivProduct.setManaged(hasImage); // Removes the empty image space
        lbProductText.setText(productTextInfo);
        taTrolley.setText(trolley);
        if (!receipt.equals("")) {
            showTrolleyOrReceiptPage(vbReceiptPage);
            taReceipt.setText(receipt);
        }
    }

    // Replace the gallery whenever the model supplies a new product list.
    public void showProducts(List<Product> products) {
        productImages.getChildren().clear();
        for (Product product : products) {
            ImageView thumbnail = new ImageView();
            String filename = product.getProductImageName();
            if (filename != null && !filename.isBlank()) {
                String url = Paths.get(StorageLocation.imageFolder, filename)
                        .toAbsolutePath().toUri().toString();
                thumbnail.setImage(new Image(url));
            }
            thumbnail.setFitWidth(100);
            thumbnail.setFitHeight(100);
            thumbnail.setPreserveRatio(true);
            thumbnail.setSmooth(true);

            Button card = new Button(product.getProductDescription(), thumbnail);
            card.setContentDisplay(ContentDisplay.TOP);
            card.setWrapText(true);
            card.setPrefWidth(140);
            card.setMinHeight(140);
            card.setAccessibleText("Select " + product.getProductDescription());
            card.setOnAction(event -> cusController.selectProduct(product));
            productImages.getChildren().add(card);
        }
    }

    // Replaces the last child of hbRoot with the specified page.
    // the last child is either vbTrolleyPage or vbReceiptPage.
    private void showTrolleyOrReceiptPage(Node pageToShow) {
        int lastIndex = hbRoot.getChildren().size() - 1;
        if (lastIndex >= 0) {
            hbRoot.getChildren().set(lastIndex, pageToShow);
        }
    }

    WindowBounds getWindowBounds() {
        return new WindowBounds(viewWindow.getX(), viewWindow.getY(),
                  viewWindow.getWidth(), viewWindow.getHeight());
    }
}
