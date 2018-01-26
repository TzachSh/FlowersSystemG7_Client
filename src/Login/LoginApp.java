package Login;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.stage.Stage;

public class LoginApp extends Application {

	 private static File f;
	 private static FileChannel channel;
     private static FileLock lock;
     public static void main(String[] args) {
		 try {
	            f = new File("RunLock.lock");
	            // Check if the lock exist
	            if (f.exists()) {
	                // if exist try to delete it
	                f.delete();
	            }
	            // Try to get the lock
	            channel = new RandomAccessFile(f, "rw").getChannel();
	            lock = channel.tryLock();
	            if(lock == null)
	            {
	                // File	 is lock by other application
	                channel.close();
	               
	              //default title and icon
	            	JOptionPane.showMessageDialog(null, "Only 1 instance of the application can run." ,"Error", JOptionPane.ERROR_MESSAGE);
	            	return;
	            }
	            // Add shutdown hook to release lock when application shutdown
	            ShutdownHook shutdownHook = new ShutdownHook();
	            Runtime.getRuntime().addShutdownHook(shutdownHook);
	 
	            //Starting the application
	    		launch(args);
	        }
	        catch(IOException e)
	        {
	            throw new RuntimeException("Error starting application process", e);
	        }
	    }
	 
		/***
		 * Unlock the application
		 */
	    public static void unlockFile() {
	        // release and delete file lock
	        try {
	            if(lock != null) {
	                lock.release();
	                channel.close();
	                f.delete();
	            }
	        } catch(IOException e) {
	            e.printStackTrace();
	        }
	    }
	 
	    /***
	     * 
	     * Thread to handle Unlocking 
	     *
	     */
	    static class ShutdownHook extends Thread {
	 
	        public void run() {
	            unlockFile();
	        }
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
