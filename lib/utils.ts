export function sum(arr: number[]): number {
    return arr.reduce((sum, curr) => sum + curr, 0);
}

export function removeAt(arr: number[], index: number): number[] {
    return arr.slice(0, index).concat(arr.slice(index + 1));
}

type Handler = <T>(t: T) => void;
export function debounce(callback: Handler, ms: number) {
    let timer: number;
    return function <T>(...t: T[]) {
        clearTimeout(timer);
        timer = setTimeout(() => callback(t), ms);
    };
}

export function setHeader(header: string) {
    const el = document.querySelector("#header");
    el!.innerHTML = header;
}

export function logLine(msg: string) {
    const output = document.querySelector("#output");
    console.log("Log message: ", msg);
    output?.insertAdjacentHTML("afterbegin", `<code>${msg}</code><br/>`);
}