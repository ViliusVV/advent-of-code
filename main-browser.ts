import { bundle } from "https://deno.land/x/emit/mod.ts";
import {getDayAndPart, validateScript} from "./lib/deno-file-utils.ts";
import {debounce} from "./lib/utils.ts";

const DATA_NAME = "data.txt";

const TARGET_DIR = "./target";
const INDEX_TEMPLATE_PATH = `${TARGET_DIR}/index.template.html`;
const GENERATED_DIR = `${TARGET_DIR}/generated`;
const INDEX_PATH = `${GENERATED_DIR}/index.html`

type AppContext = {
    scriptFile: string;
    day: string;
    part: string;
    dayDir: string;
}

function getIndexResponse() {
    const indexContent = Deno.readTextFileSync(INDEX_PATH);
    return new Response(indexContent, { headers: { "content-type": "text/html" }});
}

function getCodeResponse(path: string) {
    const processedPath = path.replace("/", "");
    const bundlePath = getBundlePath(processedPath);
    const code = Deno.readTextFileSync(bundlePath);
    return new Response(code, { headers: { "content-type": "application/javascript" }});
}

function getDataResponse(ctx: AppContext) {
    const dataContent = Deno.readTextFileSync(`${ctx.dayDir}/${DATA_NAME}`);
    return new Response(dataContent, { headers: { "content-type": "text/plain" }});
}

function getBundlePath(bundleName: string) {
    return `${GENERATED_DIR}/${bundleName}`;
}

function getBundleName(ctx: AppContext) {
    const randomHash = Math.random().toString(36).substring(7);
    return `bundle-${ctx.day}-${ctx.part}.${randomHash}.js`;
}

function createTemplatedIndex(bundleName: string) {
    const indexTemplate = Deno.readTextFileSync(INDEX_TEMPLATE_PATH)
    const processed = indexTemplate.replace("{{BUNDLE_NAME}}", bundleName);
    Deno.writeTextFileSync(INDEX_PATH, processed);
    console.log("Updated index.html");
    return processed;
}

async function compileCode(ctx: AppContext) {
    const compiled = await bundle(ctx.scriptFile);
    const code = compiled.code;

    const bundleName = getBundleName(ctx);
    const bundlePath = getBundlePath(bundleName);

    Deno.writeTextFileSync(bundlePath , code);
    createTemplatedIndex(bundleName);
    console.log(`Compiled ${ctx.scriptFile} to ${bundlePath}`);
}

function handleRequestRoute(req: Request, ctx: AppContext): Response {
    const url = new URL(req.url);
    const path = url.pathname;
    console.log("GET:", path);


    // Index/entrypoint
    if(path === "/" || path === "/index.html") {
        return getIndexResponse();
    }
    // Handle JS bundle requests
    else if(path.endsWith(".js")) {
        return getCodeResponse(path);
    }
    // Handle data requests
    else if(path === `/${DATA_NAME}`) {
        return getDataResponse(ctx)
    }
    // Favicon
    else if(path === "/favicon.ico") {
        return new Response("", { status: 200 });
    }
    // Not Found
    else {
        return new Response("Not Found", { status: 404 });
    }
}

async function watchAndCompileFiles(ctx: AppContext) {
    const debouncedCompile = debounce(async () => {
        await compileCode(ctx)
    }, 250);

    const watcher = Deno.watchFs(ctx.dayDir);
    for await (const event of watcher) {
        if(event.kind === "modify" && !event.paths[0].endsWith("~")) {
            console.log("Modified", event.paths);
            debouncedCompile();
        }
    }
}

if(import.meta.main) {
    console.log("AoC Entry Point");

    const scriptFile = Deno.args[0];
    const {day, part} = getDayAndPart(Deno.args[0]);
    validateScript(scriptFile);

    const ctx : AppContext = {
        part: part,
        day: day,
        dayDir: `days/${day}`,
        scriptFile: scriptFile,
    }

    await compileCode(ctx);

    // noinspection JSIgnoredPromiseFromCall
    watchAndCompileFiles(ctx)

    Deno.serve(
        { port: 8000 },
        (req) => {
           return handleRequestRoute(req, ctx);
        }
    );
}


