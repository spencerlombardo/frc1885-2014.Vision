package Vision;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageNetworkConnection {
	private static final String user = "root";
	private static final String passwd = "team1885";
	private URL mUrl;
	private final Base64Encoder base64 = new Base64Encoder();
	private BufferedInputStream httpIn;

	private boolean grabImage = true;
	
	HttpURLConnection conn = null;

	public ImageNetworkConnection(String pURL) throws IOException {

		mUrl = new URL(pURL);
		
		conn = (HttpURLConnection)mUrl.openConnection();
		conn.setRequestProperty("Authorization", "Basic " + base64.encode(user + ":" + passwd));

		httpIn = new BufferedInputStream(conn.getInputStream());

	}

	public BufferedImage grabImage() throws IOException {
		try {
			BufferedImage read = ImageIO.read(httpIn);
			return read;
		} finally {

			if(conn != null) {
				conn.disconnect();
			}

			if(httpIn != null) {
				try {
					httpIn.close();
				} catch(IOException e) {
					System.err.println("Unable to close httpIn, probably not a problem, just thought you should know");
				}
			}
		}
	}

}
