console.log("AoC Entry Point");

if(import.meta.main) {
  const fileToRun = Deno.args[0];
  if(!fileToRun) {
    console.error("No file to run");
    Deno.exit(1);
  }

  // .js or .ts
  if(!fileToRun.endsWith(".ts") && !fileToRun.endsWith(".js")) {
      console.error("File must be .ts or .js");
      Deno.exit(1);
  }

  const file = `./${fileToRun}`;
  if(!Deno.statSync(file).isFile) {
    console.error("File does not exist");
    Deno.exit(1);
  }

  import(file);
}
