package com.flexicore.ui.style;

import com.flexicore.ui.model.UiStyleTargets;
import com.flexicore.ui.model.UiStyleValueType;
import com.flexicore.ui.response.UiStyleAllowedValue;
import com.flexicore.ui.response.UiStylePropertyDefinition;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import java.util.List;

@Extension
@Component
public class DefaultUiStylePropertyDefinitionProvider implements UiStylePropertyDefinitionProvider, Plugin {

    private static final List<UiStyleAllowedValue> ALIGNMENT_VALUES = List.of(
            option("START", "Start"), option("CENTER", "Center"), option("END", "End"));
    private static final List<UiStyleAllowedValue> WEIGHT_VALUES = List.of(
            option("NORMAL", "Normal"), option("MEDIUM", "Medium"),
            option("SEMIBOLD", "Semi-bold"), option("BOLD", "Bold"));
    private static final List<UiStyleAllowedValue> DENSITY_VALUES = List.of(
            option("COMPACT", "Compact"), option("COMFORTABLE", "Comfortable"),
            option("SPACIOUS", "Spacious"));
    private static final List<UiStyleAllowedValue> SHADOW_VALUES = List.of(
            option("NONE", "None"), option("SUBTLE", "Subtle"),
            option("MEDIUM", "Medium"), option("STRONG", "Strong"));
    private static final List<UiStyleAllowedValue> MAP_POSITION_VALUES = List.of(
            option("TOP_LEFT", "Top left"), option("TOP_RIGHT", "Top right"),
            option("BOTTOM_LEFT", "Bottom left"), option("BOTTOM_RIGHT", "Bottom right"));
    private static final List<UiStyleAllowedValue> COLOR_TOKEN_VALUES = List.of(
            option("THEME_PRIMARY", "Theme primary"),
            option("THEME_ON_PRIMARY", "Text on theme primary"),
            option("THEME_SURFACE", "Theme surface"),
            option("THEME_SURFACE_VARIANT", "Theme surface variant"),
            option("THEME_ON_SURFACE", "Text on theme surface"),
            option("THEME_BACKGROUND", "Theme background"),
            option("THEME_ON_BACKGROUND", "Text on theme background"),
            option("THEME_OUTLINE", "Theme outline"),
            option("THEME_HOVER", "Theme hover"),
            option("TRANSPARENT", "Transparent"));

