import { bundle } from "https://deno.land/x/emit/mod.ts";
import {getDayAndPart, validateScript} from "./lib/deno-file-utils.ts";
import {debounce} from "./lib/utils.ts";

const OUT_DIR = "./target";
const BUNDLE_NAME = "main.bundle.js";
const DATA_PATH = "/data.txt";
const BUNDLE_PATH = `${OUT_DIR}/${BUNDLE_NAME}`;
const INDEX_PATH = `${OUT_DIR}/index.html`

function getIndexResponse() {
    const indexContent = Deno.readTextFileSync(INDEX_PATH);
    return new Response(indexContent, { headers: { "content-type": "text/html" }});
}

function getCodeResponse() {
    const code = Deno.readTextFileSync(BUNDLE_PATH);
    return new Response(code, { headers: { "content-type": "application/javascript" }});
}

function getDataResponse(ctx: AppContext) {
    const dataContent = Deno.readTextFileSync(`${ctx.dayDir}/${DATA_PATH}`);
    return new Response(dataContent, { headers: { "content-type": "text/plain" }});
}

async function compileCode(scriptFile: string) {
    const compiled = await bundle(scriptFile);
    const code = compiled.code;
    Deno.writeTextFileSync(BUNDLE_PATH, code);
    console.log("Compiled", scriptFile);
    return code;
}

function handleRequestRoute(req: Request, ctx: AppContext): Response {
    const url = new URL(req.url);
    const path = url.pathname;
    console.log(path);


    // Index/entrypoint
    if(path === "/") {
        return getIndexResponse();
    }
    // Handle JS bundle requests
    else if(path === `/${BUNDLE_NAME}`) {
        return getCodeResponse();
    }
    // Handle data requests
    else if(path === DATA_PATH) {
        return getDataResponse(ctx)
    }
    // Not Found
    else {
        return new Response("Not Found", { status: 404 });
    }
}

type AppContext = {
    code: string;
    scriptFile: string;
    day: string;
    dayDir: string;
}

if(import.meta.main) {
    console.log("AoC Entry Point");

    const scriptFile = Deno.args[0];
    const {day} = getDayAndPart(Deno.args[0]);
    validateScript(scriptFile);

    const ctx : AppContext = {
        code: await compileCode(scriptFile),
        day: day,
        dayDir: `days/${day}`,
        scriptFile: scriptFile
    }

    const debouncedCompile = debounce(async () => {
        ctx.code =  await compileCode(scriptFile)
    }, 250);

    const watcher = Deno.watchFs(ctx.dayDir);
    for await (const event of watcher) {
        if(event.kind === "modify" && !event.paths[0].endsWith("~")) {
            console.log("Modified", event.paths);
            debouncedCompile();
        }
    }

    Deno.serve(
        { port: 8000 },
        (req) => {
           return handleRequestRoute(req, ctx);
        }
    );
}


