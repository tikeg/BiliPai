package app.bilipai.lint;

import static org.junit.Assert.assertEquals;

import com.android.tools.lint.checks.infrastructure.LintDetectorTest;
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

    /** Puts the source under a directory whose path carries the package the rule matches on. */
    private TestFile kotlinAt(String dirPath, String source) {
        return kotlin(source).within(dirPath);
    }

    @Test
    public void testLowerLayerImportingFeatureIsRejected() throws IOException {
        lint().allowMissingSdk().allowCompilationErrors().files(
                kotlinAt("src/main/java/app/bilipai/core/store",
                        "package app.bilipai.core.store\n"
                                + "import app.bilipai.feature.search.SearchScreen\n"
                                + "val x = SearchScreen\n"))
                .run()
                .expectErrorCount(1);
    }

    @Test
    public void testNavigation3ImportingNavigationIsRejected() throws IOException {
        lint().allowMissingSdk().allowCompilationErrors().files(
                kotlinAt("src/main/java/app/bilipai/navigation3",
                        "package app.bilipai.navigation3\n"
                                + "import app.bilipai.navigation.ScreenRoutes\n"
                                + "val x = ScreenRoutes\n"))
                .run()
                .expectErrorCount(1);
    }

    @Test
    public void testLowerLayerImportingSharedModelIsAllowed() throws IOException {
        lint().allowMissingSdk().allowCompilationErrors().files(
                kotlinAt("src/main/java/app/bilipai/core/store",
                        "package app.bilipai.core.store\n"
                                + "import app.bilipai.data.model.Video\n"
                                + "val x = Video\n"))
                .run()
                .expectClean();
    }

    @Test
    public void testNavigationImportingNavigation3HostIsAllowed() throws IOException {
        lint().allowMissingSdk().allowCompilationErrors().files(
                kotlinAt("src/main/java/app/bilipai/navigation",
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
