import { useState } from "react";
import { useNavigate } from "react-router-dom";

import {  useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { Box, Button, Stack, Typography, Snackbar, Alert, type AlertColor } from "@mui/material";
import { DataGrid, type GridColDef } from "@mui/x-data-grid";

import { createLabel, getAllLabels } from "../api/labels";
import type GMailLabel from "../api/GMailLabel";
import CreateLabelDialog from "./CreateLabelDialog";


export default function LabelsPage() {
	const queryClient = useQueryClient();
	const navigate = useNavigate();

	//Load the list of all labels with tanstack
	const { data = [], isLoading, error } = useQuery({
		queryKey: ["labels"],
		queryFn: getAllLabels,
	});


	//Only display the create Label dialog when true
	const [createOpen, setCreateOpen] = useState(false);


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
	

	//Create the label, then invalidate our labels cache
	const createMutation = useMutation({
		mutationFn: createLabel,
		onSuccess: () => {
			queryClient.invalidateQueries({ queryKey: ["labels"] });
		},
		onError: () => {
			showSnackbar("error", "Failed to create label.");
		}
	});
	

	if (error) return (
		<Alert severity="error" variant="filled"> 
			{error instanceof Error ? error.message : "An unexpected error occurred."}
		</Alert>)


	const columns: GridColDef<GMailLabel>[] = [
		{ field: "id", headerName: "ID", flex: 1 },
		{ field: "name", headerName: "Name", flex: 2 },
	];

	return (
		<Box sx={{ p: 2 }}>

			<Stack
				direction="row"
				sx={{ 
					justifyContent: "space-between",
					mb: 1.5 
				}} 
			>
				<Typography variant="h4">
					Gmail Labels
				</Typography>

				<Button
					variant="contained"
					onClick={() => setCreateOpen(true)}
				>
					Create Label
				</Button>

			</Stack>

			<DataGrid
				rows={data}
				columns={columns}
				loading={isLoading}
				autoHeight
				getRowId={(row) => row.id}
				//Navigate to the label details page on row click
				onRowClick={(params) =>
					navigate(`/labels/${params.row.id}`)
				}
			/>

			<CreateLabelDialog
				open={createOpen}
				onSubmit={ async (name) => {
					await createMutation.mutateAsync(name);
					setCreateOpen(false);
				}}
				onClose={() => setCreateOpen(false)}
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