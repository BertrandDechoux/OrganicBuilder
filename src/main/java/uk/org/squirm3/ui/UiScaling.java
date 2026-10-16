package uk.org.squirm3.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Adapts the Java2D user-interface scale to the real screen resolution so the
 * fixed-size Swing interface stays readable on HiDPI displays.
 *
 * On Windows and macOS the JDK scales Swing applications automatically; on
 * Linux/X11 it does not, so this helper probes the physical DPI of the
 * connected outputs (xrandr, EDID blocks via sysfs DRM connectors, xdpyinfo)
 * and sets
 * {@code sun.java2d.uiScale} before the AWT toolkit is initialized. The
 * property multiplies every font, icon, gap and component size throughout the
 * UI, including the simulation canvas.
 *
 * The automatic value can always be overridden with
 * {@code -Dsun.java2d.uiScale=<factor>}.
 */
public final class UiScaling {

    /** Logical (unscaled) reference DPI used by Java2D. */
    private static final double REFERENCE_DPI = 96.0;
    /** Byte offsets of the display size in centimetres inside an EDID block. */
    private static final int EDID_WIDTH_CM_OFFSET = 21;
    private static final int EDID_HEIGHT_CM_OFFSET = 22;
    private static final int EDID_MIN_LENGTH = EDID_HEIGHT_CM_OFFSET + 1;
    private static final double MIN_SCALE = 1.0;
    private static final double MAX_SCALE = 3.0;
    /** Detection noise below this factor is ignored so 96-DPI screens stay unscaled. */
    private static final double SCALE_THRESHOLD = 1.05;
    private static final double MM_PER_INCH = 25.4;

    /**
     * xrandr output line carrying the physical size of a connected output,
     * e.g. "eDP-1 connected primary 3408x2130+0+0 ... 344mm x 215mm".
     */
    private static final Pattern XRANDR_CONNECTED = Pattern
            .compile("\\bconnected\\b.*?(\\d+)mm x (\\d+)mm");
    /**
     * xrandr mode line carrying the native resolution, e.g. the mode flagged
     * with '+' ("   2560x1600    165.04*+"). The native mode is used rather
     * than the current geometry because compositors with fractional scaling
     * report a scaled framebuffer size that is larger than the real panel.
     */
    private static final Pattern XRANDR_NATIVE_MODE = Pattern
            .compile("^\\s+(\\d+)x(\\d+)\\s+.*\\+");
    private static final Pattern XDPYINFO_DPI = Pattern
            .compile("resolution:\\s+(\\d+)x\\d+ dots per inch");

    private UiScaling() {
    }

    /**
     * Detects the screen scale factor and sets {@code sun.java2d.uiScale}.
     * Must run before the AWT toolkit is initialized, otherwise the property
     * is read too late and has no effect.
     */
    public static void adaptToScreenResolution() {
        if (System.getProperty("sun.java2d.uiScale") != null
                || System.getenv("DISPLAY") == null
                || !System.getProperty("os.name", "")
                        .toLowerCase(Locale.ROOT).contains("linux")) {
            return;
        }
        final Double scale = detectScale();
        if (scale != null && scale.doubleValue() > SCALE_THRESHOLD) {
            System.setProperty("sun.java2d.uiScale", scale.toString());
        }
    }

    private static Double detectScale() {
        final Integer dpi = probeDpi();
        if (dpi != null && dpi.intValue() > 0) {
            return Double.valueOf(clamp(dpi.doubleValue() / REFERENCE_DPI));
        }
        return null;
    }

