package app.bilipai.lint;

import static org.junit.Assert.assertEquals;

import com.android.tools.lint.checks.infrastructure.LintDetectorTest;
import com.android.tools.lint.checks.infrastructure.ProjectDescription;
import com.android.tools.lint.checks.infrastructure.TestFile;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Issue;
import java.io.IOException;
import java.util.List;
import org.junit.Test;

/** Verifies the docs/wiki/LAYERS.md dependency-direction rules. */
public final class LayerDependencyDetectorTest extends LintDetectorTest {

    @Override
    protected Detector getDetector() {
        return new LayerDependencyDetector();
    }

    @Override
    protected List<Issue> getIssues() {
        return List.of(LayerDependencyDetector.ISSUE);
    }

    /**
     * A fixture is only analyzed when it sits under {@code <project>/src/}, so every case names
     * its project and keeps the path under {@code src/}. The project directory appears in the
     * absolute path, which is how the detector tells an application module from a library one.
     */
    private static TestFile sourceIn(String sourceRoot, String source) {
        return kotlin(source).within(sourceRoot);
    }

    /** A lower-layer module such as {@code :core-data}: its project directory is not {@code app}. */
    private static ProjectDescription lowerLayerProject(String sourceRoot, String source) {
        return new ProjectDescription(sourceIn(sourceRoot, source)).name("core-data");
    }

    @Test
    public void testLowerLayerModuleImportingFeatureIsRejected() throws IOException {
        lint().allowMissingSdk().allowCompilationErrors().projects(
                lowerLayerProject("src/main/java/app/bilipai/core/store",
                        "package app.bilipai.core.store\n"
                                + "import app.bilipai.feature.search.SearchScreen\n"
                                + "val x = SearchScreen\n"))
                .run()
                .expectErrorCount(1);
    }

    @Test
    public void testNavigation3ImportingNavigationIsRejected() throws IOException {
        lint().allowMissingSdk().allowCompilationErrors().files(
                sourceIn("src/main/java/app/bilipai/navigation3",
                        "package app.bilipai.navigation3\n"
                                + "import app.bilipai.navigation.ScreenRoutes\n"
                                + "val x = ScreenRoutes\n"))
                .run()
                .expectErrorCount(1);
    }

    @Test
    public void testLowerLayerModuleImportingSharedModelIsAllowed() throws IOException {
        lint().allowMissingSdk().allowCompilationErrors().projects(
                lowerLayerProject("src/main/java/app/bilipai/core/store",
                        "package app.bilipai.core.store\n"
                                + "import app.bilipai.data.model.Video\n"
                                + "val x = Video\n"))
                .run()
                .expectClean();
    }

    @Test
    public void testAppModuleInternalCoreImportingFeatureIsAllowed() throws IOException {
        // :app hosts both core/ and feature/ packages; that is an in-module concern.
        lint().allowMissingSdk().allowCompilationErrors().files(
                sourceIn("src/main/java/app/bilipai/core/store",
                        "package app.bilipai.core.store\n"
                                + "import app.bilipai.feature.search.SearchScreen\n"
                                + "val x = SearchScreen\n"))
                .run()
                .expectClean();
    }

    @Test
    public void testNavigationImportingNavigation3HostIsAllowed() throws IOException {
        lint().allowMissingSdk().allowCompilationErrors().files(
                sourceIn("src/main/java/app/bilipai/navigation",
                        "package app.bilipai.navigation\n"
                                + "import app.bilipai.navigation3.BiliPaiNavDisplayHost\n"
                                + "val x = BiliPaiNavDisplayHost\n"))
                .run()
                .expectClean();
    }

    @Test
    public void testIssueIsRegisteredAsError() {
        assertEquals("BiliPaiLayerDependency", LayerDependencyDetector.ISSUE.getId());
        assertEquals(com.android.tools.lint.detector.api.Severity.ERROR,
                LayerDependencyDetector.ISSUE.getDefaultSeverity());
    }
}
