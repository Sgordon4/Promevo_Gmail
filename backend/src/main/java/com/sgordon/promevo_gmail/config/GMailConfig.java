package com.sgordon.promevo_gmail.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.auth.Credentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.sgordon.promevo_gmail.services.GoogleAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

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
