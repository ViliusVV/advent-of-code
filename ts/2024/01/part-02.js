import {parseData} from "./common.ts";
import {logOutput, setAnswer, sum} from "../../lib/utils.ts";

function countOccurrences(rightArr) {
    const counts = {};
    for(let num of rightArr) {
        counts[num] = counts[num] ? counts[num] + 1 : 1;
    }

    return counts;
}

export function partRunMain(data) {
    const locations = parseData(data);

    const counts = countOccurrences(locations.right);
    logOutput("Counts", counts);

    const scores = []
    for (let num of locations.left) {
        if(num in counts) {
            scores.push(num * counts[num]);
        }
    }

    logOutput("Scores", scores);

    const similarity = sum(scores);

    setAnswer(similarity, "Similarity");
}

