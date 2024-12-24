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

const data = readData(import.meta);

const locations = parseData(data);

console.log(locations);

