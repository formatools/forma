# Forma benchmarks

Reproducible host measurements for product claims (configuration cost, migration
surface). Not a substitute for CI unit tests.

| Suite | Script | Latest results |
|-------|--------|----------------|
| **NiA → Forma migration** (F-115 perf pair) | [`scripts/bench-nia-migration.sh`](../../scripts/bench-nia-migration.sh) | [`nia-migration-20260803T174730Z/RESULTS.md`](nia-migration-20260803T174730Z/RESULTS.md) |

## NiA migration bench

Same-host pair:

- Upstream: `/Users/claw/work/nowinandroid` (convention-plugin NiA)
- Spike: `/Users/claw/work/nowinandroid-forma/forma-spike` (external Forma consumer, `0.1.3-NIA`)

Measures cold/warm `help` (no configuration cache), configuration-cache miss/hit,
optional `assembleDemoDebug`, plus LOC surface metrics.

```bash
export JAVA_HOME=/usr/local/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home
export ANDROID_HOME=/usr/local/share/android-commandlinetools
cd /Users/claw/work/forma
BENCH_ASSEMBLE=1 bash scripts/bench-nia-migration.sh
# writes docs/benchmarks/nia-migration-<UTC stamp>/
```

**Primary metric to quote:** Gradle profile **Configuring Projects** on cold/warm
`help`. Assemble wall clock is recorded but confounded (tooling graph, wrapper
pin, cache warmth) — see RESULTS caveats.

Also see [`docs/CONFIGURATION-PERFORMANCE.md`](../CONFIGURATION-PERFORMANCE.md)
and case study §3.5 in [`docs/DOGFOOD-NIA-CASE-STUDY.md`](../DOGFOOD-NIA-CASE-STUDY.md).
