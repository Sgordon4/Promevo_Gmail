package com.sgordon.promevo_gmail;

import com.sgordon.promevo_gmail.controllers.LabelController;
import com.sgordon.promevo_gmail.exceptions.LabelNotFoundException;
import com.sgordon.promevo_gmail.services.LabelDTO;
import com.sgordon.promevo_gmail.services.LabelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Basic testing for the business rules in the controller layer
//The controller is so simple it doesn't really need this, but I wanted to get more familiar with Spring testing

@WebMvcTest(LabelController.class)
class LabelControllerTest {
	@Autowired
	MockMvc mockMvc;
	@MockitoBean
	LabelService labelService;


	@Test
	void getAllLabels() throws Exception {
		//Return a fake set of labels
		List<LabelDTO> labels = List.of(
				new LabelDTO("INBOX", "Inbox"),
				new LabelDTO("1", "Work")
		);
		when(labelService.getAllLabels()).thenReturn(labels);

		mockMvc.perform(get("/gmail/labels"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].id").value("INBOX"))
				.andExpect(jsonPath("$[0].name").value("Inbox"));

		verify(labelService).getAllLabels();
	}


	@Test
	void getLabel() throws Exception {
		//Return a fake label
		LabelDTO dto = new LabelDTO("abc123", "Projects");
		when(labelService.getLabel("abc123")).thenReturn(dto);

		mockMvc.perform(get("/gmail/labels/abc123"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("abc123"))
				.andExpect(jsonPath("$.name").value("Projects"));

		verify(labelService).getLabel("abc123");
	}


	@Test
	void getLabel404() throws Exception {
		when(labelService.getLabel("missing"))
				.thenThrow(new LabelNotFoundException("missing"));

		mockMvc.perform(get("/gmail/labels/missing"))
				.andExpect(status().isNotFound());
	}



	@Test
	void createLabel() throws Exception {
		//Return a fake label
		LabelDTO created = new LabelDTO("new-id", "Finance");
		when(labelService.createLabel("Finance")).thenReturn(created);

		//Use a valid label
		String label = "{\"name\":\"Finance\"}";

		mockMvc.perform(post("/gmail/labels")
						.contentType(MediaType.APPLICATION_JSON)
						.content(label))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value("new-id"))
				.andExpect(jsonPath("$.name").value("Finance"));

		verify(labelService).createLabel("Finance");
	}


	@Test
	void createLabelLengthJustRight() throws Exception {
		//Use a name greater than the 225-character limit
		String longName = "a".repeat(225);
		String label = "{\"name\":\""+longName+"\"}";

		//Return a fake label
		LabelDTO created = new LabelDTO("new-id", longName);
		when(labelService.createLabel(longName)).thenReturn(created);

		mockMvc.perform(post("/gmail/labels")
						.contentType(MediaType.APPLICATION_JSON)
						.content(label))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value("new-id"))
				.andExpect(jsonPath("$.name").value(longName));

		verify(labelService).createLabel(longName);
	}


	@Test
	void createLabelTooLong() throws Exception {
		//Use a name greater than the 225-character limit
		String longName = "a".repeat(226);
		String label = "{\"name\":\""+longName+"\"}";

		mockMvc.perform(post("/gmail/labels")
						.contentType(MediaType.APPLICATION_JSON)
						.content(label))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(labelService);
	}


	@Test
	void createLabelBlank() throws Exception {
		//Use a blank label name
		String label = "{\"name\":\"\"}";

		mockMvc.perform(post("/gmail/labels")
						.contentType(MediaType.APPLICATION_JSON)
						.content(label))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(labelService);
	}


	@Test
	void updateLabel() throws Exception {
		//Use a valid label name
		String label = "{\"name\":\"Updated\"}";

		//Return a fake label
		LabelDTO updated = new LabelDTO("123", "Updated");
		when(labelService.updateLabel("123", "Updated")).thenReturn(updated);

		mockMvc.perform(patch("/gmail/labels/123")
						.contentType(MediaType.APPLICATION_JSON)
						.content(label))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("123"))
				.andExpect(jsonPath("$.name").value("Updated"));

		verify(labelService).updateLabel("123", "Updated");
	}


	@Test
	void updateLabelBlank() throws Exception {
		//Use a blank label name
		String label = "{\"name\":\"\"}";

		mockMvc.perform(patch("/gmail/labels/123")
						.contentType(MediaType.APPLICATION_JSON)
						.content(label))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(labelService);
	}


	@Test
	void deleteLabel() throws Exception {
		mockMvc.perform(delete("/gmail/labels/123"))
				.andExpect(status().isNoContent());

		verify(labelService).deleteLabel("123");
	}
}