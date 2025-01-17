const SPC = "&nbsp;";

export type AoCModule = {
    aocMain: (data: string) => void;
};


export function sum(arr: number[]): number {
    return arr.reduce((sum, curr) => sum + curr, 0);
}

export function removeAt(arr: number[], index: number): number[] {
    return arr.slice(0, index).concat(arr.slice(index + 1));
}

type Handler = <T>(t: T) => void;
export function debounce(callback: Handler, ms: number) {
    let timer: Timer;
    return function <T>(...t: T[]) {
        clearTimeout(timer);
        timer = setTimeout(() => callback(t), ms);
    };
}

export function isBrowser() {
    return typeof window !== "undefined";
}

export async function browserReadData(path: string): Promise<string> {
    const dataPath = `/${path}/data.txt`;
    console.log(`Loading data from ${dataPath}`);
    const res = await fetch(dataPath);
    return await res.text();
}

export function getScriptPath(year: number, day: number, part: number) {
    return `/${year}/${day}/${part}.js`;
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

export function extractPartPaths(path: string) {
    // /2024/01/01
    const splitPath = path.split("/");
    const year = splitPath[1];
    const day = splitPath[2];
    const part = splitPath[3].replace(".js", "");
    return {year, day, part};
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