import {
    type AppContext,
    extractPartPaths,
    readTextFileSync, type RequestContext,
} from "./lib/server-utils";
import fs from "fs";
import {bundleScripts} from "./lib/bundler";
import {debounce} from "./lib/utils";

const DATA_NAME = "data.txt";

const TARGET_DIR = "./target";
const INDEX_TEMPLATE_PATH = `${TARGET_DIR}/index.template.html`;
const GENERATED_DIR = `${TARGET_DIR}/generated`;
const INDEX_PATH = `${GENERATED_DIR}/index.html`

function getIndexResponse() {
    const indexContent = readTextFileSync(INDEX_PATH);
    return new Response(indexContent, { headers: { "content-type": "text/html" }});
}

function getBundleResponse(path: string) {
    const processedPath = path.replace("/", "");
    const filePath = `${GENERATED_DIR}/${processedPath}`;
    const code = readTextFileSync(filePath);
    return new Response(code, { headers: { "content-type": "application/javascript" }});
}

function getDataResponse(ctx: AppContext) {
    const dataContent = readTextFileSync(`${ctx.dayDir}/${DATA_NAME}`);
    return new Response(dataContent, { headers: { "content-type": "text/plain" }});
}

function getScriptPath(ctx: RequestContext) {
    const {year, day, part} = extractPartPaths(ctx.path);
    return `${year}/${day}/part-${part}.ts`;
}

function exctractFileName(path: string) {
    const parts = path.split("/");
    return parts[parts.length - 1];
}

function createTemplatedIndex(bundleName: string) {
    const indexTemplate = readTextFileSync(INDEX_TEMPLATE_PATH)
    const processed = indexTemplate.replace("{{BUNDLE_NAME}}", `generated/${bundleName}`);
    fs.writeFileSync(INDEX_PATH, processed, { encoding: "utf-8" });
    console.log("Updated index.html");
    return processed;
}

async function compileScript(reqCtx: RequestContext) {
    const scriptPath = getScriptPath(reqCtx);
    const entryPoints = [scriptPath, "./lib/core.ts"];

    await bundleScripts(entryPoints, GENERATED_DIR);

    notifySessions(reqCtx.appCtx);

    console.log(`Compiled ${scriptPath}`);
}

function cleanupGenerated() {
    console.log("Cleaning up generated files");
    const generatedFiles = fs.readdirSync(GENERATED_DIR);
    for(const entry of generatedFiles) {
        // all .js and .html files
        if(entry.endsWith(".js") || entry.endsWith(".html")) {
            fs.readFileSync(`${GENERATED_DIR}/${entry}`);
            console.log("Removed generated file", entry);
        }
    }
}

async function handleRequestRoute(req: Request, ctx: AppContext): Promise<Response> {
    const url = new URL(req.url);
    const path = url.pathname;
    console.log("GET:", path);

    // if (req.headers.get("upgrade") === "websocket") {
    //     const { socket, response } = Deno.upgradeWebSocket(req);
    //
    //     socket.onopen = () => {
    //         console.log("WebSocket client connected");
    //         ctx.sessions.push(socket);
    //     };
    //     socket.onmessage = (e: any) => {
    //         console.log("WebSocket message", e.data);
    //         socket.send("Server: Received: " + e.data);
    //     }
    //     socket.onclose = () => {
    //         console.log("WebSocket client disconnected");
    //         ctx.sessions = ctx.sessions.filter((s) => s !== socket);
    //     };
    //     socket.onerror = (err: Error) => {
    //         console.error("WebSocket error:", err);
    //     };
    //
    //     return response;
    // } else {
    // Index/entrypoint
    if(path === "/" || path === "/index.html") {
        return getIndexResponse();
    }
    // Handle /2024/01/01 html and /2024/01/01/data.txt
    else if(path.match(/\/\d{4}\/\d{2}\/\d{2}/)) {
        if(path.endsWith(DATA_NAME)) {
            return getDataResponse(ctx);
        } else {
            const {year, day, part} = extractPartPaths(path);
            console.log("Year", year, "Day", day, "Part", part);
            return getIndexResponse();
        }
    }
    // Handle JS bundle requests
    else if(path.startsWith("/bundles")) {
        return getBundleResponse(path.replace("/bundles", ""));
    }
    // Favicon
    else if(path === "/favicon.ico") {
        return new Response("", { status: 200 });
    }
    // Not Found
    else {
        return new Response("Not Found", { status: 404 });
    }
    // }
}

async function watchAndCompileFiles(ctx: AppContext) {
    const debouncedCompile = debounce(async () => {
        // await compileCode(ctx)
        createTemplatedIndex("/aa.js");
    }, 250);

    const watcher = fs.watch(import.meta.dir, (event, filename) => {
        if(event === "change"){
            debouncedCompile();
            console.log(`${filename}`);
        }
    });
}

function notifySessions(ctx: AppContext) {
    for(const session of ctx.sessions) {
        session.send("RELOAD");
    }
}

function main() {
    console.log("AoC Entry Point");
    // cleanupGenerated();

    const ctx : AppContext = {
        sessions: [],
    }


    createTemplatedIndex("/aa.js");
    // noinspection JSIgnoredPromiseFromCall
    watchAndCompileFiles(ctx)


    console.log("AoC Browser Entry Point");
    Bun.serve({
        port: 8000,
        fetch(req) {
            return handleRequestRoute(req, ctx);
        },});
}

if(import.meta.main) {
    main();
}