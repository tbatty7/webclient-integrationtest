package com.webclient.integrationtest.webclientintegrationtest;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@SpringBootTest
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("local")
@ContextConfiguration(classes = {WebclientIntegrationtestApplication.class, WebClientMockWebServerIntegrationTest.TestConfig.class})
class WebClientMockWebServerIntegrationTest {

	public static MockWebServer mockServer;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	JwtDecoder jwtDecoder;

	@BeforeAll
	static void beforeAll() throws IOException {
		mockServer = new MockWebServer();
		mockServer.start();
	}

	@AfterAll
	static void afterAll() throws IOException {
		mockServer.shutdown();
	}

	@Configuration
	public static class TestConfig {
		@Bean(name = "dmppsWebClient")
		WebClient dmppsWebClient() {
			HttpUrl url = mockServer.url("/");
			WebClient webClient = WebClient.create(url.toString());
			return webClient;
		}
	}
	@Test
	void contextLoads() {
	}

}
