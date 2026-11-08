// The expected strings for QeetrixFormatTest: @qeetrix/react's format rules (dist/format/format.js: formatNumber,
// formatCurrency, formatRelativeTime, formatDate, formatDateTime, and 0.5.0's formatDuration and formatTime), restated here and run on Node's Intl,
// which the web's functions call. Not the package's code: its rules, so this runs without installing it.
//
//   node Tools/FormatCases/format-cases.mjs > qeetrix/src/test/resources/qeetrix/format-cases.json
const out = [];
const num = (v, l, o = {}) => new Intl.NumberFormat(l, o).format(v);
const UNITS = [["second", 1e3, 60], ["minute", 6e4, 60], ["hour", 36e5, 24], ["day", 864e5, 7], ["week", 6048e5, 30 / 7], ["month", 2592e6, 12], ["year", 31536e6, Infinity]];
const rel = (delta, l) => {
  const f = new Intl.RelativeTimeFormat(l, { numeric: "auto" });
  for (const [u, s, n] of UNITS) { const a = Math.round(Math.abs(delta) / s); if (a < n) return f.format(a === 0 ? 0 : Math.sign(delta) * a, u); }
  return f.format(0, "second");
};
const date = (t, l, ds = "medium", ts = "", tz = "Asia/Kolkata") =>
  new Intl.DateTimeFormat(l, { dateStyle: ds, ...(ts ? { timeStyle: ts } : {}), calendar: "gregory", timeZone: tz }).format(t);
for (const l of ["en-IN", "hi-IN", "en-US", "ar-EG", "de-DE"]) {
  for (const v of [0, 7, 1234.5, 1234567, -98765.4321, 0.5]) out.push({ fn: "number", locale: l, value: v, out: num(v, l) });
  for (const v of [124500, 99.5, 0, 1234567.891]) {
    out.push({ fn: "currency", locale: l, value: v, currency: "INR", out: num(v, l, { style: "currency", currency: "INR" }) });
    out.push({ fn: "currency", locale: l, value: v, currency: "USD", out: num(v, l, { style: "currency", currency: "USD" }) });
  }
  out.push({ fn: "currencyWhole", locale: l, value: 124500, currency: "INR", out: num(124500, l, { style: "currency", currency: "INR", maximumFractionDigits: 0 }) });
  for (const v of [0.25, 0.031, 1.5]) out.push({ fn: "percent", locale: l, value: v, out: num(v, l, { style: "percent" }) });
  const S = 1e3, M = 6e4, H = 36e5, D = 864e5;
  for (const d of [0, -10 * S, -45 * S, -59 * S - 600, -2 * M, -59 * M - 40 * S, -3 * H, -23 * H, -D, -2 * D, 3 * D, -6 * D, -8 * D, -27 * D, -30 * D, -45 * D, -364 * D, -400 * D, 2 * 365 * D, M, H])
    out.push({ fn: "relative", locale: l, delta: d, out: rel(d, l) });
  const instant = Date.UTC(2026, 9, 25, 9, 0, 0); // 14:30 in Asia/Kolkata
  for (const ds of ["short", "medium", "long"]) {
    out.push({ fn: "dateInstant", locale: l, epochMillis: instant, dateStyle: ds, out: date(instant, l, ds) });
    out.push({ fn: "plainDate", locale: l, date: "2026-10-25", dateStyle: ds, out: date(Date.UTC(2026, 9, 25), l, ds, "", "UTC") });
  }
  for (const ts of ["short", "medium"]) out.push({ fn: "dateTimeInstant", locale: l, epochMillis: instant, dateStyle: "medium", timeStyle: ts, out: date(instant, l, "medium", ts) });
  out.push({ fn: "dateTimeInstantZone", locale: l, epochMillis: instant, zone: "America/New_York", out: date(instant, l, "medium", "short", "America/New_York") });
  // Durations: Intl.DurationFormat, as the web's formatDuration writes them and QeetrixFormat.duration matches.
  for (const style of ["long", "short", "narrow"]) {
    for (const [seconds, parts] of [[3900, { hours: 1, minutes: 5 }], [172803, { days: 2, seconds: 3 }], [45, { seconds: 45 }], [90061, { days: 1, hours: 1, minutes: 1, seconds: 1 }]])
      out.push({ fn: "duration", locale: l, seconds, style, out: new Intl.DurationFormat(l, { style }).format(parts) });
  }
  // Times: Intl.DateTimeFormat's timeStyle, as the web's formatTime writes them — the time part formatDateTime
  // writes — with hourCycle for a phone's 12- or 24-hour setting.
  const morning = Date.UTC(2026, 9, 25, 5, 17, 9); // 10:47:09 in Asia/Kolkata
  for (const t of [instant, morning])
    for (const ts of ["short", "medium"])
      for (const hc of ["", "h23", "h12"])
        out.push({
          fn: "time", locale: l, epochMillis: t, timeStyle: ts, hourCycle: hc,
          out: new Intl.DateTimeFormat(l, { timeStyle: ts, timeZone: "Asia/Kolkata", ...(hc ? { hourCycle: hc } : {}) }).format(t),
        });
  // Just before midnight UTC on 31 Dec: a later day in Kolkata, so the zone matters for an instant.
  const nye = Date.UTC(2026, 11, 31, 20, 0, 0);
  out.push({ fn: "dateInstant", locale: l, epochMillis: nye, dateStyle: "medium", out: date(nye, l, "medium") });
}
const doc = {
  $comment:
    "Expected strings for QeetrixFormat: @qeetrix/react's format rules (dist/format/format.js: formatNumber, " +
    "formatCurrency, formatRelativeTime, formatDate, formatDateTime, formatDuration, formatTime), restated in Tools/FormatCases/format-cases.mjs and run " +
    `on Node ${process.versions.node}'s Intl (ICU ${process.versions.icu}, CLDR ${process.versions.cldr}). Relative cases give the ` +
    "moment's distance from now in ms; date cases an instant (shown in Asia/Kolkata unless a zone is named) or a calendar " +
    "date. Duration cases are Intl.DurationFormat's and time cases Intl.DateTimeFormat's timeStyle, as formatDuration and formatTime (0.5.0) write them. Regenerate with: node Tools/FormatCases/format-cases.mjs > qeetrix/src/test/resources/qeetrix/format-cases.json",
  icu: process.versions.icu,
  cldr: process.versions.cldr,
  cases: out,
};
console.log(JSON.stringify(doc, null, 1));
