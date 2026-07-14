package com.sgordon.promevo_gmail.controllers;

import com.sgordon.promevo_gmail.services.LabelDTO;
import com.sgordon.promevo_gmail.services.LabelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Scope: This implementation exposes only the label name during operations.
// The Gmail API also supports MessageListVisibility and LabelListVisibility, but these
// fields are intentionally omitted to keep the challenge focused on the core CRUD workflow.

@RestController
@RequestMapping("/gmail/labels")
public class LabelController {
	private final LabelService labelService;

	public LabelController(LabelService labelService) {
		this.labelService = labelService;
	}


	@GetMapping
	public List<LabelDTO> getAllLabels() {
		return labelService.getAllLabels();
	}


	@GetMapping("/{id}")
	public LabelDTO getLabel(@PathVariable @NotBlank String id) {
		return labelService.getLabel(id);
	}


	public record CreateLabelRequest(
			@NotBlank
			@Size(max = 225)    //GMail limits label size to 225
			String name
	) {}
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public LabelDTO createLabel(@RequestBody @Valid LabelController.CreateLabelRequest body) {
		return labelService.createLabel(body.name);
	}


	public record UpdateLabelBody(
			@NotBlank
			@Size(max = 225)
			String name
	) {}
	@PatchMapping("/{id}")
	public LabelDTO updateLabel(@PathVariable @NotBlank String id, @RequestBody @Valid UpdateLabelBody body) {
		return labelService.updateLabel(id, body.name);
	}


	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteLabel(@PathVariable @NotBlank String id) {
		labelService.deleteLabel(id);
	}
}
