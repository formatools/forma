"""Forma JVM target types as Starlark macros (v3 experiment).

Each public symbol is a **TargetType / rule**:
- owns rule kind (`kt_jvm_library` / `kt_jvm_binary`)
- owns `tags = [\"forma:type=...\"]`
- owns default `srcs` glob
- owns matrix enforcement via `check_deps`

Call sites set **attributes only** (`name`, `deps`, `visibility`, `main_class`).
Never re-select tools/plugins per module.
"""

load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_binary", "kt_jvm_library")
load(":matrix.bzl", "check_deps")

_DEFAULT_SRCS = ["src/main/kotlin/**/*.kt"]

def _srcs(srcs):
    return srcs if srcs != None else native.glob(_DEFAULT_SRCS)

def jvm_api(name, deps = [], visibility = None, srcs = None, **kwargs):
    """Forma `jvm.api` — contracts. Allowed deps: api, library."""
    check_deps("jvm.api", deps)
    kt_jvm_library(
        name = name,
        srcs = _srcs(srcs),
        deps = deps,
        visibility = visibility,
        tags = ["forma:type=jvm.api"],
        **kwargs
    )

def jvm_impl(name, deps = [], visibility = None, srcs = None, **kwargs):
    """Forma `jvm.impl`. Allowed deps: api, library, util, test-util. Never impl."""
    check_deps("jvm.impl", deps)
    kt_jvm_library(
        name = name,
        srcs = _srcs(srcs),
        deps = deps,
        visibility = visibility,
        tags = ["forma:type=jvm.impl"],
        **kwargs
    )

def jvm_library(name, deps = [], visibility = None, srcs = None, **kwargs):
    """Forma `jvm.library`. Allowed deps: util, test-util."""
    check_deps("jvm.library", deps)
    kt_jvm_library(
        name = name,
        srcs = _srcs(srcs),
        deps = deps,
        visibility = visibility,
        tags = ["forma:type=jvm.library"],
        **kwargs
    )

def jvm_util(name, deps = [], visibility = None, srcs = None, **kwargs):
    """Forma `jvm.util`. Allowed deps: util, library."""
    check_deps("jvm.util", deps)
    kt_jvm_library(
        name = name,
        srcs = _srcs(srcs),
        deps = deps,
        visibility = visibility,
        tags = ["forma:type=jvm.util"],
        **kwargs
    )

def jvm_test_util(name, deps = [], visibility = None, srcs = None, **kwargs):
    """Forma `jvm.test-util`. Allowed deps: test-util, util, library."""
    check_deps("jvm.test-util", deps)
    kt_jvm_library(
        name = name,
        srcs = _srcs(srcs),
        deps = deps,
        visibility = visibility,
        tags = ["forma:type=jvm.test-util"],
        **kwargs
    )

def jvm_binary(name, deps = [], visibility = None, srcs = None, main_class = None, **kwargs):
    """Forma `jvm.binary` — composition root. The only type that may depend on multiple impls."""
    check_deps("jvm.binary", deps)
    kt_jvm_binary(
        name = name,
        srcs = _srcs(srcs),
        deps = deps,
        visibility = visibility if visibility != None else ["//visibility:public"],
        main_class = main_class,
        tags = ["forma:type=jvm.binary"],
        **kwargs
    )