    /**
     * Rounds to half steps (1.0, 1.5, 2.0 ...) and clamps to
     * [{@link #MIN_SCALE}, {@link #MAX_SCALE}]. Java2D honours integer or
     * half-integer scale factors cleanly.
     */
    private static double clamp(final double scale) {
        final double rounded = Math.round(scale * 2.0) / 2.0;
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, rounded));
    }

    private static Integer probeDpi() {
        final Integer dpi = xrandrDpi();
        if (dpi != null) {
            return dpi;
        }
        final Integer edidDpi = edidDpi();
        return edidDpi != null ? edidDpi : xdpyinfoDpi();
    }

    /**
     * Computes the physical DPI of the connected outputs from their native
     * mode and their reported size in millimetres. The output flagged
     * "primary" wins; otherwise the largest DPI is used so the panel with the
     * highest density determines the global scale.
     */
    private static Integer xrandrDpi() {
        try {
            final Process process = new ProcessBuilder("xrandr").start();
            int maxDpi = 0;
            int widthMm = 0;
            boolean outputIsPrimary = false;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Non-indented lines start a new screen or output block.
                    if (!line.startsWith(" ") && !line.startsWith("\t")) {
                        widthMm = 0;
                        outputIsPrimary = false;
                        final Matcher connected = XRANDR_CONNECTED
                                .matcher(line);
                        if (connected.find()) {
                            widthMm = Integer.parseInt(connected.group(1));
                            outputIsPrimary = line.contains("primary");
                        }
                        continue;
                    }
                    final Matcher mode = XRANDR_NATIVE_MODE.matcher(line);
                    if (mode.find() && widthMm > 0) {
                        final int widthPx = Integer.parseInt(mode.group(1));
                        final int dpi = Math.round(
                                (float) (widthPx * MM_PER_INCH / widthMm));
                        if (outputIsPrimary) {
                            return Integer.valueOf(dpi);
                        }
                        maxDpi = Math.max(maxDpi, dpi);
                        widthMm = 0;
                    }
                }
            } finally {
                process.destroy();
            }
            return maxDpi > 0 ? Integer.valueOf(maxDpi) : null;
        } catch (IOException | NumberFormatException e) {
            // xrandr not installed or unparsable output
            return null;
        }
    }

    private static Integer xdpyinfoDpi() {
        try {
            final Process process = new ProcessBuilder("xdpyinfo").start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final Matcher matcher = XDPYINFO_DPI.matcher(line);
                    if (matcher.find()) {
                        return Integer.valueOf(matcher.group(1));
                    }
                }
            } finally {
                process.destroy();
            }
        } catch (IOException | NumberFormatException e) {
            // xdpyinfo not installed or unparsable output
        }
        return null;
    }

    /**
     * Computes the physical DPI of connected DRM connectors by combining the
     * native mode ({@code /sys/class/drm/card0-eDP-1/modes}) with the display
     * size advertised in the EDID block
     * ({@code /sys/class/drm/card0-eDP-1/edid}).
     * This reports the real panel size, unlike a pixel-height heuristic which
     * cannot tell a 13-inch 4K laptop panel from a 55-inch 4K television.
     */
    private static Integer edidDpi() {
        final File drm = new File("/sys/class/drm");
        final File[] connectors = drm.listFiles();
        if (connectors == null) {
            return null;
        }
        int maxDpi = 0;
        for (final File connector : connectors) {
            if (!"connected"
                    .equals(readFirstLine(new File(connector, "status")))) {
                continue;
            }
            final Integer dpi = connectorDpi(connector);
            if (dpi != null) {
                maxDpi = Math.max(maxDpi, dpi.intValue());
            }
        }
        return maxDpi > 0 ? Integer.valueOf(maxDpi) : null;
    }

    private static Integer connectorDpi(final File connector) {
        final String mode = readFirstLine(new File(connector, "modes"));
        final byte[] edid = readEdid(new File(connector, "edid"));
        if (mode == null || edid == null) {
            return null;
        }
        final int x = mode.indexOf('x');
        if (x < 0) {
            return null;
        }
        try {
            final int widthPx = Integer.parseInt(mode.substring(0, x).trim());
            final int widthCm = edid[EDID_WIDTH_CM_OFFSET] & 0xFF;
            if (widthPx <= 0 || widthCm <= 0) {
                return null;
            }
            return Integer.valueOf(
                    Math.round((float) (widthPx * MM_PER_INCH / (widthCm * 10.0))));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static byte[] readEdid(final File file) {
        final byte[] edid = new byte[EDID_MIN_LENGTH];
        try (FileInputStream in = new FileInputStream(file)) {
            int offset = 0;
            while (offset < edid.length) {
                final int read = in.read(edid, offset, edid.length - offset);
                if (read < 0) {
                    break;
                }
                offset += read;
            }
            return offset >= EDID_MIN_LENGTH ? edid : null;
        } catch (IOException e) {
            return null;
        }
    }

    private static String readFirstLine(final File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final String line = reader.readLine();
            return line == null ? null : line.trim();
        } catch (IOException e) {
            return null;
        }
    }
}
