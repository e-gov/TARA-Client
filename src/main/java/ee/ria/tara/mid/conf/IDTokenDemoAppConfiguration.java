package ee.ria.tara.mid.conf;

import ee.ria.tara.mid.EndpointDiscovery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
class IDTokenDemoAppConfiguration {

	@Bean
	EndpointDiscovery endpointDiscoveryTest() throws Exception {
		return new EndpointDiscovery();
	}

}
