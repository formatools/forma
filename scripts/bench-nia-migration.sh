#!/usr/bin/env bash
# Same-host NiA upstream vs Forma spike migration benchmark (F-115 perf gap).
#
# Measures:
#   - cold configuration (help --no-configuration-cache --profile)
#   - warm configuration (second help --no-configuration-cache --profile)
#   - configuration-cache hit (help --profile, second run when CC enabled)
#   - assembleDemoDebug wall clock (optional; set BENCH_ASSEMBLE=1)
#   - LOC surface metrics (module scripts + org build logic)
#
# Usage (from Forma repo root or anywhere):
#   bash scripts/bench-nia-migration.sh
#   BENCH_ASSEMBLE=1 bash scripts/bench-nia-migration.sh
#   BENCH_OFFLINE=0 bash scripts/bench-nia-migration.sh   # allow network
#
# Env overrides:
#   UPSTREAM_DIR  default /Users/claw/work/nowinandroid
#   SPIKE_DIR     default /Users/claw/work/nowinandroid-forma/forma-spike
#   OUT_DIR       default <forma>/docs/benchmarks/nia-migration-<timestamp>
#   JAVA_HOME / ANDROID_HOME — if unset, uses worker Mac defaults (openjdk@21 + cmdline-tools)

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
UPSTREAM_DIR="${UPSTREAM_DIR:-/Users/claw/work/nowinandroid}"
SPIKE_DIR="${SPIKE_DIR:-/Users/claw/work/nowinandroid-forma/forma-spike}"
STAMP="$(date -u +%Y%m%dT%H%M%SZ)"
OUT_DIR="${OUT_DIR:-$ROOT/docs/benchmarks/nia-migration-$STAMP}"
BENCH_ASSEMBLE="${BENCH_ASSEMBLE:-1}"
BENCH_OFFLINE="${BENCH_OFFLINE:-0}"
RUNS_COLD="${RUNS_COLD:-1}"
RUNS_WARM="${RUNS_WARM:-1}"

if [[ -z "${JAVA_HOME:-}" ]]; then
  for c in \
    /usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home \
    /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
  do
    if [[ -x "$c/bin/java" ]]; then JAVA_HOME="$c"; break; fi
  done
fi
export JAVA_HOME="${JAVA_HOME:?JAVA_HOME not found}"
export ANDROID_HOME="${ANDROID_HOME:-/usr/local/share/android-commandlinetools}"
export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

OFFLINE_FLAG=()
if [[ "$BENCH_OFFLINE" == "1" ]]; then
  OFFLINE_FLAG=(--offline)
fi

