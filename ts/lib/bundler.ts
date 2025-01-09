import {rollup, type RollupCache} from "rollup";
import {nodeResolve} from "@rollup/plugin-node-resolve";
import commonjs from "@rollup/plugin-commonjs";
import typescript from "@rollup/plugin-typescript";
import {writeTextFileSync} from "./server-utils";

let rollupCache: RollupCache | undefined;

export async function bundleScripts(entryPoints: string[], outputDir: string) {
    console.log(`Bundling: ${entryPoints} to ${outputDir}`);
    try {
        // Bundle
        console.log("Rollup bundling...");
        const bundle = await rollup({
            input: entryPoints,
            plugins: [
                nodeResolve({
                    extensions: [".js", ".ts"], // Resolve JS and TS files
                }),
                commonjs(),
                typescript({tsconfig: "./tsconfig-rollup.json"}), // Compile TypeScript files
            ],
            cache: rollupCache,
        });

        rollupCache = bundle.cache;

        // Output
        console.log("Generating output...");
        const { output: generatedOutput } = await bundle.generate({
            dir: outputDir,
            format: "esm",
            sourcemap: true,
        });

        // Write files
        console.log("Writing files...");
        for (const chunkOrAsset of generatedOutput) {
            const filePath = `${outputDir}/${chunkOrAsset.fileName}`
            if (chunkOrAsset.type === "chunk") {
                console.log(`Writing chunk: ${filePath}`);
                // Write the JavaScript bundle
                writeTextFileSync(`${filePath}`, chunkOrAsset.code);

                // Write the source map
                if (chunkOrAsset.map) {
                    const mapFileName = `${filePath}.map`;
                    console.log(`Writing map: ${mapFileName}`);
                    writeTextFileSync(mapFileName, chunkOrAsset.map.toString());
                }
            } else {
                console.log(`Ignoring asset: ${chunkOrAsset.fileName}`);
            }
        }

        await bundle.close();

        console.log(`Bundling complete`);
    } catch (error) {
        console.error("Error during bundling:", error);
    }
}