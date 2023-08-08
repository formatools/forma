# List of supported targets

### androidBinary
The androidBinary target handles packaging Android application bundles into distributable APK files and publishing app artifacts. It contains Gradle tasks for generating final release builds, running ProGuard, signing the APK, versioning, etc.

An androidBinary can only depend on an androidApp target. The binary encapsulates distribution logic and therefore needs to access the app configuration like flags, keys, features etc. But the app code itself should not depend on the binary. This prevents the app layer from getting coupled to low level packaging details.

### androidApp
The androidApp target is used to configure and package Android applications. It contains code, resources and configurations like feature flags, keys, debug modes etc. specific to an app.

An androidApp cannot depend on androidBinary. The app target should only provide high level application configurations, components and resources. Depending on binary could tightly couple the app code to low level distribution details like signing, ProGuard, versioning etc. Isolating androidApp allows changing distribution methods without impacting app code.

### androidLibrary
The androidLibrary target defines reusable components and code modules for Android. It contains business logic, data layers, UI widgets, custom views, etc that can be consumed by multiple androidApps or other libraries.

An androidLibrary is allowed to depend on other androidLibraries, androidWidgets, and androidUtils. This allows building up a graph of modular, decoupled libraries with clean APIs. However, androidLibrary cannot depend directly on androidApp or androidBinary targets. This prevents libraries from making assumptions about higher level application code, or leaking implementation details through the public API. Keeping the library layer decoupled maximizes reusability across different apps.

### androidWidget
The androidWidget target provides reusable UI components that can be shared between apps. These contain layout XML, drawables, custom views, composables, etc. that abstract UI elements away from app code.

Like libraries, androidWidgets can depend on other androidWidgets as well as androidUtils. But they are restricted from directly accessing androidApps and androidBinaries. This prevents widgets from making assumptions about Activities, Fragments, navigation, styling, app ID, signing configs, etc. Isolating widgets as truly generic UI components increases reusability.

### androidRes
The androidRes target contains Android resources and assets like strings, dimensions, themes, layouts, drawables, animations etc. Resources are bundled separately from code for localization and theming.

An androidRes can depend only on other androidRes targets. This ensures resources and assets remain free of logic and implementation details. Allowing androidRes to access app code could result in resources becoming coupled to specific Activities, libraries or components. Keeping resource dependencies isolated maximizes reuse and dead code elimination.

### androidTestUtils
The androidTestUtils target provides utilities and helpers for instrumentation and UI tests on Android. This includes test runners, JUnit rules, Espresso actions, device connectors, etc.

androidTestUtils can depend only on other testUtils and androidRes targets. This prevents test helpers from leaking into app or library implementation code. Test utils should contain only generic test logic reusable across tests. Dependencies on app code could violate isolation and introduce flakiness. Keeping them isolated improves reliability.

### api
The api target defines public interface contracts for components. These contain abstract classes, method signatures, data classes, annotations, etc. Api targets form the explicitly defined public APIs of libraries.

An api can only depend on other api targets. This prevents implementation details from leaking into public facing APIs. Restricting dependencies forces clean separation of interface from implementation. If api could access impl targets, it could expose internal component details as public API contracts.

### impl
The impl target provides concrete implementations of one api target interface. These contain business logic, platform integrations, algorithms, etc behind the public api.

An impl can only depend on its own api target. This enforces a 1:1 relationship between interface and implementation. Preventing impl-to-impl dependencies avoids tangled layers of hidden implementation logic. The impl contains private code to power the api contract. Isolating it avoids leaking internals.

### androidUtil
The androidUtil target provides Android specific utilities and platform extensions. These contain code to wrap Android SDK APIs, system services, device integrations etc.

androidUtils can be used by Android libraries, widgets and resources. But they cannot depend on general utils, to prevent Android specific logic from leaking out. For example, a util accessing Android SDKs could limit its reuse on other platforms. Isolating androidUtil preserves reusability.

### util
The util target provides general purpose utility code not specific to Android. This includes data types, helpers, extensions, algorithms etc. Usable across multiple platforms.

A util can only depend on other util targets. This prevents Android specific logic from entering generic utilities. For example a util accessing androidUtils could assume certain Android APIs are always available. Keeping utils isolated maximizes reusability beyond just Android.

### testUtil
The testUtil target provides reusable utilities for tests like mocks, fakes, assertions, spies, etc. These help abstract test code from implementation details.

testUtils can access androidTestUtils and utils only. Dependencies on app or library code could introduce flakiness and test leakage. Isolating testUtils forces truly generic reusable test logic, improving reliability.
