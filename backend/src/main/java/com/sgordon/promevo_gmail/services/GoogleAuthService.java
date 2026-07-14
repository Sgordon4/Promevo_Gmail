package com.sgordon.promevo_gmail.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.GmailScopes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleAuthService {
	private static final NetHttpTransport HTTP = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_LABELS);

	private final String credentialsFilepath;
	private final String tokensFilepath;

	//Get filepaths from application.properties
	//Use constructor injection for easier testing
	public GoogleAuthService(
			@Value("${gmail.credentials-file}") String credentialsFilepath,
			@Value("${gmail.token-directory}") String tokensFilepath) {
		this.credentialsFilepath = credentialsFilepath;
		this.tokensFilepath = tokensFilepath;
	}

	public Credential getCredential() throws IOException {
		//Parse the user's account credentials
		GoogleClientSecrets secrets = readCredentials();

		//Set up a flow for access/refresh token retrieval
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP, JSON_FACTORY, secrets, SCOPES)
				//Store the refresh token in the TOKENS_FILEPATH directory
				.setDataStoreFactory(new FileDataStoreFactory(new File(tokensFilepath)))
				.setAccessType("offline")
				.build();

		//google-oauth-client-jetty uses Jetty to receive authorization results
		//Jetty expects credential authorization to return to this receiver
		VerificationCodeReceiver receiver = new LocalServerReceiver.Builder()
				.setHost("localhost")
				.setPort(8888)
				.build();

		//Prompt an authorization screen for the user's account
		return new AuthorizationCodeInstalledApp(flow, receiver)
				.authorize("default");
	}

	private GoogleClientSecrets readCredentials() {
		//Parse the credentials from the credentials.json file
		Path credentialsPath = Paths.get(credentialsFilepath);
		try (InputStream in = Files.newInputStream(credentialsPath)) {
			return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
