MIUIX Migration Roadmap

This file documents the immediate next steps and mapping suggestions for migrating the project UI to MIUIX.

1) Theme adapter
   - We added a minimal MiuixTheme adapter at design-system/src/main/kotlin/.../MiuixThemeAdapter.kt
   - Replace the body of MiuixTheme.Theme with the MIUIX theme implementation once dependencies/coordinates are confirmed.

2) Component adapter
   - We added PlatformButton, PlatformTopAppBar, PlatformTextField in design-system/src/main/kotlin/.../PlatformComponents.kt.
   - Replace their implementations with MIUIX components (one place) to switch the app UI gradually.

3) Replace imports in app code (recommended approach)
   - Search for usages of androidx.compose.material3 (or direct Material3 components) with:
       rg "androidx.compose.material3" -n
   - Replace usages with the PlatformX adapters, e.g.:
       - Button -> PlatformButton
       - TopAppBar/CenterAlignedTopAppBar -> PlatformTopAppBar
       - TextField/OutlinedTextField -> PlatformTextField
   - Make replacements per-file and run a quick compile after each batch.

4) Validation
   - After each batch, run:
       ./gradlew :app:compileDebugKotlin
   - Manually inspect key screens and adjust paddings/colors in the adapter or Theme adapter.

5) Cleanup
   - After all usages are migrated, remove Material3 dependency lines from app/build.gradle.kts and any unused resources.

Notes
   - The adapter implementations are intentionally minimal so they compile without MIUIX at first. When you (or I) switch to MIUIX, update the adapter internals.
   - I can now start replacing usages automatically in small batches (e.g., by searching usages and creating commits that change imports and call sites). Reply with "go" to let me begin automated replacements (I will make small commits and run safe compile checks locally in CI is recommended).
