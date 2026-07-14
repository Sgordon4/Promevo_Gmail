import { Dialog, DialogTitle, DialogContent, DialogActions, Button } from "@mui/material";

interface Props {
    open: boolean;
    labelName: string;
    loading: boolean;
    onConfirm: () => void;
    onClose: () => void;
}

export default function DeleteConfirmDialog({ open, labelName, loading, onConfirm, onClose }: Props) {
    return (
        <Dialog
            open={open}
            onClose={loading ? undefined : onClose}
        >
            <DialogTitle>Delete Label</DialogTitle>

            <DialogContent>
                Are you sure you want to delete <strong>{labelName}</strong>?
                This action cannot be undone.
            </DialogContent>

            <DialogActions>
                <Button
                    onClick={onClose}
                    disabled={loading}
                >
                    Cancel
                </Button>

                <Button
                    color="error"
                    variant="contained"
                    loading={loading}
                    onClick={onConfirm}
                >
                    Delete
                </Button>
            </DialogActions>
        </Dialog>
    );
}