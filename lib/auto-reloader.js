import {logLine} from "./utils.ts";

document.addEventListener("DOMContentLoaded", function () {
    console.log("Page ready and reloader script loaded");

    const wsUri = "ws://127.0.0.1:8000";
    const websocket = new WebSocket(wsUri);
    let pingInterval;



    function sendMessage(message) {
        logLine(`SENT: ${message}`);
        websocket.send(message);
    }

    websocket.onopen = (e) => {
        logLine("CONNECTED");
        sendMessage("ping");
        pingInterval = setInterval(() => {
            sendMessage("ping");
        }, 5000);
    };


    websocket.onclose = (e) => {
        logLine("DISCONNECTED");


        // poll with fetch until the server is back up
        const poll = async () => {
            try {
                const response = await fetch("/", {method: "HEAD"});
                if (response.ok) {
                    logLine("Server is back up!");
                    clearInterval(pollInterval);
                    location.reload();
                }
            } catch (e) {
                logLine("Server is still down..." + e);
                // do nothing
            }
        }

        const pollInterval = setInterval(poll, 1000);

        clearInterval(pingInterval);
    };

    websocket.onmessage = (e) => {
        logLine(`RECEIVED: ${e.data}`);
    };

    websocket.onerror = (e) => {
        logLine(`ERROR: ${e.data}`);
    };
})