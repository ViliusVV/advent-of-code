function readData(meta, noPart = true) {
    const { day, part } = getDayAndPart(meta.url);
    let dataFilename = "";
    if (noPart) {
        dataFilename = "data.txt";
    } else {
        dataFilename = `part-${part}.txt`;
    }
    let filename = `./days/${day}/${dataFilename}`;
    if (checkIfExists(filename)) {
        console.log(`Reading data from ${filename}`);
        return Deno.readTextFileSync(filename);
    }
    console.error(`File ${filename} does not exist`);
    filename = `./${dataFilename}`;
    console.log(`Reading data from ${filename}`);
    return Deno.readTextFileSync(filename);
}
function getDayAndPart(scriptPath) {
    console.log(scriptPath);
    const day = scriptPath.split("days/")[1].split("/")[0];
    let part = scriptPath?.split("/part-")[1];
    part = part?.match(/\d+/)?.[0];
    return {
        day,
        part
    };
}
function checkIfExists(path) {
    try {
        Deno.statSync(path);
        return true;
    } catch (e) {
        return false;
    }
}
const importMeta = {
    url: "file:///home/vilius/repos/aoc-2024/days/03/part-01.js",
    main: import.meta.main
};
const MATCHER = /mul\((\d+),(\d+)\)/g;
function parseData(data) {
    const matches = data.matchAll(MATCHER);
    let operations = [];
    matches.forEach((match)=>{
        operations.push({
            raw: match[0],
            a: parseInt(match[1]),
            b: parseInt(match[2])
        });
    });
    return operations;
}
function compute(operations) {
    return operations.reduce((acc, op)=>acc + op.a * op.b, 0);
}
const data = readData(importMeta);
console.log(data);
const operations = parseData(data);
console.log(operations);
const result = compute(operations);
console.log(result);
