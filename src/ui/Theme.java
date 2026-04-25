package ui;

import java.awt.Color;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Неизменяемая тема IDE. Содержит цвета в {@link EnumMap<UIKey,Color>}.
 * Для создания используется Builder, что избавляет от 30‑ти параметров в конструкторе.
 */
public final class Theme {

    private final String name;
    private final Map<UIKey,Color> colors;   // неизменяемый EnumMap

    private Theme(String name, Map<UIKey,Color> colors) {
        this.name   = name;
        this.colors = Collections.unmodifiableMap(new EnumMap<>(colors));
    }

    /* --------------------------- публичный API --------------------------- */

    public String getName()                     { return name; }
    public Color get(UIKey key)                 { return colors.get(key); }

    // Геттеры для обратной совместимости (не меняют сигнатуру)
    public Color getEditorBg()      { return get(UIKey.EDITOR_BACKGROUND); }
    public Color getEditorFg()      { return get(UIKey.EDITOR_FOREGROUND); }
    public Color getTerminalBg()    { return get(UIKey.TERMINAL_BACKGROUND); }
    public Color getTerminalFg()    { return get(UIKey.TERMINAL_FOREGROUND); }
    public Color getPanelBg()       { return get(UIKey.PANEL_BACKGROUND); }
    public Color getLabelFg()       { return get(UIKey.LABEL_FOREGROUND); }
    public Color getButtonBg()      { return get(UIKey.BUTTON_BACKGROUND); }
    public Color getButtonFg()      { return get(UIKey.BUTTON_FOREGROUND); }
    public Color getMenuBarBg()     { return get(UIKey.MENU_BAR_BACKGROUND); }
    public Color getMenuBarFg()     { return get(UIKey.MENU_BAR_FOREGROUND); }
    public Color getMenuBg()        { return get(UIKey.MENU_BACKGROUND); }
    public Color getMenuFg()        { return get(UIKey.MENU_FOREGROUND); }
    public Color getMenuItemBg()    { return get(UIKey.MENU_ITEM_BACKGROUND); }
    public Color getMenuItemFg()    { return get(UIKey.MENU_ITEM_FOREGROUND); }
    public Color getTooltipBg()     { return get(UIKey.TOOL_TIP_BACKGROUND); }
    public Color getTooltipFg()     { return get(UIKey.TOOL_TIP_FOREGROUND); }
    public Color getToolBarBg()     { return get(UIKey.TOOL_BAR_BACKGROUND); }
    public Color getToolBarFg()     { return get(UIKey.TOOL_BAR_FOREGROUND); }
    // При необходимости можно добавить остальные (table, list, …)

    /** Чёрный/белый контрастный к заданному фону (по рекомендации WCAG). */
    public static Color contrast(Color bg) {
        if (bg == null) return Color.BLACK;
        double lum = (0.2126 * bg.getRed()
                    + 0.7152 * bg.getGreen()
                    + 0.0722 * bg.getBlue()) / 255.0;
        return lum < 0.5 ? Color.WHITE : Color.BLACK;
    }

    /* ------------------------------- Builder -------------------------- */
    public static Builder builder(String name) { return new Builder(name); }

    public static final class Builder {
        private final String name;
        private final EnumMap<UIKey,Color> map = new EnumMap<>(UIKey.class);
        private Builder(String name) { this.name = name; }

        /** Добавить (или переопределить) цвет для {@link UIKey}. */
        public Builder put(UIKey key, Color color) {
            map.put(key, color);
            return this;
        }
        public Theme build() { return new Theme(name, map); }
    }
}
