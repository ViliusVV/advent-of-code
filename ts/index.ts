
console.log("AoC Browser Entry Point");
Bun.serve({
    port: 8000,
    fetch(req) {
        return new Response("Bun!");
},});