/*
    Read data from file according to the day and part
 */
export function readData(meta: ImportMeta): string {
    const day = meta.filename?.split("/days/")[1].split("/")[0];

    // first number is part
    let part = meta.filename?.split("/part-")[1];
    part = part?.match(/\d+/)?.[0];

    if(!day || !part) {
        console.error("Could not determine day or part from filename");
        Deno.exit(1);
    }

    let filename = `./days/${day}/part-${part}.txt`;

    // launching from ide uses other cwd
    if(checkIfExists(filename)) {
        console.log(`Reading data from ${filename}`);
        return Deno.readTextFileSync(filename);
    }

    console.error(`File ${filename} does not exist`);

    filename = `./part-${part}.txt`;

    console.log(`Reading data from ${filename}`);
    return Deno.readTextFileSync(filename);
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