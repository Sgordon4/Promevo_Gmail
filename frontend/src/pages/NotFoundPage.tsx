import { Box, Typography, Button } from "@mui/material";
import { Link } from 'react-router-dom';

export default function NotFoundPage() {
    return (
        <Box sx={{textAlign:"center", py:8}}>

            <Typography variant="h3"> 404 </Typography>
            <Typography> Page not found. </Typography>

            <Button component={Link} to="/labels" sx={{mt:2}}>
                Return to labels
            </Button>
        </Box>
    );
}