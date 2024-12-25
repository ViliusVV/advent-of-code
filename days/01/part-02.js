import  {readData} from "../../lib/deno-file-utils.ts";
import {parseData} from "./common.ts";
import {logLine, sum} from "../../lib/utils.ts";

function countOccurrences(rightArr) {
    const counts = {};
    for(let num of rightArr) {
        counts[num] = counts[num] ? counts[num] + 1 : 1;
    }

    return counts;
}

readData().then(data => {
    const locations = parseData(data);

    const counts = countOccurrences(locations.right);
    logLine("Counts", counts);

    const scores = []
    for (let num of locations.left) {
        if(num in counts) {
            scores.push(num * counts[num]);
        }
    }

    logLine("Scores", scores);

    const similarity = sum(scores);

    logLine("Similarity", similarity);
});


