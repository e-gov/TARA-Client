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
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import ee.ria.tara.mid.utils.Properties;
import ee.ria.tara.mid.utils.TlsSupport;

public final class Main {

	public static void main(String... a) throws Exception {
		Properties.setApplicationId("openIdDemo");
		Properties.setApplicationSecret("secret");
		Properties.setServiceProviderUrl("http://localhost:8080/oidc");
		Properties.setApplicationUrl("https://localhost:8451/oauth/response");

		Server server = new Server();
		createHttpsConnector(server);
		server.setHandler(getServletContextHandler(getContext()));
		server.start();
		server.join();
	}

	private static void createHttpsConnector(Server server) throws Exception {
		SslContextFactory cf = new SslContextFactory(false);
		cf.setWantClientAuth(true);
		cf.setSessionCachingEnabled(true);

		SSLContext ctx = SSLContext.getInstance("TLSv1.2");
		ctx.init(
				new KeyManager[] { TlsSupport.getKeyManager() },
				new TrustManager[] { TlsSupport.getTrustManager() },
				new SecureRandom()
		);
		cf.setSslContext(ctx);

		ServerConnector connector = new ServerConnector(server, cf);
		connector.setName("Https");
		connector.setPort(8451);
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

}
