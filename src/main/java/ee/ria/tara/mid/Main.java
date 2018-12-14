package ee.ria.tara.mid;

import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import ee.ria.tara.mid.utils.Properties;
import ee.ria.tara.mid.utils.TlsSupport;

public final class Main {

	private enum Protocol {
		HTTP, HTTPS
	}

	private final static Integer port = 8451;
	private final static Integer providerPort = 8450;
	private final static Protocol providerProtocol = Protocol.HTTP;

	public static void main(String... a) throws Exception {
		Properties.setApplicationId(Main.getApplicationId());
		Properties.setApplicationSecret(Main.getApplicationSecret());
		Properties.setApplicationUrl(Main.getApplicationUrl());
		Properties.setServiceProviderUrl(Main.getServiceProviderUrl());
		Properties.print();
		Server server = new Server();
		createHttpsConnector(server);
		server.setHandler(getServletContextHandler(getContext()));
		server.start();
		server.join();
	}

	private static void createHttpsConnector(Server server) throws Exception {
		SslContextFactory cf = new SslContextFactory(false);
		cf.setWantClientAuth(false);
		cf.setSessionCachingEnabled(true);
		SSLContext ctx = SSLContext.getInstance("TLSv1.2");
		ctx.init(
				new KeyManager[] { TlsSupport.getKeyManager() },
				new TrustManager[] { TlsSupport.getTrustManager() },
				new SecureRandom()
		);
		cf.setSslContext(ctx);
		ServerConnector connector = new ServerConnector(server, cf);
		connector.setName("HTTPS");
		connector.setPort(Main.getPort());
		connector.setHost("0.0.0.0");
		server.addConnector(connector);
	}

	private static Handler getServletContextHandler(
			WebApplicationContext context) throws Exception {

		WebAppContext resourceContext = new WebAppContext();
		resourceContext.setContextPath("/ui");
		resourceContext.setResourceBase(
				new ClassPathResource("static").getURI().toString()
		);

		ServletContextHandler contextHandler = new ServletContextHandler();
		contextHandler.setErrorHandler(null);
		contextHandler.setSessionHandler(new SessionHandler());
		contextHandler.addServlet(
				new ServletHolder(new DispatcherServlet(context)), "/*"
		);
		contextHandler.addEventListener(new ContextLoaderListener(context));
		ContextHandler restContext = new ContextHandler();
		restContext.setHandler(contextHandler);

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		contexts.setHandlers(new Handler[] { resourceContext, restContext });
		return contexts;
	}

	private static WebApplicationContext getContext() {
		AnnotationConfigWebApplicationContext context =
				new AnnotationConfigWebApplicationContext();
		context.setConfigLocation("ee.ria.tara.mid");
		return context;
	}

	private static Integer getPort() {
		String port = System.getProperty("client.port");
		try {
			return Integer.parseInt(port);
		} catch (NumberFormatException e) {
			return Main.port;
		}
	}

	private static Integer getProviderPort() {
		String port = System.getProperty("provider.port");
		try {
			return Integer.parseInt(port);
		} catch (NumberFormatException e) {
			return Main.providerPort;
		}
	}

	private static String getApplicationId() {
		String id = System.getProperty("client.id");
		return (StringUtils.isEmpty(id)) ? "openIdDemo" : id;
	}

	private static String getApplicationSecret() {
		String secret = System.getProperty("client.secret");
		return (StringUtils.isEmpty(secret)) ? "secret" : secret;
	}

	private static String getApplicationUrl() {
		StringBuilder sb = new StringBuilder("https://");
		String domain = System.getProperty("client.domain");
		if (StringUtils.isEmpty(domain)) {
			sb.append("localhost");
		} else {
			sb.append(domain);
		}
		sb.append(":");
		sb.append(Main.getPort());
		sb.append("/oauth/response");
		return sb.toString();
	}

	private static String getServiceProviderUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(Main.getServiceProviderProtocol());
		sb.append("://");
		String domain = System.getProperty("provider.domain");
		if (StringUtils.isEmpty(domain)) {
			sb.append("localhost");
		} else {
			sb.append(domain);
		}
		sb.append(":");
		sb.append(Main.getProviderPort());
		sb.append("/oidc");
		return sb.toString();
	}

	private static String getServiceProviderProtocol() {
		String protocol = System.getProperty("provider.protocol");
		try {
			return Protocol.valueOf(protocol.toUpperCase()).name().toLowerCase();
		} catch (Exception e) {
			return Main.providerProtocol.name().toLowerCase();
		}
	}

}
