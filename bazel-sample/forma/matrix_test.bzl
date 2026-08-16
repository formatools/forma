"""Starlark unit tests for the Forma JVM matrix (bazel-skylib unittest)."""

load("@bazel_skylib//lib:unittest.bzl", "asserts", "unittest")
load(":matrix.bzl", "check_deps", "infer_jvm_type")

def _infer_test_impl(ctx):
    env = unittest.begin(ctx)
    asserts.equals(env, "jvm.impl", infer_jvm_type("//feature/greeter/impl:impl"))
    asserts.equals(env, "jvm.api", infer_jvm_type("//feature/greeter/api:api"))
    asserts.equals(env, "jvm.library", infer_jvm_type("//common/library:library"))
    asserts.equals(env, "jvm.util", infer_jvm_type("//common/util:util"))
    asserts.equals(env, "jvm.test-util", infer_jvm_type("//common/test-util:test-util"))
    asserts.equals(env, "jvm.binary", infer_jvm_type("//binary:binary"))
    asserts.equals(env, "jvm.impl", infer_jvm_type("//whatever:feature-greeter-impl"))
    return unittest.end(env)

infer_test = unittest.make(_infer_test_impl)

def _legal_deps_test_impl(ctx):
    env = unittest.begin(ctx)
    recorded = []

    def _record(msg):
        recorded.append(msg)

    check_deps(
        "jvm.impl",
        [
            "//feature/greeter/api:api",
            "//common/library:library",
            "//common/util:util",
        ],
        _fail = _record,
    )
    asserts.equals(env, [], recorded)

    check_deps(
        "jvm.binary",
        [
            "//feature/greeter/impl:impl",
            "//feature/calculator/impl:impl",
            "//feature/greeter/api:api",
        ],
        _fail = _record,
    )
    asserts.equals(env, [], recorded)
    return unittest.end(env)

legal_deps_test = unittest.make(_legal_deps_test_impl)

def _illegal_impl_to_impl_test_impl(ctx):
    env = unittest.begin(ctx)
    recorded = []

    def _record(msg):
        recorded.append(msg)

    check_deps(
        "jvm.impl",
        [
            "//feature/greeter/api:api",
            "//feature/calculator/impl:impl",
        ],
        _fail = _record,
    )
    asserts.equals(env, 1, len(recorded))
    asserts.true(env, "jvm.impl → jvm.impl" in recorded[0])
    asserts.true(env, "//feature/calculator/impl:impl" in recorded[0])
    return unittest.end(env)

illegal_impl_to_impl_test = unittest.make(_illegal_impl_to_impl_test_impl)

def _illegal_api_to_impl_test_impl(ctx):
    env = unittest.begin(ctx)
    recorded = []

    def _record(msg):
        recorded.append(msg)

    check_deps("jvm.api", ["//feature/greeter/impl:impl"], _fail = _record)
    asserts.equals(env, 1, len(recorded))
    asserts.true(env, "jvm.api → jvm.impl" in recorded[0])
    return unittest.end(env)

illegal_api_to_impl_test = unittest.make(_illegal_api_to_impl_test_impl)

def forma_matrix_test_suite():
    unittest.suite(
        "forma_matrix_tests",
        infer_test,
        legal_deps_test,
        illegal_impl_to_impl_test,
        illegal_api_to_impl_test,
    )
