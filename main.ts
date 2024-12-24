import {validateScript} from "./lib/deno-file-utils.ts";

console.log("AoC Entry Point");

if(import.meta.main) {
  const scriptFile = `./${Deno.args[0]}`;
  validateScript(scriptFile);
  import(scriptFile);
}


