import {readData} from "../../lib/file-utils.ts";
import {parseData} from "./common.ts";

function findDistances(locations: {left: number[], right: number[]}) {
    const distances: number[] = [];
    for(let i = 0; i < locations.left.length; i++) {
        const left = locations.left[i];
        const right = locations.right[i];
        distances.push(Math.abs(right - left));
    }
    return distances;
}

const data = readData(import.meta);
const locations = parseData(data);

locations.left.sort();
locations.right.sort();

const distances = findDistances(locations);
const sum = distances.reduce((sum, curr) => sum + curr, 0);

console.log("Sum of distances is", sum);

