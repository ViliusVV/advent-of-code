export function sum(arr: number[]): number {
    return arr.reduce((sum, curr) => sum + curr, 0);
}