import {readData} from "../../lib/deno-file-utils.ts";
import {parseData} from "./common.ts";
import {logOutput} from "../../lib/utils.ts";


function printSafe(report) {
    logOutput(`Report ${report} is safe`);
}

function printNotSafe(report, faultIdx, dec) {
    const val1 = report[faultIdx];
    const val2 = report[faultIdx + 1];
    const diff = val1 - val2;

    let info = ""
    if (dec && diff < 0) {
        info = "Report should be decreasing but it increases";
    } else if (!dec && diff > 0) {
        info = "Report should be increasing but it decreases";
    } else if (!(1 <= diff && diff <= 3)) {
        info = "Diff is not in range 1-3";
    }
    const text =  `Report ${report} is not safe: ${val1} - ${val2} = ${diff}; ${info}`;
    logOutput(text);
}

function isReportSafe(report) {
    let dec = report[0] > report[1]

    for(let i = 0; i < report.length - 1; i++) {
        let diff = report[i] - report[i + 1];

        if((dec && diff < 0) || (!dec && diff > 0)) {
            printNotSafe(report, i, dec);
            return false
        }
        diff = Math.abs(diff);
        if(!(1 <= diff && diff <= 3)) {
            printNotSafe(report, i, dec);
            return false;
        }
    }
    printSafe(report);
    return true;
}

readData().then(data => {
    const reports = parseData(data);

    logOutput(reports)

    let safeReports = 0;
    for(const r of reports) {
        if(isReportSafe(r)) {
            safeReports++;
        }
    }

    logOutput("Safe reports", safeReports);
});

