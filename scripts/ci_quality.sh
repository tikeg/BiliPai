#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

MODE="${1:-}"
if [[ $# -gt 0 ]]; then
  shift
fi

# Each workflow job owns one group; task-local filters keep module boundaries explicit.
case "$MODE" in
  performance)
    GRADLE_TASKS=(
      :ci-lint:test
        --tests 'app.bilipai.lint.PerformanceDetectorTest'
        --tests 'app.bilipai.lint.ModelStabilityDetectorTest'
      :app:lintDebug -Pbili.ciChecks=performance
      :app:testDebugUnitTest
        --tests 'app.bilipai.core.ui.transition.BlurRenderEffectCacheTest'
        --tests 'app.bilipai.core.ui.performance.AppDisplayModePreferenceTest'
        --tests 'app.bilipai.core.ui.performance.RuntimeVisualGuardTrackerTest'
      :design-system:testDebugUnitTest
        --tests 'app.bilipai.core.ui.adaptive.RuntimeVisualGuardPolicyTest'
        --tests 'app.bilipai.core.ui.adaptive.RuntimeVisualGuardSignalPolicyTest'
    )
    ;;
  correctness)
    GRADLE_TASKS=(
      :app:testDebugUnitTest
        --tests 'app.bilipai.feature.settings.AppUpdateCheckerTest'
        --tests 'app.bilipai.core.store.SerializedSettingsCacheWriterTest'
        --tests 'app.bilipai.core.store.AppNavigationSettingsMappingPolicyTest.gestureRotationAndTranslationPreferencesAreIndependent'
        --tests 'app.bilipai.core.store.AppNavigationSettingsMappingPolicyTest.populatedPreferences_mapToNavigationSettingsCorrectly'
        --tests 'app.bilipai.core.store.ThemeSelectionMigrationPolicyTest'
    )
    ;;
  style)
    GRADLE_TASKS=(
      :ci-lint:test
        --tests 'app.bilipai.lint.StyleDetectorTest'
      :app:lintDebug -Pbili.ciChecks=style
    )
    ;;
  *)
    echo "Usage: bash scripts/ci_quality.sh {performance|correctness|style} [Gradle options...]" >&2
    exit 2
    ;;
esac

# Keep independent reports without hiding compilation, checker or behavior failures.
# CI 可通过 CI_GRADLE_MAX_WORKERS 覆盖 worker 数（默认沿用 gradle.properties）。
MAX_WORKERS_ARGS=()
if [[ -n "${CI_GRADLE_MAX_WORKERS:-}" ]]; then
  MAX_WORKERS_ARGS=(--max-workers "$CI_GRADLE_MAX_WORKERS")
fi
bash ./gradlew ${MAX_WORKERS_ARGS[@]+"${MAX_WORKERS_ARGS[@]}"} "${GRADLE_TASKS[@]}" --continue --no-daemon --console=plain "$@"
