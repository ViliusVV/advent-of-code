import {bunReadData, validateScript} from "./lib/server-utils.ts";
import type {AoCModule} from "./lib/utils.ts";
import fs from "fs";

console.log("AoC Entry Point (IDE)");

if(import.meta.main) {
    const scriptFile = fs.readFileSync(".aoc_launch_path").toString().trim();
    console.log("Script File", scriptFile);
    await validateScript(scriptFile);

    import(`file://${scriptFile}`).then((module: AoCModule) => {
        console.log("Script loaded");
        if(!module.aocMain) {
            console.log("'main(data: string)' function not implemented in module");
            process.exit(1);
        }

        const data = bunReadData(scriptFile)
        module.aocMain(data);
    });
    console.log(`Script ${scriptFile} loading...`);
}

