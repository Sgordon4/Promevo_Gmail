import { Navigate, Route, Routes } from "react-router-dom";

import LabelDetailsPage from "./pages/LabelDetailsPage";
import LabelsPage from "./pages/LabelsPage";
import NotFoundPage from "./pages/NotFoundPage";

function App() {
	return (
		<Routes>
			<Route path="/" element={<Navigate to="/labels" replace />} />
			<Route path="/labels" element={<LabelsPage />} />
			<Route path="/labels/:id" element={<LabelDetailsPage />} />
			<Route path="*" element={<NotFoundPage />} />
		</Routes>
	);
}

export default App;