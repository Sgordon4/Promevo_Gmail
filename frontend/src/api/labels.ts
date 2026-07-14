import type GMailLabel from "./GMailLabel";
const API_URL = import.meta.env.VITE_API_URL;

export async function getAllLabels(): Promise<GMailLabel[]> {
	const response = await fetch(`${API_URL}/gmail/labels`);
    
	if (!response.ok) throw await response.json();
    return await response.json();
}

export async function getLabel(id: string): Promise<GMailLabel | null> {
	const response = await fetch(`${API_URL}/gmail/labels/${id}`);
    
	if (response.status === 404) return null;
	if (!response.ok) throw await response.json();
    return await response.json();
}

export async function createLabel(name: string): Promise<GMailLabel> {
	const response = await fetch(`${API_URL}/gmail/labels`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name })
    });

    if (!response.ok) throw await response.json();
	return await response.json();
}

export async function updateLabel(label: GMailLabel): Promise<GMailLabel> {
	const response = await fetch(`${API_URL}/gmail/labels/${label.id}`, {
		method: "PATCH",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ name: label.name })
	});

	if (!response.ok) throw await response.json();
	return await response.json();
}

export async function deleteLabel(id: string): Promise<void> {
	const response = await fetch(`${API_URL}/gmail/labels/${id}`, {
		method: "DELETE"
	});
	
	if (!response.ok) throw await response.json();
	//Delete returns 204
}
