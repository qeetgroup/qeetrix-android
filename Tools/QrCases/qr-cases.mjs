// The expected modules for QeetrixQREncoderTest: @qeetrix/react's own encodeQr (dist/experimental/qr-code/encode.js) run
// on a set of values at every level. Pass the unpacked package directory (the one holding dist/):
//
//   node Tools/QrCases/qr-cases.mjs <@qeetrix/react package dir> > qeetrix/src/test/resources/qeetrix/qr-cases.json
import { pathToFileURL } from "node:url";
import { join } from "node:path";
import { readFileSync } from "node:fs";

const dir = process.argv[2];
if (!dir) throw new Error("usage: node qr-cases.mjs <@qeetrix/react package dir>");
const { version } = JSON.parse(readFileSync(join(dir, "package.json"), "utf8"));
const { encodeQr } = await import(pathToFileURL(join(dir, "dist/experimental/qr-code/encode.js")).href);
const values = [
  "Q",
  "https://qeet.in",
  "otpauth://totp/Acme:priya@acme.in?secret=JBSWY3DPEHPK3PXP&issuer=Acme&algorithm=SHA1&digits=6&period=30",
  "upi://pay?pa=acme@okhdfc&pn=Acme%20Industries&am=12400.00&cu=INR&tn=Invoice%20INV-2026-0042",
  "नमस्ते, Qeet — ₹1,24,500",
  "A".repeat(150),
  "0123456789".repeat(40),
];
const cases = [];
for (const value of values)
  for (const level of ["L", "M", "Q", "H"]) {
    const modules = encodeQr(value, level);
    cases.push({ value, level, size: modules.length, rows: modules.map((row) => row.map((on) => (on ? "1" : "0")).join("")) });
  }
console.log(JSON.stringify({ $comment: `Modules from @qeetrix/react ${version}'s encodeQr, row by row, 1 for dark. Regenerate with Tools/QrCases/qr-cases.mjs.`, cases }, null, 1));