# Safe expand under `set -u` when array may be empty.
gradle_extra_flags() {
  if ((${#OFFLINE_FLAG[@]})); then
    printf '%s\n' "${OFFLINE_FLAG[@]}"
  fi
}

mkdir -p "$OUT_DIR/logs" "$OUT_DIR/profiles"
SUMMARY_MD="$OUT_DIR/RESULTS.md"
SUMMARY_TSV="$OUT_DIR/results.tsv"
: >"$SUMMARY_TSV"
echo -e "tree\tphase\tmetric\tvalue_ms\tnotes" >>"$SUMMARY_TSV"

log() { printf '[bench] %s\n' "$*" | tee -a "$OUT_DIR/logs/bench.log"; }

ensure_local_properties() {
  local dir="$1"
  local lp="$dir/local.properties"
  if [[ ! -f "$lp" ]] || ! grep -q '^sdk.dir=' "$lp" 2>/dev/null; then
    # Gradle local.properties wants escaped Windows paths; on Unix plain path is fine.
    printf 'sdk.dir=%s\n' "$ANDROID_HOME" >"$lp"
    log "wrote $lp"
  fi
}

latest_profile() {
  local dir="$1"
  # Gradle writes build/reports/profile/profile-*.html
  ls -t "$dir"/build/reports/profile/profile-*.html 2>/dev/null | head -1 || true
}

# Extract key timings from Gradle HTML profile (ms).
# Summary table rows put the duration on the *next* text line after the label.
parse_profile_html() {
  local html="$1"
  python3 - "$html" <<'PY'
import re, sys, pathlib
html = pathlib.Path(sys.argv[1]).read_text(errors="replace")
text = re.sub(r"<script[\s\S]*?</script>", "", html, flags=re.I)
text = re.sub(r"<style[\s\S]*?</style>", "", text, flags=re.I)
text = re.sub(r"<[^>]+>", "\n", text)
lines = [re.sub(r"\s+", " ", ln).strip() for ln in text.splitlines()]
lines = [ln for ln in lines if ln]

def to_ms(s):
    s = s.strip().lower().replace(",", "")
    m = re.match(r"(?:(\d+)m\s*)?(\d+(?:\.\d+)?)s$", s)
    if m:
        return int(round((int(m.group(1) or 0) * 60 + float(m.group(2))) * 1000))
    m = re.match(r"(\d+(?:\.\d+)?)ms$", s)
    if m:
        return int(round(float(m.group(1))))
    return None

wanted = {
    "Total Build Time": "total_build",
    "Startup": "startup",
    "Settings and buildSrc": "settings_and_buildSrc",
    "Loading Projects": "loading_projects",
    "Configuring Projects": "configuring_projects",
    "Task Execution": "task_execution",
    "Artifact Transforms": "artifact_transforms",
}
out = {}
for i, line in enumerate(lines):
    key = wanted.get(line)
    if key is None or key in out or i + 1 >= len(lines):
        continue
    nxt = lines[i + 1]
    ms = to_ms(nxt)
    if ms is None:
        continue
    out[key] = (ms, nxt)
for k, (ms, raw) in out.items():
    print(f"{k}\t{ms}\t{raw}")
PY
}

record_metric() {
  local tree="$1" phase="$2" metric="$3" value_ms="$4" notes="${5:-}"
  echo -e "${tree}\t${phase}\t${metric}\t${value_ms}\t${notes}" >>"$SUMMARY_TSV"
}

run_gradle_timed() {
  # args: tree_name workdir label extra_gradle_args...
  local tree="$1" workdir="$2" phase="$3"
  shift 3
  local logf="$OUT_DIR/logs/${tree}-${phase}.log"
  log "RUN $tree/$phase in $workdir — $*"
  local start end elapsed
  start=$(python3 -c 'import time; print(int(time.time()*1000))')
  set +e
  (
    cd "$workdir"
    # shellcheck disable=SC2046
    ./gradlew "$@" $(gradle_extra_flags)
  ) >"$logf" 2>&1
  local rc=$?
  set -e
  end=$(python3 -c 'import time; print(int(time.time()*1000))')
  elapsed=$((end - start))
  record_metric "$tree" "$phase" "wall_ms" "$elapsed" "exit=$rc"
  log "  wall_ms=$elapsed exit=$rc log=$logf"
  if [[ $rc -ne 0 ]]; then
    log "  FAILED — tail:"
    tail -n 40 "$logf" | tee -a "$OUT_DIR/logs/bench.log" || true
    return $rc
  fi
  # copy profile if present
  local prof
  prof=$(latest_profile "$workdir")
  if [[ -n "$prof" ]]; then
    local dest="$OUT_DIR/profiles/${tree}-${phase}.html"
    cp "$prof" "$dest"
    log "  profile → $dest"
    while IFS=$'\t' read -r metric ms raw; do
      [[ "$metric" == "raw" ]] && continue
      if [[ -n "${ms:-}" ]]; then
        record_metric "$tree" "$phase" "$metric" "$ms" "$raw"
      fi
    done < <(parse_profile_html "$dest" | grep -v '^raw')
  else
    log "  no profile html found"
  fi
  return 0
}

loc_metrics() {
  local up="$1" sp="$2"
  python3 - "$up" "$sp" "$OUT_DIR/loc.json" <<'PY'
import json, pathlib, sys, subprocess

def wc_lines(paths):
    n = 0
    for p in paths:
        try:
            n += sum(1 for _ in open(p, "rb"))
        except OSError:
            pass
    return n

def find(root, name, exclude_parts):
    root = pathlib.Path(root)
    out = []
    for p in root.rglob(name):
        s = str(p)
        if any(x in s for x in exclude_parts):
            continue
        out.append(p)
    return out

up, sp, outp = sys.argv[1:4]
ex = ["/build/", "/.gradle/", "/build-logic/"]
up_mods = find(up, "build.gradle.kts", ex + ["/build-logic/"])
# also exclude benchmarks/lint tooling apps from upstream product surface? keep all product modules
# case study excluded only build-logic
sp_mods = find(sp, "build.gradle.kts", ["/build/", "/.gradle/"])
up_bl = find(pathlib.Path(up)/"build-logic", "*.kt", ["/build/"])
sp_fd = find(pathlib.Path(sp)/"forma-defs", "*.kt", ["/build/"])

def count_android_blocks(paths):
    c = 0
    for p in paths:
        t = pathlib.Path(p).read_text(errors="replace")
        c += t.count("android {")
    return c

def count_with_plugin(paths):
    c = 0
    for p in paths:
        t = pathlib.Path(p).read_text(errors="replace")
        c += t.count(".withPlugin")
    return c

data = {
    "upstream_module_build_files": len(up_mods),
    "upstream_module_build_loc": wc_lines(up_mods),
    "spike_module_build_files": len(sp_mods),
    "spike_module_build_loc": wc_lines(sp_mods),
    "upstream_build_logic_kt_files": len(up_bl),
    "upstream_build_logic_kt_loc": wc_lines(up_bl),
    "spike_forma_defs_kt_files": len(sp_fd),
    "spike_forma_defs_kt_loc": wc_lines(sp_fd),
    "spike_raw_android_blocks": count_android_blocks(sp_mods),
    "spike_withPlugin": count_with_plugin(list(pathlib.Path(sp).rglob("*")) if False else sp_mods),
}
# full-tree withPlugin scan on spike (scripts only)
wp = 0
for p in pathlib.Path(sp).rglob("*"):
    if p.suffix in {".kt", ".kts"} and "build" not in p.parts and ".gradle" not in p.parts:
        try:
            wp += p.read_text(errors="replace").count(".withPlugin")
        except OSError:
            pass
data["spike_withPlugin_tree"] = wp
pathlib.Path(outp).write_text(json.dumps(data, indent=2) + "\n")
print(json.dumps(data, indent=2))
PY
}

stop_daemons() {
  local dir="$1"
  # --stop can hang when no daemon exists / wrapper is slow; never block the bench.
  (cd "$dir" && ./gradlew --stop >/dev/null 2>&1) &
  local spid=$!
  local i=0
  while kill -0 "$spid" 2>/dev/null; do
    i=$((i+1))
    if [[ $i -ge 30 ]]; then
      kill "$spid" 2>/dev/null || true
      wait "$spid" 2>/dev/null || true
      log "gradlew --stop timed out in $dir (ignored)"
      return 0
    fi
    sleep 1
  done
  wait "$spid" 2>/dev/null || true
}

bench_tree() {
  local tree="$1" dir="$2" assemble_task="$3"
  ensure_local_properties "$dir"
  log "=== tree=$tree dir=$dir ==="

  # Stop daemons so cold-ish start is fairer across trees (shared daemon JVM still possible).
  stop_daemons "$dir"
  rm -rf "$dir/.gradle/configuration-cache" 2>/dev/null || true

  # One dependency resolution pass (online) if offline requested later — pre-warm caches unless offline forced only.
  if [[ "$BENCH_OFFLINE" != "1" ]]; then
    log "pre-resolve help (online) for $tree"
    (cd "$dir" && ./gradlew help --no-configuration-cache >"$OUT_DIR/logs/${tree}-preresolve.log" 2>&1) || {
      log "pre-resolve failed for $tree"; tail -n 50 "$OUT_DIR/logs/${tree}-preresolve.log" || true
      return 1
    }
  fi

  stop_daemons "$dir"
  rm -rf "$dir/.gradle/configuration-cache" 2>/dev/null || true

  local i
  for ((i=1; i<=RUNS_COLD; i++)); do
    run_gradle_timed "$tree" "$dir" "cold_help_${i}" help --no-configuration-cache --profile || log "cold_help_${i} failed"
  done

  for ((i=1; i<=RUNS_WARM; i++)); do
    run_gradle_timed "$tree" "$dir" "warm_help_${i}" help --no-configuration-cache --profile || log "warm_help_${i} failed"
  done

  # Configuration cache path (if project supports it)
  rm -rf "$dir/.gradle/configuration-cache" 2>/dev/null || true
  run_gradle_timed "$tree" "$dir" "cc_miss_help" help --configuration-cache --profile || log "cc_miss skipped/failed"
  run_gradle_timed "$tree" "$dir" "cc_hit_help" help --configuration-cache --profile || log "cc_hit skipped/failed"

  if [[ "$BENCH_ASSEMBLE" == "1" ]]; then
    stop_daemons "$dir"
    # clean assemble is expensive; do one non-clean demo debug with profile
    run_gradle_timed "$tree" "$dir" "assemble_demo_debug" "$assemble_task" --profile || log "assemble failed for $tree"
  fi
}

# Host meta
{
  echo "stamp=$STAMP"
  echo "host=$(hostname)"
  echo "date=$(date)"
  echo "java=$("$JAVA_HOME/bin/java" -version 2>&1 | head -1)"
  echo "JAVA_HOME=$JAVA_HOME"
  echo "ANDROID_HOME=$ANDROID_HOME"
  echo "UPSTREAM_DIR=$UPSTREAM_DIR"
  echo "SPIKE_DIR=$SPIKE_DIR"
  echo "BENCH_ASSEMBLE=$BENCH_ASSEMBLE"
  echo "BENCH_OFFLINE=$BENCH_OFFLINE"
} | tee "$OUT_DIR/meta.txt"

log "LOC metrics"
loc_metrics "$UPSTREAM_DIR" "$SPIKE_DIR" | tee "$OUT_DIR/logs/loc.txt"

# Upstream assemble task name
UP_ASSEMBLE=":app:assembleDemoDebug"
SP_ASSEMBLE=":binary:assembleDemoDebug"

bench_tree "upstream" "$UPSTREAM_DIR" "$UP_ASSEMBLE"
bench_tree "spike" "$SPIKE_DIR" "$SP_ASSEMBLE"

# Write RESULTS.md
python3 - "$SUMMARY_TSV" "$OUT_DIR/loc.json" "$SUMMARY_MD" "$OUT_DIR/meta.txt" <<'PY'
import csv, json, pathlib, sys, collections
tsv, locp, outp, metap = sys.argv[1:5]
rows = list(csv.DictReader(open(tsv), delimiter="\t"))
loc = json.loads(pathlib.Path(locp).read_text()) if pathlib.Path(locp).exists() else {}
meta = pathlib.Path(metap).read_text()

by = collections.defaultdict(dict)
for r in rows:
    key = (r["tree"], r["phase"], r["metric"])
    by[key] = r

def get(tree, phase, metric):
    r = by.get((tree, phase, metric))
    return r["value_ms"] if r and r["value_ms"] else None

def fmt_ms(ms):
    if ms is None or ms == "":
        return "—"
    ms = int(float(ms))
    if ms >= 1000:
        return f"{ms/1000:.2f}s ({ms} ms)"
    return f"{ms} ms"

phases = [
    ("cold_help_1", "Cold `help` (no CC, profile)"),
    ("warm_help_1", "Warm `help` (no CC, profile)"),
    ("cc_miss_help", "CC miss `help`"),
    ("cc_hit_help", "CC hit `help`"),
    ("assemble_demo_debug", "assembleDemoDebug (profile)"),
]
metrics = [
    ("wall_ms", "Wall clock"),
    ("total_build", "Profile: total build"),
    ("configuring_projects", "Profile: configuring projects"),
    ("loading_projects", "Profile: loading projects"),
    ("task_execution", "Profile: task execution"),
]

lines = []
lines.append("# NiA → Forma migration benchmark results")
lines.append("")
lines.append("Generated by `scripts/bench-nia-migration.sh`.")
lines.append("")
lines.append("## Host")
lines.append("")
lines.append("```")
lines.append(meta.rstrip())
lines.append("```")
lines.append("")
lines.append("## LOC surface (re-measured)")
lines.append("")
lines.append("| Metric | Upstream | Spike | Δ |")
lines.append("|--------|----------|-------|---|")
u, s = loc.get("upstream_module_build_loc"), loc.get("spike_module_build_loc")
if u and s:
    lines.append(f"| Module `build.gradle.kts` LOC | {u} | {s} | {(s-u)/u*100:+.0f}% |")
u, s = loc.get("upstream_build_logic_kt_loc"), loc.get("spike_forma_defs_kt_loc")
if u and s:
    lines.append(f"| Org build logic Kotlin LOC | {u} | {s} | {(s-u)/u*100:+.0f}% |")
lines.append(f"| Spike raw `android {{` blocks | — | {loc.get('spike_raw_android_blocks')} | |")
lines.append(f"| Spike `.withPlugin` | — | {loc.get('spike_withPlugin_tree')} | |")
lines.append("")
lines.append("## Timing (same host)")
lines.append("")
lines.append("| Phase | Metric | Upstream | Spike | Spike − Upstream |")
lines.append("|-------|--------|----------|-------|------------------|")
for phase, label in phases:
    for metric, mlabel in metrics:
        u = get("upstream", phase, metric)
        s = get("spike", phase, metric)
        if u is None and s is None:
            continue
        try:
            du = int(float(u)) if u not in (None, "") else None
            ds = int(float(s)) if s not in (None, "") else None
            if du is not None and ds is not None:
                delta = f"{(ds-du)/1000:+.2f}s" if abs(ds-du)>=1000 else f"{ds-du:+d} ms"
                pct = f" ({(ds-du)/du*100:+.0f}%)" if du else ""
                delta = delta + pct
            else:
                delta = "—"
        except Exception:
            delta = "—"
        lines.append(f"| {label} | {mlabel} | {fmt_ms(u)} | {fmt_ms(s)} | {delta} |")

lines.append("")
lines.append("## How to read")
lines.append("")
lines.append("- **Configuring projects** is the pure configuration cost (Forma validators + AGP/Hilt apply).")
lines.append("- Graphs are similar module counts but **not identical** (spike omits Roborazzi/baseline/lint apps; upstream has fuller tooling). Treat as order-of-magnitude, not a lab microbench.")
lines.append("- Wrappers differ (NiA Gradle pin vs spike 9.6.x). Document both; do not claim a single-factor cause from wall clock alone.")
lines.append("- Prefer paired runs on this host; absolute seconds move with load and daemon state.")
lines.append("")
lines.append("## Raw")
lines.append("")
lines.append("- `results.tsv` — all metrics")
lines.append("- `profiles/*.html` — Gradle `--profile` HTML")
lines.append("- `logs/` — full gradle stdout")
lines.append("")
pathlib.Path(outp).write_text("\n".join(lines) + "\n")
print("wrote", outp)
PY

log "DONE out=$OUT_DIR"
log "See $SUMMARY_MD"
cat "$SUMMARY_MD"
