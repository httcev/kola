export function sortByCreationDate(input: Array<{}>, asc: boolean = true) {
	sortByProperty('dateCreated', input, asc);
}

export function sortByLastUpdatedDate(input: Array<{}>, asc: boolean = true) {
	sortByProperty('lastUpdated', input, asc);
}

export function sortByProperty(property:string, input: Array<{}>, asc: boolean = true) {
	input.sort((o1,o2) => {
		if (o1[property] == null) {
			return asc ? 1 : -1;
		}
		if (o2[property] == null) {
			return asc ? -1 : 1;
		}
		if (o1[property] < o2[property]) {
			return asc ? -1 : 1;
		}
		if (o1[property] > o2[property]) {
			return asc ? 1 : -1;
		}
		return o1['id'] < o2['id'] ? -1 : 1;
	});
}
