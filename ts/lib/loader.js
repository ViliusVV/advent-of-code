import {browserReadData, extractPartPaths, getScriptPath, setHeader} from "./utils";

document.addEventListener("DOMContentLoaded", function () {
    // setupReload()
    loadAocScript();
})

function loadAocScript() {
    // Load aoc part script from the server according to current url
    const path = new URL(location.href).pathname;
    const {year, day, part} = extractPartPaths(path);
    const scriptPath = getScriptPath(year, day, part);
    setHeader(`Loading script: ${scriptPath}`, 2);

    // Load the script and run the aocMain function
    import(scriptPath).then(({aocMain}) => {
        console.log("AOC script loaded");
        browserReadData(path).then(data => {
            console.log("Data loaded");
            aocMain(data);
        });
    });
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