import {denoReadData, readData, validateScript} from "./lib/deno-file-utils.ts";
import {AoCModule} from "./lib/utils.ts";

console.log("AoC Entry Point");

if(import.meta.main) {
  const scriptFile = Deno.args[0];
  validateScript(scriptFile);

  import(scriptFile).then((module: AoCModule) => {
    console.log("Script loaded");
    if(!module.default) {
      console.log("'default(data: string)' function not implemented in module");
      Deno.exit(1);
    }

    const data = denoReadData(scriptFile)
    module.default(data);
  });
  console.log(`Script ${scriptFile} loading...`);
}


