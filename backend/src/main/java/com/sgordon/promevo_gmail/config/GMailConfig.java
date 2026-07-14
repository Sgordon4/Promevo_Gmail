package com.sgordon.promevo_gmail.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.sgordon.promevo_gmail.services.GoogleAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GMailConfig {
	private final static NetHttpTransport HTTP = new NetHttpTransport();
	private final static JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

	@Bean
	public Gmail gmail(GoogleAuthService authService) throws IOException {
		Credential credential = authService.getCredential();

		return new Gmail.Builder(HTTP, JSON_FACTORY, credential)
				.setApplicationName("SGordon Promevo GMail")
				.build();
	}
}
