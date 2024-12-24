import {readData} from "../../lib/file-utils";

export function parseData(data) {
    const reports = [];
    const lines = data.split("\n");
    for(const line of lines) {
        const levels = line.split(" ");
        reports.push(levels);
    }
    return reports
}

const data = readData(import.meta);
const reports = parseData(data);

console.log(reports)