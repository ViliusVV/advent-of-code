import {readData} from "../../lib/file-utils.ts";

const MATCHER =  /mul\((\d+),(\d+)\)/g;

function parseData(data) {
    const matches = data.matchAll(MATCHER);

    let operations = []
    matches.forEach(match => {
        operations.push({
            raw: match[0],
            a: parseInt(match[1]),
            b: parseInt(match[2])
        });
    });

    return operations;
}

function compute(operations) {
    return operations.reduce((acc, op) => acc + op.a * op.b, 0);
}

const data = readData(import.meta);
console.log(data);

const operations = parseData(data);
console.log(operations);

const result = compute(operations);
console.log(result);

