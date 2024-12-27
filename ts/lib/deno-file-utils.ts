/*
    Read data from file according to the day and part
 */
export async function readData(): Promise<string> {
    if(isBrowser()) {
        const res = await fetch("/data.txt")
        return await res.text()
    } else {
        const stack = new Error().stack!.split("\n")
        const lastStack = stack[stack.length - 1]

        const {day} = getDayAndPart(lastStack);

        const  dataFilename = "data.txt"
        let filename = `./2024/${day}/${dataFilename}`;

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
}

export function isBrowser() {
    return typeof window !== "undefined";
}

export function getDayAndPart(scriptPath: string) {
    const splitPath = scriptPath.split("/")
    const partPart = splitPath[splitPath.length - 1].split("part-")[1]
    const day = splitPath[splitPath.length - 2]
    const part = partPart?.match(/\d+/)?.[0]!;
    return {day, part};
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