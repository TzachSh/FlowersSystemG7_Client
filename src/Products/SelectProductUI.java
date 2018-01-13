package Products;

import javafx.application.Application;
import javafx.stage.Stage;

public class SelectProductUI extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		SelectProductController selectController = new SelectProductController();

		//selectController.setForUpdateCatalog();
		selectController.setForViewingCatalog();
		//selectController.setForUpdateSale();
		
		selectController.start(arg0);
	}
}
