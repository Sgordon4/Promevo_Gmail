import type GMailLabel from "./GMailLabel";

const labels = new Map<string, GMailLabel>([
	["INBOX", { id: "INBOX", name: "Inbox"}],
	["IMPORTANT", { id: "IMPORTANT", name: "Important"}],
	["WORK", { id: "WORK", name: "Work"}],
	["PERSONAL",  { id: "PERSONAL", name: "Personal"},],
]);


export async function getAllLabels(): Promise<GMailLabel[]> {
	await new Promise(r => setTimeout(r, 500));
	return Array.from(labels.values());
}

export async function getLabel(id: string): Promise<GMailLabel | undefined> {
	await new Promise(r => setTimeout(r, 500));
	return labels.get(id);
}

export async function createLabel(name: string): Promise<void> {
	await new Promise(r => setTimeout(r, 500));
	const label: GMailLabel = {
		id: name,
		name: name
	}
	
	labels.set(name, label);
}

export async function updateLabel(label: GMailLabel): Promise<void> {
	await new Promise(r => setTimeout(r, 500));

	labels.set(label.id, label);
}

export async function deleteLabel(id: string): Promise<void> {
	await new Promise(r => setTimeout(r, 500));
	labels.delete(id);
}