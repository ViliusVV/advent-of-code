import {readData} from "../../lib/deno-file-utils.ts";
import {logLine} from "../../lib/utils.ts";


const MATCHER =  /mul\((\d+),(\d+)\)|do(?:n't)?\(\)/g;

function parseData(data) {
    const matches = data.matchAll(MATCHER);

    let doStatementIsLast = true;
    let operations = []
    matches.forEach(match => {
        let a, b;
        if(match[0].startsWith("mul")) {
            a = parseInt(match[1]);
            b = parseInt(match[2]);
        } else if(match[0] === "do()") {
            doStatementIsLast = true;
        } else if(match[0] === "don't()") {
            doStatementIsLast = false;
        }

        operations.push({
            enabled: doStatementIsLast,
            raw: match[0],
            a: a,
            b: b,
        });
    });

    return operations;
}

function compute(operations) {
    return operations.reduce((acc, op) => {
        if(!op.enabled || !op.a) {
            return acc;
        }

        return acc + op.a * op.b;
    }, 0);
}

readData().then(data => {
    logLine(data);

    const operations = parseData(data);
    logLine(operations);

    const result = compute(operations);
    logLine(result);
});

