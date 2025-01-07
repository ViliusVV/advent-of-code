import {parseData} from "./common.ts";
import {logOutput, removeAt, setAnswer} from "../../lib/utils.ts";

function printSafe(report, afterRemoved) {
    let info = " "
    if(afterRemoved) {
        info += " after lvl removal";
    }
    logOutput(`Report ${report} is safe ${info}`);
}

function printNotSafe(report, faultIdx, dec, afterRemoved) {
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

    if(afterRemoved) {
        info += " after level removal";
    }

    const text =  `Report ${report} is not safe: ${val1} - ${val2} = ${diff}; ${info}`;
    logOutput(text);
}

function isTransitionSafe(report, dec, i) {
    let diff = report[i] - report[i + 1];
    if((dec && diff < 0) || (!dec && diff > 0)) {
        return false;
    }

    diff = Math.abs(diff);

    return 1 <= diff && diff <= 3;
}

function isReportSafe(report, afterRemoved) {
    let dec = report[0] > report[1]

    for(let i = 0; i < report.length - 1; i++) {
        let safe = isTransitionSafe(report, dec, i);

        if(!safe && afterRemoved) {
            printNotSafe(report, i, dec, afterRemoved);
            return false
        }

        if(!safe && !afterRemoved) {
            const newReports = report.map((_, idx) => removeAt(report, idx));

            for(const newReport of newReports) {
                if(isReportSafe(newReport, true)) {
                    return true;
                }
            }

            logOutput("Could not find a safe report even after removing a level");
            return false;
        }
    }

    printSafe(report, afterRemoved);
    return true;
}

export function aocMain(data) {
    const reports = parseData(data);

    logOutput(reports)

    let safeReports = 0;
    for (const r of reports) {
        if (isReportSafe(r)) {
            safeReports++;
        }
    }

    setAnswer(safeReports, "Safe reports");
}