    @Override
    public List<UiStylePropertyDefinition> getDefinitions() {
        return List.of(
                bool("TITLE_VISIBLE", UiStyleTargets.COMMON, "TITLE", "Show title", "Display the preset title", true, 10),
                number("TITLE_FONT_SIZE", UiStyleTargets.COMMON, "TITLE", "Title size", "Title text size", 10, 72, 1, "px", 20, 20),
                enumeration("TITLE_FONT_WEIGHT", UiStyleTargets.COMMON, "TITLE", "Title weight", "Title text weight", WEIGHT_VALUES, "SEMIBOLD", 30),
                enumeration("TITLE_ALIGNMENT", UiStyleTargets.COMMON, "TITLE", "Title alignment", "Horizontal title alignment", ALIGNMENT_VALUES, "START", 40),
                color("TITLE_TEXT_COLOR", UiStyleTargets.COMMON, "TITLE", "Title text color", "Color of the title text", "THEME_ON_SURFACE", 50),
                color("TITLE_BACKGROUND_COLOR", UiStyleTargets.COMMON, "TITLE", "Title background", "Background color behind the title", "TRANSPARENT", 60),
                number("TITLE_PADDING", UiStyleTargets.COMMON, "TITLE", "Title spacing", "Space around the title", 0, 48, 1, "px", 12, 70),
                color("CONTENT_BACKGROUND_COLOR", UiStyleTargets.COMMON, "CONTENT", "Content background", "Main content background color", "THEME_SURFACE", 80),
                color("CONTENT_TEXT_COLOR", UiStyleTargets.COMMON, "CONTENT", "Content text color", "Main content text color", "THEME_ON_SURFACE", 90),
                color("BORDER_COLOR", UiStyleTargets.COMMON, "BORDER", "Border color", "Outer border color", "THEME_OUTLINE", 100),
                number("BORDER_RADIUS", UiStyleTargets.COMMON, "BORDER", "Rounded corners", "Outer corner radius", 0, 32, 1, "px", 4, 110),

                enumeration("GRID_DENSITY", UiStyleTargets.GRID_PRESET, "TABLE", "Table spacing", "Amount of spacing in table rows and cells", DENSITY_VALUES, "COMFORTABLE", 200),
                color("GRID_HEADER_BACKGROUND_COLOR", UiStyleTargets.GRID_PRESET, "HEADER", "Header background", "Column header background color", "THEME_SURFACE_VARIANT", 210),
                color("GRID_HEADER_TEXT_COLOR", UiStyleTargets.GRID_PRESET, "HEADER", "Header text color", "Column header text color", "THEME_ON_SURFACE", 220),
                enumeration("GRID_HEADER_FONT_WEIGHT", UiStyleTargets.GRID_PRESET, "HEADER", "Header weight", "Column header text weight", WEIGHT_VALUES, "SEMIBOLD", 230),
                bool("GRID_HEADER_STICKY", UiStyleTargets.GRID_PRESET, "HEADER", "Keep header visible", "Keep the table header visible while scrolling", true, 240),
                number("GRID_ROW_HEIGHT", UiStyleTargets.GRID_PRESET, "ROWS", "Row height", "Height of each data row", 24, 96, 1, "px", 40, 250),
                color("GRID_ROW_BACKGROUND_COLOR", UiStyleTargets.GRID_PRESET, "ROWS", "Row background", "Default data-row background", "THEME_SURFACE", 260),
                bool("GRID_STRIPED_ROWS", UiStyleTargets.GRID_PRESET, "ROWS", "Alternating row colors", "Use a different color for alternating rows", false, 270),
                color("GRID_ALTERNATE_ROW_BACKGROUND_COLOR", UiStyleTargets.GRID_PRESET, "ROWS", "Alternate row background", "Background used for alternating rows", "THEME_SURFACE_VARIANT", 280),
                color("GRID_ROW_HOVER_COLOR", UiStyleTargets.GRID_PRESET, "ROWS", "Row hover color", "Background while the pointer is over a row", "THEME_HOVER", 290),
                number("GRID_CELL_PADDING", UiStyleTargets.GRID_PRESET, "CELLS", "Cell spacing", "Space inside each table cell", 0, 32, 1, "px", 8, 300),
                bool("GRID_SHOW_VERTICAL_BORDERS", UiStyleTargets.GRID_PRESET, "CELLS", "Vertical separators", "Show separators between columns", false, 310),
                bool("GRID_SHOW_HORIZONTAL_BORDERS", UiStyleTargets.GRID_PRESET, "CELLS", "Horizontal separators", "Show separators between rows", true, 320),

                color("DASHBOARD_BACKGROUND_COLOR", UiStyleTargets.DASHBOARD_PRESET, "DASHBOARD", "Dashboard background", "Background behind dashboard cells", "THEME_BACKGROUND", 400),
                number("DASHBOARD_CELL_GAP", UiStyleTargets.DASHBOARD_PRESET, "DASHBOARD", "Space between cells", "Gap between dashboard cells", 0, 48, 1, "px", 12, 410),
                color("DASHBOARD_CELL_BACKGROUND_COLOR", UiStyleTargets.DASHBOARD_PRESET, "CELLS", "Cell background", "Dashboard cell background color", "THEME_SURFACE", 420),
                color("DASHBOARD_CELL_BORDER_COLOR", UiStyleTargets.DASHBOARD_PRESET, "CELLS", "Cell border", "Dashboard cell border color", "THEME_OUTLINE", 430),
                number("DASHBOARD_CELL_BORDER_RADIUS", UiStyleTargets.DASHBOARD_PRESET, "CELLS", "Cell rounded corners", "Dashboard cell corner radius", 0, 32, 1, "px", 8, 440),
                enumeration("DASHBOARD_CELL_SHADOW", UiStyleTargets.DASHBOARD_PRESET, "CELLS", "Cell shadow", "Visual depth of dashboard cells", SHADOW_VALUES, "SUBTLE", 450),
                color("DASHBOARD_TOOLBAR_BACKGROUND_COLOR", UiStyleTargets.DASHBOARD_PRESET, "TOOLBAR", "Toolbar background", "Dashboard toolbar background color", "THEME_SURFACE", 460),

                color("MAP_BACKGROUND_COLOR", UiStyleTargets.MAP_PRESET, "MAP", "Map background", "Background shown while map tiles load", "THEME_BACKGROUND", 500),
                enumeration("MAP_TOOLBAR_POSITION", UiStyleTargets.MAP_PRESET, "TOOLBAR", "Toolbar position", "Position of map tools", MAP_POSITION_VALUES, "TOP_RIGHT", 510),
                color("MAP_TOOLBAR_BACKGROUND_COLOR", UiStyleTargets.MAP_PRESET, "TOOLBAR", "Toolbar background", "Map toolbar background color", "THEME_SURFACE", 520),
                number("MAP_CONTROL_SIZE", UiStyleTargets.MAP_PRESET, "TOOLBAR", "Control size", "Size of map control buttons", 24, 64, 1, "px", 40, 530),
                number("MAP_MARKER_SCALE", UiStyleTargets.MAP_PRESET, "MARKERS", "Marker size", "Scale applied to map markers", 0.5, 3, 0.1, null, 1, 540),
                color("MAP_CLUSTER_BACKGROUND_COLOR", UiStyleTargets.MAP_PRESET, "CLUSTERS", "Cluster background", "Background color of marker clusters", "THEME_PRIMARY", 550),
                color("MAP_CLUSTER_TEXT_COLOR", UiStyleTargets.MAP_PRESET, "CLUSTERS", "Cluster text", "Text color inside marker clusters", "THEME_ON_PRIMARY", 560),
                color("MAP_POPUP_BACKGROUND_COLOR", UiStyleTargets.MAP_PRESET, "POPUPS", "Popup background", "Map popup background color", "THEME_SURFACE", 570),
                color("MAP_POPUP_TEXT_COLOR", UiStyleTargets.MAP_PRESET, "POPUPS", "Popup text", "Map popup text color", "THEME_ON_SURFACE", 580)
        );
    }

