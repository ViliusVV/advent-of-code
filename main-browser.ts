import { bundle } from "https://deno.land/x/emit/mod.ts";
import {getDayAndPart, validateScript} from "./lib/deno-file-utils.ts";

const OUT_DIR = "./target";
const BUNDLE_NAME = "main.bundle.js";
const DATA_PATH = "./data.txt";
const BUNDLE_PATH = `${OUT_DIR}/${BUNDLE_NAME}`;
const INDEX_PATH = `${OUT_DIR}/index.html`

if(import.meta.main) {
  console.log("AoC Entry Point");

  const scriptFile = `./${Deno.args[0]}`;
  validateScript(scriptFile);
  const {day, part} = getDayAndPart(Deno.args[0]);

  const result = await bundle(scriptFile);

  Deno.writeTextFileSync(BUNDLE_PATH, result.code);

  const index = Deno.readTextFileSync(INDEX_PATH);

  Deno.serve(
      { port: 8000},
      (req) => {
        const url = new URL(req.url);
        const path = url.pathname;
        console.log(path);

        if(path === "/") {
            return new Response(index, { headers: { "content-type": "text/html" }});
        } else if(path === `/${BUNDLE_NAME}`) {
          return new Response(result.code, { headers: { "content-type": "application/javascript" }});
        } else if(path === DATA_PATH) {
          const data = Deno.readTextFileSync(`./days/${day}/${DATA_PATH}`);
            return new Response(data, { headers: { "content-type": "text/plain" }});
        } else {
          return new Response("Not Found", { status: 404 });
        }
      }
  );
}


