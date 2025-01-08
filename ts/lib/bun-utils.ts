import * as fs from "node:fs";
import * as os from "node:os";


export function bunReadData(scriptPath: string): string {
    const {year, day} = getPathParts(scriptPath);

    const  dataFilename = "data.txt"
    let filename = `./${year}/${day}/${dataFilename}`;

    console.log(`Reading data from ${filename}`);
    return fs.readFileSync(filename, "utf-8");
}

export function getPathParts(scriptPath: string) {
    const pathSeparator = os.platform() === "win32" ? "\\" : "/";

    const splitPath = scriptPath.split(pathSeparator);
    const partPart = splitPath[splitPath.length - 1].split("part-")[1]
    const day = splitPath[splitPath.length - 2]
    const year = splitPath[splitPath.length - 3]
    const part = partPart?.match(/\d+/)?.[0]!;
    return {year, day, part};
}

function isFile(path?: string) {
    if(!path) return false;
    try {
        const stat = fs.statSync(path);
        return stat.isFile();
    } catch(e) {
        return false;
    }
}

export async function validateScript(scriptPath?: string) {
    if(!scriptPath) {
        console.error("No file to run");
        process.exit(1);
    }

    // .js or .ts
    if(!scriptPath.endsWith(".ts") && !scriptPath.endsWith(".js")) {
        console.error("File must be .ts or .js");
        process.exit(1);
    }

    if(!isFile(scriptPath)) {
        console.error("File does not exist");
        process.exit(1);
    }
}