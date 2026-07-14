import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useForm } from "react-hook-form";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Box, Button, CircularProgress, Stack, Typography, TextField, Snackbar, Alert, type AlertColor } from "@mui/material";

import { deleteLabel, getLabel, updateLabel } from "../api/labels";
import DeleteConfirmDialog from "./DeleteConfirmDialog";
import type GMailLabel from "../api/GMailLabel";


export default function LabelDetailsPage() {
	const queryClient = useQueryClient();
	const navigate = useNavigate();

	//Grab the label id from the url
	const { id } = useParams();

	//Load the label data with tanstack
	const { data, isLoading, error } = useQuery({
		queryKey: ["label", id],
		queryFn: () => getLabel(id!),
		enabled: !!id,	//Don't allow fetch if id==undefined
	});


	//Only display the Delete Confirm dialog when true
	const [deleteOpen, setDeleteOpen] = useState(false);


	//Use a snackbar to show alerts for operation success/failure
	const [snackbar, setSnackbar] = useState({
		open: false,
		message: "",
		severity: "success" as AlertColor,
	});
	const showSnackbar = (severity: AlertColor, message: string) => {
		setSnackbar({ open: true, severity, message });
	};
	const closeSnackbar = () => 
		setSnackbar((current) => ({ ...current, open: false }));


	//Update the label, then invalidate our label cache
	const updateMutation = useMutation({
		mutationFn: updateLabel,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ["labels"] });
			queryClient.invalidateQueries({ queryKey: ["label", id] });

			showSnackbar("success", "Label updated successfully.");
		},
		onError: () => {
			showSnackbar("error", "Failed to update label.");
		}
	});

	//Delete the label, then invalidate our label cache and leave
	const deleteMutation = useMutation({
		mutationFn: deleteLabel,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ["labels"] });
			navigate("/labels");
		},
		onError: () => {
			showSnackbar("error", "Failed to delete label.");
		}
	});

	
	//---------------------------------------------------------

	const { register, handleSubmit, reset, formState:{ errors } } = useForm<GMailLabel>();
	
	useEffect(() => {
        if (data) reset(data);
    }, [data, reset]);

	
	if (error) return (
		<Alert severity="error" variant="filled"> 
			{error instanceof Error ? error.message : "An unexpected error occurred."}
		</Alert>);

	if (isLoading) return (
		<Box sx={{display:"flex", justifyContent:"center", py:6}}> 
			<CircularProgress /> 
		</Box>);

	if (data == null) return (
		<Box sx={{display:"flex", justifyContent:"center", py:6}}> 
			<Typography>Label not found.</Typography>
		</Box>);
	

	return (
		<Box sx={{ p: 2 }}>

			<Typography variant="h4" gutterBottom >
				Label Details
			</Typography>

			<form onSubmit={handleSubmit((label) => updateMutation.mutate(label))}>
                <Stack spacing={2}>
                    <TextField
                        label="ID"
						{...register("id")}
                        slotProps={{ input: { readOnly: true } }}
                    />

                    <TextField
                        label="Name"
                        {...register("name", { required: "Name is required" })}
						slotProps={{ htmlInput: { maxLength: 225 } }}	//GMail limits label size to 225
                        error={!!errors.name}
                        helperText={errors.name?.message}
                    />

                    <Button
                        type="submit"
                        loading={updateMutation.isPending}
                        variant="contained"
                    >
                        {updateMutation.isPending ? "Saving..." : "Save"}
                    </Button>
                </Stack>
            </form>

            <Stack direction="row" spacing={2} sx={{ justifyContent: "flex-end", mt: 2 }}>
                <Button
                    color="error"
                    variant="outlined"
                    onClick={() => setDeleteOpen(true)}
					disabled={deleteMutation.isPending || updateMutation.isPending}
                >
                    Delete
                </Button>

                <Button 
					onClick={() => navigate("/labels")}
					disabled={deleteMutation.isPending || updateMutation.isPending}
				>
                    Back
                </Button>
            </Stack>


			<DeleteConfirmDialog
				open={deleteOpen}
				labelName={data.name}
				loading={deleteMutation.isPending}
				onConfirm={async () => {
					await deleteMutation.mutateAsync(data.id);
					setDeleteOpen(false);
				}}
				onClose={() => setDeleteOpen(false)}
			/>


			<Snackbar
				open={snackbar.open}
				autoHideDuration={3000}
				onClose={closeSnackbar}
				anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
			>
				<Alert
					severity={snackbar.severity}
					variant="filled"
					onClose={closeSnackbar}
				>
					{snackbar.message}
				</Alert>
			</Snackbar>

		</Box>
	);
}