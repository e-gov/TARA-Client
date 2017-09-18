package ee.ria.tara.mid.utils;

public class Properties {
	private static String applicationId;
	private static String applicationSecret;
	private static String serviceProviderUrl;
	private static String applicationUrl;

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
}
