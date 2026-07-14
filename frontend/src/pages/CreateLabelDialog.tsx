import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, TextField } from "@mui/material";

interface Props {
	open: boolean;
	//Allow callbacks to be sync or async
	onSubmit: (name: string) => void | Promise<void>;
	onClose: () => void;
}

interface CreateForm {
	name: string;
}

export default function CreateLabelDialog({ open, onSubmit, onClose }: Props) {
	const { register, handleSubmit, reset, formState:{ errors, isSubmitting } } = 
		useForm<CreateForm>({ defaultValues: { name:"" } });

	//Reset state each time the dialog is hidden
	useEffect(() => {
		if (!open) reset();
	}, [open, reset]);
	
	return (
		<Dialog
			open={open}
			onClose={onClose}
			fullWidth
		>

			<form onSubmit={ handleSubmit(({name}) => onSubmit(name)) }>
				<DialogTitle>Create Label</DialogTitle>

				<DialogContent>
					<TextField
						autoFocus
						fullWidth
						label="Name"
						{...register("name", { required: "Name is required" })}
						slotProps={{ htmlInput: { maxLength: 225 } }}	//GMail limits label size to 225
						error={!!errors.name}
						helperText={errors.name?.message}
					/>
				</DialogContent>

				<DialogActions>
					<Button
						onClick={onClose}
						disabled={isSubmitting}
					>
						Cancel
					</Button>

					<Button
						type="submit"
						loading={isSubmitting}
						variant="contained"
					>
						{isSubmitting ? "Creating..." : "Create"}
					</Button>
				</DialogActions>
			</form>

		</Dialog>
	);
}