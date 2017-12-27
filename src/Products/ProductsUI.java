package Products;


import javafx.application.Application;
import javafx.stage.Stage;

public class ProductsUI extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		// TODO Auto-generated method stub

		CatalogProductController productsController = new CatalogProductController();
		productsController.start(arg0);
	}
}
