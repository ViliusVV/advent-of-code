import {denoReadData, validateScript} from "./lib/deno-utils.ts";
import {AoCModule} from "./lib/utils.ts";

console.log("AoC Entry Point");

if(import.meta.main) {
  const scriptFile = Deno.args[0];
  validateScript(scriptFile);

  import(`file://${scriptFile}`).then((module: AoCModule) => {
    console.log("Script loaded");
    if(!module.aocMain) {
      console.log("'main(data: string)' function not implemented in module");
      Deno.exit(1);
    }

    const data = denoReadData(scriptFile)
    module.aocMain(data);
  });
  console.log(`Script ${scriptFile} loading...`);
}


