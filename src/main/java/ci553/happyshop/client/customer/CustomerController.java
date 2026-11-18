package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;

public class CustomerController {
    public CustomerModel cusModel;

    public void doAction(String action) {
        switch (action) {
            case "Load Products":
                cusModel.loadProducts();
                break;
            case "Search":
                cusModel.search();
                break;
            case "Add to Trolley":
                cusModel.addToTrolley();
                break;
            case "Cancel":
                cusModel.cancel();
                break;
            case "Check Out":
                cusModel.checkOut();
                break;
            case "OK & Close":
                cusModel.closeReceipt();
                break;
        }
    }

    public void selectProduct(Product product) {
        cusModel.selectProduct(product);
    }
}
