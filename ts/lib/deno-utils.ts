export function denoReadData(scriptPath: string): string {
    const {year, day} = getPathParts(scriptPath);

    const  dataFilename = "data.txt"
    let filename = `./${year}/${day}/${dataFilename}`;

    // launching from ide uses other cwd
    if(checkIfExists(filename)) {
        console.log(`Reading data from ${filename}`);
        return Deno.readTextFileSync(filename);
    }

    console.error(`File ${filename} does not exist`);

    filename = `./${dataFilename}`;

    console.log(`Reading data from ${filename}`);
    return Deno.readTextFileSync(filename);
}

export function getPathParts(scriptPath: string) {
    const pathSeparator = Deno.build.os === "windows" ? "\\" : "/";

    const splitPath = scriptPath.split(pathSeparator);
    const partPart = splitPath[splitPath.length - 1].split("part-")[1]
    const day = splitPath[splitPath.length - 2]
    const year = splitPath[splitPath.length - 3]
    const part = partPart?.match(/\d+/)?.[0]!;
    return {year, day, part};
}

function checkIfExists(path: string) {
    try {
        Deno.statSync(path);
        return true;
    // deno-lint-ignore no-unused-vars
    } catch(e) {
        return false;
    }
}

export function validateScript(scriptPath: string) {
    if(!scriptPath) {
        console.error("No file to run");
        Deno.exit(1);
    }

    // .js or .ts
    if(!scriptPath.endsWith(".ts") && !scriptPath.endsWith(".js")) {
        console.error("File must be .ts or .js");
        Deno.exit(1);
    }

    if(!Deno.statSync(scriptPath).isFile) {
        console.error("File does not exist");
        Deno.exit(1);
    }
}