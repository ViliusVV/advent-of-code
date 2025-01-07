import {denoReadData, validateScript} from "./lib/bun-utils.ts";
import type {AoCModule} from "./lib/utils.ts";

console.log("AoC Entry Point (IDE)");

if(import.meta.main) {
    const scriptFile = Bun.argv[2];
    console.log("Script File", scriptFile);
    await validateScript(scriptFile);

    import(`file://${scriptFile}`).then((module: AoCModule) => {
        console.log("Script loaded");
        if(!module.aocMain) {
            console.log("'main(data: string)' function not implemented in module");
            process.exit(1);
        }

        const data = denoReadData(scriptFile)
        module.aocMain(data);
    });
    console.log(`Script ${scriptFile} loading...`);
}

