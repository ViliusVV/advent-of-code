import {rollup} from "rollup";
import {nodeResolve} from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import typescript from "@rollup/plugin-typescript";
import {writeTextFileSync} from "./lib/server-utils.ts";

async function bundleEntryPoint(entry: string[], output: string) {
    try {
        // Step 1: Create Rollup bundle
        const bundle = await rollup({
            input: entry,
            plugins: [
                nodeResolve({
                    extensions: [".js", ".ts"], // Resolve JS and TS files
                }),
                commonjs(),
                typescript({tsconfig: "./tsconfig-rollup.json"}), // Compile TypeScript files
            ],
        });

        // Step 2: Generate output
        const { output: generatedOutput } = await bundle.generate({
            dir: output,
            format: "esm",
            sourcemap: true,
        });

        // Step 3: Write output file
        for (const chunkOrAsset of generatedOutput) {
            if (chunkOrAsset.type === "chunk") {
                console.log(`Writing chunk: ${chunkOrAsset.fileName}`);
                // Write the JavaScript bundle
                writeTextFileSync(output, chunkOrAsset.code);

                // Write the source map
                if (chunkOrAsset.map) {
                    const mapFileName = `${chunkOrAsset.fileName}.map`;
                    console.log(`Writing source map: ${mapFileName}`);
                    writeTextFileSync(mapFileName, chunkOrAsset.map.toString());
                }
            } else {
                console.log(`Writing asset: ${chunkOrAsset.fileName}`);
            }
        }

        // Step 4: Close the bundle
        await bundle.close();

        console.log(`Bundling complete. Output written to ${output}`);
    } catch (error) {
        console.error("Error during bundling:", error);
    }
}

// Usage Example
const entryPoint = "./2024/01/part-01.ts"; // Replace with your actual entry point
// const entryPoint = "./test-entry.ts"; // Replace with your actual entry point
const outputBundle = "./output"; // Replace with your desired output file

console.log("Bundling entry point:", entryPoint);
await bundleEntryPoint([entryPoint, "./lib/core.js"], outputBundle);
console.log("Bundling complete");