import {readData} from "../../lib/file-utils.ts";

function parseData(data: string) {
    const lines = data.split("\n");
    const leftArr: number[] = [];
    const rightArr: number[] = [];
    for(const line of lines) {
      const [left, right] = line.split("   ").map(Number);
      leftArr.push(left);
      rightArr.push(right);
    }

    return {
      left: leftArr,
      right: rightArr
    }
}

function findDisances(locations: {left: number[], right: number[]}) {
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

const distances = findDisances(locations);
const sum = distances.reduce((sum, curr) => sum + curr, 0);

console.log("Sum of distances is", sum);

