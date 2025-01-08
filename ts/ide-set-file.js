#!/usr/bin/env node

// WebStorm bun application arguments does not evaluate macros so use this file to tmp file with the path
import fs from "fs";

const arg = process.argv[2];

fs.writeFileSync('.aoc_launch_path', arg);
