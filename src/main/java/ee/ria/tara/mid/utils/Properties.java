package ee.ria.tara.mid.utils;

import java.util.logging.Logger;

public class Properties {

	static final Logger logger = Logger.getLogger(Properties.class.getName());

	private static String applicationId;
	private static String applicationSecret;
	private static String applicationUrl;
	private static String serviceProviderUrl;
	private static String openIdDiscoveryEndpoint;

	private Properties() {
	}

	public static String getApplicationId() {
		return applicationId;
	}

	public static void setApplicationId(String applicationId) {
		Properties.applicationId = applicationId;
	}

	public static String getApplicationSecret() {
		return applicationSecret;
	}

	public static void setApplicationSecret(String applicationSecret) {
		Properties.applicationSecret = applicationSecret;
	}

	public static String getServiceProviderUrl() {
		return serviceProviderUrl;
	}

	public static void setServiceProviderUrl(String serviceProviderUrl) {
		Properties.serviceProviderUrl = serviceProviderUrl;
	}

	public static String getApplicationUrl() {
		return applicationUrl;
	}

	public static void setApplicationUrl(String applicationUrl) {
		Properties.applicationUrl = applicationUrl;
	}

	public static void print() {
		final StringBuffer sb = new StringBuffer("Properties{");
		sb.append("applicationId='").append(applicationId).append('\'');
		sb.append(", applicationSecret='").append(applicationSecret).append('\'');
		sb.append(", applicationUrl='").append(applicationUrl).append('\'');
		sb.append(", serviceProviderUrl='").append(serviceProviderUrl).append('\'');
		sb.append(", openIdDiscoveryEndpoint='").append(openIdDiscoveryEndpoint).append('\'');
		sb.append('}');
		logger.info(sb.toString());
	}

	public static void setOpenIdDiscoveryEndpoint(String oidcDiscoveryEndpoint) {
		Properties.openIdDiscoveryEndpoint = oidcDiscoveryEndpoint ;
	}

	public static String getOpenIdDiscoveryEndpoint() {
		return openIdDiscoveryEndpoint;
	}
}
