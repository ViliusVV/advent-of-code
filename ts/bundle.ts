
import {rollup} from "rollup";
import typescriptImport from "@rollup/plugin-typescript";
import resolveImport from "@rollup/plugin-node-resolve";
import tslibImport from "tslib";
import commonjs from "@rollup/plugin-commonjs";

const typescript = (typescriptImport as any as typeof typescriptImport.default);
const resolve = (resolveImport as any as typeof resolveImport.default);
const tslib = (tslibImport as any as typeof tslibImport);


async function bundleEntryPoint(entry: string, output: string) {
    try {
        // Step 1: Create Rollup bundle
        const bundle = await rollup({
            input: entry, // Entry point to your application
            plugins: [
                resolve({
                    extensions: [".js", ".ts"], // Resolve JS and TS files
                }), // Compile TypeScript files
                commonjs(),
                typescript({ tsconfig: "./tsconfig.json", tslib}), // Compile TypeScript files
            ],
        });

        // Step 2: Generate output
        const { output: generatedOutput } = await bundle.generate({
            file: output,
            format: "esm", // Output format compatible with Deno
            sourcemap: true, // Optional: Sourcemaps for debugging
        });

        // Step 3: Write output file
        for (const chunkOrAsset of generatedOutput) {
            if (chunkOrAsset.type === "chunk") {
                console.log(`Writing chunk: ${chunkOrAsset.fileName}`);
                // Write the JavaScript bundle
                await Deno.writeTextFile(output, chunkOrAsset.code);

                // Write the source map
                if (chunkOrAsset.map) {
                    const mapFileName = `${output}.map`;
                    console.log(`Writing source map: ${mapFileName}`);
                    await Deno.writeTextFile(mapFileName, chunkOrAsset.map.toString());
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
// const entryPoint = "./2024/01/part-01.ts"; // Replace with your actual entry point
const entryPoint = "./test-entry.ts"; // Replace with your actual entry point
const outputBundle = "./test-output.js"; // Replace with your desired output file

console.log("Bundling entry point:", entryPoint);
await bundleEntryPoint(entryPoint, outputBundle);
console.log("Bundling complete");