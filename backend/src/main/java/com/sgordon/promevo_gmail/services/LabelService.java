package com.sgordon.promevo_gmail.services;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.sgordon.promevo_gmail.exceptions.DuplicateLabelException;
import com.sgordon.promevo_gmail.exceptions.GMailAPIException;
import com.sgordon.promevo_gmail.exceptions.LabelNotFoundException;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class LabelService {
	private static final String USER_ID = "me";
	private final Gmail gmail;

	public LabelService(Gmail gmail) {
		this.gmail = gmail;
	}

	@Nonnull
	public List<LabelDTO> getAllLabels() {
		//Get all labels for the user
		ListLabelsResponse response = execute(() -> gmail.users().labels().list(USER_ID).execute());

		//Convert the GMail Labels for handling (empty list if null)
		List<Label> labels = Optional.ofNullable(response.getLabels()).orElse(List.of());
		return labels.stream().map(LabelDTO::new).toList();
	}


	@Nonnull
	public LabelDTO getLabel(String id) {
		try {
			//Get the requested label
			Label label = execute(() -> gmail.users().labels().get(USER_ID, id).execute());

			//Convert the GMail Label for handling
			return new LabelDTO(label);
		}
		catch (GMailAPIException e) {
			if(e.getStatus() == 404) throw new LabelNotFoundException(id);
			throw e;
		}
	}


	@Nonnull
	public LabelDTO createLabel(String name) {
		try {
			//Create a Label object using the provided name
			Label label = new Label().setName(name);

			//Add labelListVisibility and messageListVisibility
			label.setLabelListVisibility("labelShow");
			label.setMessageListVisibility("show");

			//Create the requested label
			Label created = execute(() -> gmail.users().labels().create(USER_ID, label).execute());

			//Convert the GMail Label for handling
			return new LabelDTO(created);
		}
		catch (GMailAPIException e) {
			if(e.getStatus() == 409) throw new DuplicateLabelException(name);
			throw e;
		}
	}


	@Nonnull
	public LabelDTO updateLabel(String id, String name) {
		try {
			//Create a Label object using the provided name
			Label label = new Label().setName(name);

			//Update the label
			Label updated = execute(() -> gmail.users().labels().patch(USER_ID, id, label).execute());

			//Convert the GMail Label for handling
			return new LabelDTO(updated);
		}
		catch (GMailAPIException e) {
			if(e.getStatus() == 404) throw new LabelNotFoundException(id);
			throw e;
		}
	}


	public void deleteLabel(String id) {
		try {
			//Delete the requested label
			execute(() -> gmail.users().labels().delete(USER_ID, id).execute());
		}
		catch (GMailAPIException e) {
			if(e.getStatus() == 404) throw new LabelNotFoundException(id);
			throw e;
		}
	}


	//-----------------------------------------------------------------------------------------------------------------

	//Centralizes Gmail SDK exception handling

	@FunctionalInterface
	private interface GmailCall<T> {
		T execute() throws IOException;
	}
	private <T> T execute(GmailCall<T> call) {
		try {
			return call.execute();
		}
		catch (GoogleJsonResponseException e) {
			throw new GMailAPIException(
					e.getStatusCode(),
					e.getDetails() != null
							? e.getDetails().getMessage()
							: "Google Gmail request failed.",
					e);
		}
		catch (IOException e) {
			throw new GMailAPIException(
					null,
					"Unable to communicate with Gmail.",
					e);
		}
	}
}
