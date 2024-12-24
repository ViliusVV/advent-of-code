export function parseData(data: string) {
    const reports = [];
    const lines = data.split("\n");
    for(const line of lines) {
        const levels = line.split(" ").map(l => parseInt(l));
        reports.push(levels);
    }
    return reports
}
