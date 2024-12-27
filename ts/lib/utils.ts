import {isBrowser} from "./deno-file-utils.ts";
import Module = WebAssembly.Module;

const SPC = "&nbsp;";

export type AoCModule = {
    default: (data: string) => void;
};


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

export function getBody() {
    return document.querySelector("#body");
}

export function setHeader(header: string, loc: number = 1) {
    const el = document.querySelector(`#header${loc}`);
    el!.innerHTML = header;
}

function stringify(obj: any): string {
    if(typeof obj === "string") {
        return obj;
    }

    if (typeof obj === "number") {
        return obj.toString();
    }

    return JSON.stringify(obj, null, 2);
}

function onlyBrowser(body: () => void) {
    if(isBrowser()) {
        body()
    }
}

export function setAnswer(answer: string | number, answerName?: string) {
    let msg = "Answer";
    if(answerName) {
        msg += ` (${answerName}) `;
    }
    msg += "is:";

    setBody(body => {
        body.innerHTML = `<span>${msg}</span>${SPC}<span class="glowing-font">${answer}</span>`
    });
    console.log(msg, answer);
}

export function setBody(bodySetter: (body: Element) => void) {
    onlyBrowser(() => {
        const body = document.querySelector("#body")!;
        bodySetter(body)
    })
}

export function logOutput(...varargs: any[]) {
    onlyBrowser(() => {
        const output = document.querySelector("#output");
        const stringified = varargs.map(stringify).join(" ");
        output?.insertAdjacentHTML("afterbegin", `<span>${stringified}</span><br/>`);
    });

    console.log(...varargs);
}