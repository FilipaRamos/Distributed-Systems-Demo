package Interface;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Browser extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public File[] files;

	public Browser() {
		super("File Chooser");
		setSize(320, 100);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container c = getContentPane();
		c.setLayout(new FlowLayout());

		JButton openButton = new JButton("Open");
		
		setVisible(true);

		// Create a file chooser that opens up as an Open dialog
		openButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent ae) {
				
				JFileChooser chooser = new JFileChooser();
				chooser.setMultiSelectionEnabled(true);
				
				int option = chooser.showOpenDialog(Browser.this);
				
				if (option == JFileChooser.APPROVE_OPTION) {
					@SuppressWarnings("unused")
					File[] sf = chooser.getSelectedFiles(); 
		
				} else {
				}
				
			}
			
		});

		c.add(openButton);
	}
}