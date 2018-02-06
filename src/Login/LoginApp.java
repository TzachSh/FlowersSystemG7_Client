package Login;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class running application and check if no other instances of this
 * application running in the same time
 */
public class LoginApp extends Application {

	public static void main(String[] args) {
		// Starting the application
		launch(args);

	}

	/**
	 * Start login GUI
	 */
	@Override
	public void start(Stage arg0) throws Exception {
		LoginController loginController = new LoginController();
		loginController.start(arg0);
	}

}
