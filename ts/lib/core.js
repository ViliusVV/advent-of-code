import {extractPartPaths, setHeader} from "./utils";

document.addEventListener("DOMContentLoaded", function () {
    setupReload()
    loadAocScript();
})

function loadAocScript() {
    // noinspection NpmUsedModulesInstalled
    // import("aoc").then(({aocMain}) => {
    //     console.log("AOC script loaded");
    //     browserReadData().then(data => {
    //         console.log("Data loaded");
    //         aocMain(data);
    //     });
    // });

    // Load aoc part script from the server according to current url
    const url = new URL(location.href);
    const path = url.pathname;
    const {year, day, part} = extractPartPaths(path);
    const scriptPath = `/bundles/${year}/${day}/part-${part}.js`;
    setHeader(`Loading script: ${scriptPath}`, 2);
}

function setupReload() {
    console.log("Page ready and reloader script loaded");
    setHeader("WSConnection NOT CONNECTED", 3);

    const websocket = new WebSocket("/");

    websocket.onopen = (e) => {
        setHeader("WSConnection Opened", 3);
    };

    websocket.onclose = (e) => {
        setHeader("WSConnection Disconnected", 3);

        // poll with fetch until the server is back up
        const poll = async () => {
            try {
                const response = await fetch("/", {method: "HEAD"});
                if (response.ok) {
                    setHeader("Server is back up!", 3);
                    clearInterval(pollInterval);
                    location.reload();
                }
            } catch (e) {
                setHeader("Server is still down..." + e, 3);
            }
        }

        const pollInterval = setInterval(poll, 1000);
    };

    websocket.onmessage = (e) => {
        setHeader(`WS Rec: ${e.data}`, 3);
        if(e.data === "RELOAD") {
            console.log("Reloading page...");
            location.reload();
        }
    };

    websocket.onerror = (e) => {
        setHeader(`WS Err: ${e.data}`, 3);
    };
}