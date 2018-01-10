package Products;



import javafx.application.Application;
import javafx.stage.Stage;

public class EditProductUI extends Application 
{
	public static void main(String[] args) {
		launch(args);
	}
	
	
	@Override
	public void start(Stage arg0) throws Exception
	{
		CatalogProductController catalogProductController = new CatalogProductController();
		catalogProductController.setCatalogProductForInserting();
		catalogProductController.start(arg0);
	}
}
