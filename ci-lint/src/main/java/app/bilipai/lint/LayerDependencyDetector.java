package app.bilipai.lint;

import com.android.tools.lint.client.api.UElementHandler;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.SourceCodeScanner;
import java.util.List;
import org.jetbrains.uast.UElement;
import org.jetbrains.uast.UImportStatement;

/**
 * Enforces the dependency direction documented in docs/wiki/LAYERS.md:
 * <ul>
 *   <li>{@code core-*}, {@code data-*}, {@code design-*}, {@code network-core} must not import
 *       {@code app.bilipai.feature.*} or {@code app.bilipai.navigation*} (lower layers never
 *       depend on the application layer).</li>
 *   <li>{@code app.bilipai.navigation3} must not import {@code app.bilipai.navigation} directly;
 *       route keys flow through shared types instead (breaks the package-level cycle).</li>
 * </ul>
 * The check is import-based so it stays deterministic across UAST resolution differences.
 */
public final class LayerDependencyDetector extends Detector implements SourceCodeScanner {

    private static final Implementation IMPLEMENTATION =
            new Implementation(LayerDependencyDetector.class, Scope.JAVA_FILE_SCOPE);

    static final Issue ISSUE = Issue.create(
            "BiliPaiLayerDependency",
            "Lower layers must not depend on the application layer",
            "docs/wiki/LAYERS.md defines the dependency direction: core-*, data-*, design-* and "
                    + "network-core never import app.bilipai.feature.* or app.bilipai.navigation*; "
                    + "navigation3 never imports navigation directly. Move the shared type to a "
                    + "lower layer instead of adding the import.",
            Category.CORRECTNESS, 6, Severity.ERROR, IMPLEMENTATION);

    private static final String APP_FEATURE = "app.bilipai.feature.";
    private static final String APP_NAVIGATION = "app.bilipai.navigation.";
    private static final String APP_NAVIGATION3 = "app.bilipai.navigation3.";

    private static final String[] LOWER_LAYER_PATH_SEGMENTS = {
            "/app/bilipai/core/",
            "/app/bilipai/data/",
            "/app/bilipai/designsystem/",
            "/app/bilipai/designtokens/",
            "/app/bilipai/network/",
            "/app/bilipai/player/",
            "/app/bilipai/domain/",
            "/app/bilipai/model/",
    };

    @Override
    public List<Class<? extends UElement>> getApplicableUastTypes() {
        return List.of(UImportStatement.class);
    }

    @Override
    public UElementHandler createUastHandler(JavaContext context) {
        return new UElementHandler() {
            @Override
            public void visitImportStatement(UImportStatement node) {
                String imported = importedFqn(node);
                if (imported == null) {
                    return;
                }
                com.intellij.psi.PsiFile psiFile = context.getPsiFile();
                String absolutePath = psiFile == null
                        ? ""
                        : (psiFile.getVirtualFile() != null
                                ? psiFile.getVirtualFile().getPath()
                                : psiFile.getName());
                // Report the path from the source root on, so lint output stays portable.
                int srcIndex = absolutePath.indexOf("/src/");
                String filePath = srcIndex >= 0
                        ? absolutePath.substring(srcIndex + 1)
                        : absolutePath;

                if (isLowerLayerFile(filePath) && targetsAppLayer(imported)) {
                    context.report(ISSUE, node, context.getLocation(node),
                            "Lower layer file `" + filePath + "` imports application-layer `"
                                    + imported + "`; see docs/wiki/LAYERS.md");
                    return;
                }
                if (filePath.contains("/navigation3/") && imported.startsWith(APP_NAVIGATION)) {
                    context.report(ISSUE, node, context.getLocation(node),
                            "`navigation3` imports `navigation` (`" + imported + "`); break the "
                                    + "cycle by moving the shared key type to a lower layer "
                                    + "(docs/wiki/LAYERS.md)");
                }
            }
        };
    }

    /**
     * Resolves the imported name from the UAST node itself, which works for both Kotlin and Java
     * sources without full type resolution.
     */
    private static String importedFqn(UImportStatement node) {
        if (node.isOnDemand()) {
            return null;
        }
        org.jetbrains.uast.UElement reference = node.getImportReference();
        if (reference == null) {
            return null;
        }
        String rendered = reference.asRenderString();
        if (rendered == null) {
            return null;
        }
        String trimmed = rendered.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean isLowerLayerFile(String filePath) {
        for (String segment : LOWER_LAYER_PATH_SEGMENTS) {
            if (filePath.contains(segment)) {
                return true;
            }
        }
        return false;
    }

    private static boolean targetsAppLayer(String imported) {
        return imported.startsWith(APP_FEATURE)
                || imported.startsWith(APP_NAVIGATION)
                || imported.startsWith(APP_NAVIGATION3);
    }
}
