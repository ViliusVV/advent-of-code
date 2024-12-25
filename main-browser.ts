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
    sessions: WebSocket[];
}

function getIndexResponse() {
    const indexContent = Deno.readTextFileSync(INDEX_PATH);
    return new Response(indexContent, { headers: { "content-type": "text/html" }});
}

function getBundleResponse(path: string) {
    const processedPath = path.replace("/", "");
    const filePath = `${TARGET_DIR}/${processedPath}`;
    const code = Deno.readTextFileSync(filePath);
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

function exctractFileName(path: string) {
    const parts = path.split("/");
    return parts[parts.length - 1];
}

function createTemplatedIndex(bundleName: string) {
    const indexTemplate = Deno.readTextFileSync(INDEX_TEMPLATE_PATH)
    const processed = indexTemplate.replace("{{BUNDLE_NAME}}", `generated/${bundleName}`);
    Deno.writeTextFileSync(INDEX_PATH, processed);
    console.log("Updated index.html");
    return processed;
}

async function compileCode(ctx: AppContext) {
    const bundleName = getBundleName(ctx);
    const bundlePath = getBundlePath(bundleName);

    // copy script
    let scriptContent = Deno.readTextFileSync(ctx.scriptFile);
    let scriptAppend = 'import {setHeader} from "../../lib/utils.ts";\n';
    scriptAppend += 'import "../../lib/core.js";\n';
    scriptAppend += 'setHeader("AoC");\n';
    scriptContent = scriptAppend + scriptContent;
    const tmpScriptPath = `${ctx.scriptFile}~`;
    Deno.writeTextFileSync(tmpScriptPath, scriptContent);

    const compiled = await bundle(tmpScriptPath);
    const code = compiled.code;

    Deno.writeTextFileSync(bundlePath , code);
    Deno.removeSync(tmpScriptPath);
    createTemplatedIndex(bundleName);

    notifySessions(ctx);

    console.log(`Compiled ${ctx.scriptFile} to ${bundlePath}`);
}

function cleanupGenerated() {
    console.log("Cleaning up generated files");
    const generatedFiles = Deno.readDirSync(GENERATED_DIR);
    for(const entry of generatedFiles) {
        // all .js and .html files
        if(entry.isFile && (entry.name.endsWith(".js") || entry.name.endsWith(".html"))) {
            Deno.removeSync(`${GENERATED_DIR}/${entry.name}`);
            console.log("Removed generated file", entry.name);
        }
    }
}

async function handleRequestRoute(req: Request, ctx: AppContext): Promise<Response> {
    const url = new URL(req.url);
    const path = url.pathname;
    console.log("GET:", path);

    if (req.headers.get("upgrade") === "websocket") {
        const { socket, response } = Deno.upgradeWebSocket(req);

        socket.onopen = () => {
            console.log("WebSocket client connected");
            ctx.sessions.push(socket);
        };
        socket.onmessage = (e) => {
            console.log("WebSocket message", e.data);
            socket.send("Server: Received: " + e.data);
        }
        socket.onclose = () => {
            console.log("WebSocket client disconnected");
            ctx.sessions = ctx.sessions.filter((s) => s !== socket);
        };
        socket.onerror = (error) => {
            console.error("WebSocket error:", error);
        };

        return response;
    } else {
        // Index/entrypoint
        if(path === "/" || path === "/index.html") {
            await compileCode(ctx)
            return getIndexResponse();
        }
        // Handle JS bundle requests
        else if(path.endsWith(".js")) {
            return getBundleResponse(path);
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

function notifySessions(ctx: AppContext) {
    for(const session of ctx.sessions) {
        session.send("RELOAD");
    }
}

if(import.meta.main) {
    console.log("AoC Entry Point");

    const scriptFile = Deno.args[0];
    const {day, part} = getDayAndPart(Deno.args[0]);
    validateScript(scriptFile);
    cleanupGenerated();

    const ctx : AppContext = {
        part: part,
        day: day,
        dayDir: `days/${day}`,
        scriptFile: scriptFile,
        sessions: [],
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


