"""Forma JVM restriction matrix, implemented in Starlark.

Mirrors `JvmTargetRegistry.registerJvmDefaults()` / F-041 `JvmBazelAdapter`.
Call sites stay attrs-only; this module is the type-owned allow-list.

`impl` ↛ `impl` is a hard fail at loading/analysis — not a visibility accident.
"""

# Exact JVM kit allow-list (consumer type → allowed producer types).
ALLOWED_DEPS = {
    "jvm.api": ["jvm.api", "jvm.library"],
    "jvm.impl": ["jvm.api", "jvm.library", "jvm.util", "jvm.test-util"],
    "jvm.library": ["jvm.util", "jvm.test-util"],
    "jvm.util": ["jvm.util", "jvm.library"],
    "jvm.test-util": ["jvm.test-util", "jvm.util", "jvm.library"],
    "jvm.binary": ["jvm.api", "jvm.impl", "jvm.library", "jvm.util", "jvm.test-util"],
}

_SUFFIX_TO_TYPE = {
    "api": "jvm.api",
    "impl": "jvm.impl",
    "library": "jvm.library",
    "util": "jvm.util",
    "test-util": "jvm.test-util",
    "binary": "jvm.binary",
}

def infer_jvm_type(label):
    """Infer `jvm.*` type id from a Bazel label using Forma suffix conventions.

    `//feature/greeter/impl:impl` → `jvm.impl`
    `//common/test-util:test-util` → `jvm.test-util`
    `//foo:feature-greeter-impl` → `jvm.impl`
    """
    if type(label) != "string" or not label:
        fail("Forma dep must be a label string, got: %s" % type(label))
    name = label.split(":")[-1]
    if name in _SUFFIX_TO_TYPE:
        return _SUFFIX_TO_TYPE[name]
    for suffix, tid in _SUFFIX_TO_TYPE.items():
        if name.endswith("-" + suffix):
            return tid
    fail("Cannot infer Forma type from label %s (expected suffix api/impl/library/util/test-util/binary)" % label)

def check_deps(consumer_type, deps, _fail = fail):
    """Fail if any dep is outside the closed matrix for `consumer_type`.

    `_fail` is injectable so Starlark unit tests can assert the error without
    aborting the test suite.
    """
    if consumer_type not in ALLOWED_DEPS:
        _fail("Unknown Forma consumer type: %s" % consumer_type)
        return
    allowed = ALLOWED_DEPS[consumer_type]
    for dep in deps:
        producer = infer_jvm_type(dep)
        if producer not in allowed:
            _fail("Illegal Forma dependency: %s → %s (%s)" % (consumer_type, producer, dep))
            return