    private static UiStylePropertyDefinition color(String key, String target, String section,
                                                     String displayName, String description,
                                                     String defaultValue, int priority) {
        return base(key, target, section, displayName, description, UiStyleValueType.COLOR, priority)
                .setAllowedValues(COLOR_TOKEN_VALUES)
                .setDefaultStringValue(defaultValue);
    }

    private static UiStylePropertyDefinition bool(String key, String target, String section,
                                                   String displayName, String description,
                                                   boolean defaultValue, int priority) {
        return base(key, target, section, displayName, description, UiStyleValueType.BOOLEAN, priority)
                .setDefaultBooleanValue(defaultValue);
    }

    private static UiStylePropertyDefinition number(String key, String target, String section,
                                                     String displayName, String description,
                                                     double minimum, double maximum, double step,
                                                     String unit, double defaultValue, int priority) {
        return base(key, target, section, displayName, description, UiStyleValueType.NUMBER, priority)
                .setMinimum(minimum)
                .setMaximum(maximum)
                .setStep(step)
                .setUnit(unit)
                .setDefaultNumericValue(defaultValue);
    }

    private static UiStylePropertyDefinition enumeration(String key, String target, String section,
                                                          String displayName, String description,
                                                          List<UiStyleAllowedValue> values,
                                                          String defaultValue, int priority) {
        return base(key, target, section, displayName, description, UiStyleValueType.ENUM, priority)
                .setAllowedValues(values)
                .setDefaultStringValue(defaultValue);
    }

    private static UiStylePropertyDefinition base(String key, String target, String section,
                                                   String displayName, String description,
                                                   UiStyleValueType valueType, int priority) {
        return new UiStylePropertyDefinition()
                .setKey(key)
                .setTargetType(target)
                .setSection(section)
                .setDisplayName(displayName)
                .setDescription(description)
                .setValueType(valueType)
                .setPriority(priority);
    }

    private static UiStyleAllowedValue option(String value, String displayName) {
        return new UiStyleAllowedValue(value, displayName);
    }
}
