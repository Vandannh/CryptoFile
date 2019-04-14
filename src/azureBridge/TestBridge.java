package azureBridge;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class TestBridge {

	public static void main(String[] args) {
		AzureFileshareIO io = new AzureFileshareIO();


		//		String message = "Ange följande: \n1. Connect\n2. Uppload\n3. Download\n0. Exit";
		//		
		//		Thread t = new Thread() {
		//			@Override 
		//			public void run() {
		//				int answer;
		//				answer = Integer.parseInt(JOptionPane.showInputDialog(message));
		//				while(answer != 0) {
		//					if(answer == 1) {
		//						io.connect();
		//					}
		//					if(answer == 2) {
		//						System.out.println("hello");
		//						io.upload();
		//					}
		//					if(answer == 3) {
		//						
		//					}
		//					answer = Integer.parseInt(JOptionPane.showInputDialog(message));
		//				}
		//			}
		//		};
		//
		//		t.start();

		io.connect();
		String file = JOptionPane.showInputDialog("Enter filename");
		io.download(file);


	}

}
