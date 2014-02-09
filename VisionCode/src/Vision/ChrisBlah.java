package Vision;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import edu.battlefield.vision.Base64Encoder;

public class ChrisBlah {
	
	private static final String urlString = "http://10.18.85.22/axis-cgi/jpg/image.cgi";
	private static final String user = "root";
	private static final String passwd = "team1885";
	private static URL url;
	
	static {


		try {
			url = new URL(urlString);
		}
		catch (MalformedURLException e) {
			System.err.println("Invalid URL");
			url = null;
		}
	}
	
	public static BufferedInputStream getConnection(URL pUrl) throws IOException {
		HttpURLConnection conn = (HttpURLConnection)pUrl.openConnection();
		Base64Encoder base64 = new Base64Encoder();
		conn.setRequestProperty("Authorization", "Basic " + base64.encode(user + ":" + passwd));
		final BufferedInputStream httpIn = new BufferedInputStream(conn.getInputStream(), 8192);
		
		return httpIn;
	}
	
	public static void main(String[] args) {

		
		

		try {
//			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//			conn.setRequestProperty("Authorization", "Basic " + base64.encode(user + ":" + passwd));
//			final BufferedInputStream httpIn = new BufferedInputStream(conn.getInputStream(), 8192);
//		
			final BufferedImage read = getFrame(getConnection(url));
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					JFrame aFrame = new JFrame();
					
					JPanel viewPanel = new JPanel();
					viewPanel.setLayout(new BorderLayout());
					final JLabel imageLabel = new JLabel(new ImageIcon(read));
					viewPanel.add(imageLabel, BorderLayout.CENTER);
					JButton aButton = new JButton("REFRESH");
					aButton.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							SwingWorker<BufferedImage, Void>aWorker = 
									new SwingWorker<BufferedImage, Void>() {
								@Override
								protected BufferedImage doInBackground()
										throws Exception {
									return getFrame(getConnection(url));
								}
								
								protected void done() {
									try {
										imageLabel.setIcon(new ImageIcon(get()));
									} catch (InterruptedException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (ExecutionException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								};
								
								
							};
							aWorker.execute();
						}
					});
					
					viewPanel.add(aButton, BorderLayout.SOUTH);
					
					aFrame.setContentPane(viewPanel);
					aFrame.pack();
					aFrame.setVisible(true);
					
					
					
				}
			});
		
		}
		catch (IOException e) {
			System.err.println("Unable to connect: " + e.getMessage());
			return;
		}
	}

	private static BufferedImage getFrame(BufferedInputStream httpIn)
			throws IOException {
		final BufferedImage read = ImageIO.read(httpIn);
		return read;
	}

}
