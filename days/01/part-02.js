import {readData} from "../../lib/file-utils.ts";
import {parseData} from "./common.ts";
import {sum} from "../../lib/utils.ts";

function countOccurrences(rightArr) {
    const counts = {};
    for(let num of rightArr) {
        counts[num] = counts[num] ? counts[num] + 1 : 1;
    }

    return counts;
}

const data = readData(import.meta);
const locations = parseData(data);

const counts = countOccurrences(locations.right);
console.log("Counts", counts);

const scores = []
for (let num of locations.left) {
    if(num in counts) {
        scores.push(num * counts[num]);
    }
}

console.log("Scores", scores);

const similarity = sum(scores);

console.log("Similarity", similarity);

