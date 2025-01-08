function parseData(data) {
    const lines = data.split("\n");
    const leftArr = [];
    const rightArr = [];
    for (const line of lines) {
        const [left, right] = line.split("   ").map(Number);
        leftArr.push(left);
        rightArr.push(right);
    }
    return {
        left: leftArr,
        right: rightArr
    };
}

const SPC = "&nbsp;";
function isBrowser() {
    return typeof window !== "undefined";
}
function onlyBrowser(body) {
    if (isBrowser()) {
        body();
    }
}
function setAnswer(answer, answerName) {
    let msg = "Answer";
    {
        msg += ` (${answerName}) `;
    }
    msg += "is:";
    setBody(body => {
        body.innerHTML = `<span>${msg}</span>${SPC}<span class="glowing-font">${answer}</span>`;
    });
    console.log(msg, answer);
}
function setBody(bodySetter) {
    onlyBrowser(() => {
        const body = document.querySelector("#body");
        bodySetter(body);
    });
}

function findDistances(locations) {
    const distances = [];
    for (let i = 0; i < locations.left.length; i++) {
        const left = locations.left[i];
        const right = locations.right[i];
        distances.push(Math.abs(right - left));
    }
    return distances;
}
function aocMain(data) {
    const locations = parseData(data);
    locations.left.sort();
    locations.right.sort();
    const distances = findDistances(locations);
    const sum = distances.reduce((sum, curr) => sum + curr, 0);
    setAnswer(sum, "Sum of distances");
}

export { aocMain };
//# sourceMappingURL=test-output.js.map
