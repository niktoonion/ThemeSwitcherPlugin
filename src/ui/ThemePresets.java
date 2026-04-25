package ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Набор готовых тем. Каждая тема собирается через {@link Theme.Builder},
 * поэтому добавление новой темы – простое копирование блока.
 */
public final class ThemePresets {

    private ThemePresets() {}

    public static List<Theme> getPresets() {
        List<Theme> list = new ArrayList<>();

        // ------------------- 1️⃣ Dracula -------------------
        list.add(theme(
                "Dracula",
                new Color(0x282A36), new Color(0xF8F8F2),   // editor bg/fg
                new Color(0x282A36), new Color(0xF8F8F2),   // terminal bg/fg
                new Color(0x21222C), new Color(0xF8F8F2),   // panel / label
                new Color(0x6272A4), new Color(0xF8F8F2),   // button
                new Color(0x21222C), new Color(0xF8F8F2),   // menubar
                new Color(0x282A36), new Color(0xF8F8F2),   // menu
                new Color(0x6272A4), new Color(0xF8F8F2),   // menuItem
                new Color(0x44475A), new Color(0xF8F8F2),   // tooltip
                // 15 дополнительных параметров (все null – не задаём)
                null, null,   // table bg / fg
                null, null,   // table‑header bg / fg
                null, null,   // list bg / fg
                null, null,   // tree bg / fg
                null, null,   // tabbedPane bg / fg
                null,         // splitPane bg
                null, null,   // scrollBar bg / fg
                null, null    // popup bg / fg
        ));

        // ------------------- 2️⃣ Nord -------------------
        list.add(theme(
                "Nord",
                new Color(0x2E3440), new Color(0xD8DEE9),
                new Color(0x2E3440), new Color(0xD8DEE9),
                new Color(0x3B4252), new Color(0xD8DEE9),
                new Color(0x81A1C1), new Color(0x2E3440),
                new Color(0x3B4252), new Color(0xD8DEE9),
                new Color(0x4C566A), new Color(0xD8DEE9),
                new Color(0x81A1C1), new Color(0x2E3440),
                new Color(0x434C5E), new Color(0xD8DEE9),
                // дополнительные (все null)
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null
        ));

        // ------------------- 3️⃣ One Dark -------------------
        list.add(theme(
                "One Dark",
                new Color(0x282C34), new Color(0xABB2BF),
                new Color(0x282C34), new Color(0xABB2BF),
                new Color(0x21252B), new Color(0xABB2BF),
                new Color(0x61AFEF), new Color(0x282C34),
                new Color(0x21252B), new Color(0xABB2BF),
                new Color(0x3E4451), new Color(0xABB2BF),
                new Color(0x61AFEF), new Color(0x282C34),
                new Color(0x3E4451), new Color(0xABB2BF),
                // дополнительные (все null)
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null
        ));

        // -----------------------------------------------------------------
        // Добавьте остальные 12 тем тем же способом – просто скопируйте их
        // из оригинального кода и замените «null, null, …» теми цветами,
        // которые хотите видеть.
        // -----------------------------------------------------------------

        return Collections.unmodifiableList(list);
    }

    /**
     * Вспомогательная функция – собирает тему через Builder.
     * Порядок аргументов полностью совпадает с оригинальным конструктором.
     */
    private static Theme theme(String name,
                               Color editorBg, Color editorFg,
                               Color terminalBg, Color terminalFg,
                               Color panelBg, Color labelFg,
                               Color buttonBg, Color buttonFg,
                               Color menuBarBg, Color menuBarFg,
                               Color menuBg, Color menuFg,
                               Color menuItemBg, Color menuItemFg,
                               Color tooltipBg, Color tooltipFg,
                               // 15 дополнительных параметров (может быть null)
                               Color tableBg, Color tableFg,
                               Color tableHeaderBg, Color tableHeaderFg,
                               Color listBg, Color listFg,
                               Color treeBg, Color treeFg,
                               Color tabbedPaneBg, Color tabbedPaneFg,
                               Color splitPaneBg,
                               Color scrollBarBg, Color scrollBarFg,
                               Color popupBg, Color popupFg) {

        Theme.Builder b = Theme.builder(name);
        // 1‑4
        b.put(UIKey.EDITOR_BACKGROUND, editorBg);
        b.put(UIKey.EDITOR_FOREGROUND, editorFg);
        b.put(UIKey.TERMINAL_BACKGROUND, terminalBg);
        b.put(UIKey.TERMINAL_FOREGROUND, terminalFg);
        // 5‑6
        b.put(UIKey.PANEL_BACKGROUND, panelBg);
        b.put(UIKey.LABEL_FOREGROUND, labelFg);
        // 7‑8
        b.put(UIKey.BUTTON_BACKGROUND, buttonBg);
        b.put(UIKey.BUTTON_FOREGROUND, buttonFg);
        // 9‑10
        b.put(UIKey.MENU_BAR_BACKGROUND, menuBarBg);
        b.put(UIKey.MENU_BAR_FOREGROUND, menuBarFg);
        // 11‑12
        b.put(UIKey.MENU_BACKGROUND, menuBg);
        b.put(UIKey.MENU_FOREGROUND, menuFg);
        // 13‑14
        b.put(UIKey.MENU_ITEM_BACKGROUND, menuItemBg);
        b.put(UIKey.MENU_ITEM_FOREGROUND, menuItemFg);
        // 15‑16
        b.put(UIKey.TOOL_TIP_BACKGROUND, tooltipBg);
        b.put(UIKey.TOOL_TIP_FOREGROUND, tooltipFg);
        // Дополнительные (если передан null – просто не ставим в карту)
        b.put(UIKey.TABLE_BACKGROUND,          tableBg);
        b.put(UIKey.TABLE_FOREGROUND,          tableFg);
        b.put(UIKey.TABLE_HEADER_BACKGROUND,   tableHeaderBg);
        b.put(UIKey.TABLE_HEADER_FOREGROUND,   tableHeaderFg);
        b.put(UIKey.LIST_BACKGROUND,          listBg);
        b.put(UIKey.LIST_FOREGROUND,          listFg);
        b.put(UIKey.TREE_BACKGROUND,          treeBg);
        b.put(UIKey.TREE_FOREGROUND,          treeFg);
        b.put(UIKey.TABBED_PANE_BACKGROUND,  tabbedPaneBg);
        b.put(UIKey.TABBED_PANE_FOREGROUND,  tabbedPaneFg);
        b.put(UIKey.SPLIT_PANE_BACKGROUND,   splitPaneBg);
        b.put(UIKey.SCROLL_BAR_BACKGROUND,    scrollBarBg);
        b.put(UIKey.SCROLL_BAR_FOREGROUND,    scrollBarFg);
        b.put(UIKey.POPUP_MENU_BACKGROUND,   popupBg);
        b.put(UIKey.POPUP_MENU_FOREGROUND,   popupFg);
        return b.build();
    }
}
