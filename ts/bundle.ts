import {bundleScripts} from "./lib/bundler.ts";

const entryPoint = "./2024/01/part-01.ts";
const outputBundle = "./target/generated";

await bundleScripts([entryPoint, "./lib/loader.js"], outputBundle);