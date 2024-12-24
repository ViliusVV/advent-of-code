/*
    Read data from file according to the day and part
 */
export function readData(meta: ImportMeta, noPart = true): string {
    const day = meta.filename?.split("/days/")[1].split("/")[0];

    // first number is part
    let part = meta.filename?.split("/part-")[1];
    part = part?.match(/\d+/)?.[0];

    let dataFilename = "";
    if(noPart) {
        dataFilename = "data.txt"
    } else {
        dataFilename = `part-${part}.txt`
    }

    let filename = `./days/${day}/${dataFilename}`;

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

function checkIfExists(path: string) {
    try {
        Deno.statSync(path);
        return true;
    // deno-lint-ignore no-unused-vars
    } catch(e) {
        return false;
    }
}