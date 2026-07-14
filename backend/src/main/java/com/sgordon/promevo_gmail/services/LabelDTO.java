package com.sgordon.promevo_gmail.services;

import com.google.api.services.gmail.model.Label;
import jakarta.validation.constraints.NotBlank;

public class LabelDTO {
	@NotBlank
	public final String id;
	@NotBlank
	public final String name;

	public LabelDTO(@NotBlank Label label) {
		this(label.getId(), label.getName());
	}
	public LabelDTO(@NotBlank String id, @NotBlank String name) {
		this.name = name;
		this.id = id;
	}
}
