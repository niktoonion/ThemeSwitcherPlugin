package plugin;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.*;

import extra.SettingsManager;
import ui.*;

/**
 * Плагин «ThemeSwitcher». Сохраняет любые пользовательские цвета и
 * автоматически применяет их при загрузке.
 *
 * Основные возможности:
 *  • Пункт меню «Темы → Настроить…» – открывает диалог настройки.
 *  • Пункт «Темы → Открыть панель» – открывает вкладку с той же кнопкой.
 *  • При старте плагина читаются сохранённые цвета и применяются.
 *  • При закрытии IDE все настройки уже находятся в Preferences,
 *    ничего дополнительно сохранять не нужно.
 */
public final class ThemePlugin implements IDEPlugin {

    private static final String MENU_TITLE = "Темы";

    /** Какими UI‑цветами следует сопоставить «foreground»,
     *  если пользователь не указал отдельный цвет текста. */
    private static final Map<UIKey, UIKey> FOREGROUND_FOR_BACKGROUND = new EnumMap<>(UIKey.class);
    static {
        FOREGROUND_FOR_BACKGROUND.put(UIKey.PANEL_BACKGROUND,          UIKey.LABEL_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.BUTTON_BACKGROUND,         UIKey.BUTTON_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.MENU_BAR_BACKGROUND,       UIKey.MENU_BAR_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.MENU_BACKGROUND,           UIKey.MENU_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.MENU_ITEM_BACKGROUND,      UIKey.MENU_ITEM_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.TOOL_TIP_BACKGROUND,       UIKey.TOOL_TIP_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.TOOL_BAR_BACKGROUND,       UIKey.TOOL_BAR_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.TABLE_BACKGROUND,          UIKey.TABLE_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.TABLE_HEADER_BACKGROUND,   UIKey.TABLE_HEADER_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.LIST_BACKGROUND,           UIKey.LIST_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.TREE_BACKGROUND,           UIKey.TREE_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.TABBED_PANE_BACKGROUND,    UIKey.TABBED_PANE_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.SPLIT_PANE_BACKGROUND,     UIKey.TOOL_BAR_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.SCROLL_BAR_BACKGROUND,     UIKey.SCROLL_BAR_FOREGROUND);
        FOREGROUND_FOR_BACKGROUND.put(UIKey.POPUP_MENU_BACKGROUND,     UIKey.POPUP_MENU_FOREGROUND);
    }

    /** «Мягкий» контрастный цвет (чуть менее резкий, чем чистый). */
    private static Color sensibleForeground(Color bg) {
        if (bg == null) return Color.BLACK;
        double lum = (0.2126 * bg.getRed()
                    + 0.7152 * bg.getGreen()
                    + 0.0722 * bg.getBlue()) / 255.0;
        if (lum < 0.15)  return new Color(0xE0E0E0); // почти белый
        if (lum > 0.85)  return new Color(0x202020); // почти чёрный
        return lum < 0.5 ? Color.WHITE : Color.BLACK;
    }

    @Override
    public void init(JFrame owner, JMenuBar menuBar) {
        JMenu themeMenu = new JMenu(MENU_TITLE);
        themeMenu.setMnemonic(KeyEvent.VK_T);

        JMenuItem openPanel = new JMenuItem("Открыть панель");
        openPanel.addActionListener(e -> openThemePanel(owner));
        themeMenu.add(openPanel);
        themeMenu.addSeparator();

        JMenuItem configure = new JMenuItem("Настроить…");
        configure.addActionListener(e -> new ThemeConfigDialog(owner).setVisible(true));
        themeMenu.add(configure);
        themeMenu.addSeparator();

        JMenuItem reset = new JMenuItem("Сбросить к системным");
        reset.addActionListener(e -> {
            SettingsManager.clearAllUIColors();

            UIManager.getLookAndFeelDefaults().entrySet().forEach(entry -> {
                Object key = entry.getKey();
                if (key instanceof String) {
                    UIManager.put(key, null);
                } else if (key instanceof UIKey) {
                    UIManager.put(((UIKey) key).key(), null);
                }
            });

            SwingUtilities.updateComponentTreeUI(owner);
        });
        themeMenu.add(reset);

        menuBar.add(themeMenu);
        menuBar.revalidate();

        // Применяем сохранённую тему, если она есть
        Theme saved = loadSavedTheme();
        if (saved != null) {
            applyTheme(owner, saved);
        }
    }


    @Override public String getName()        { return "ThemeSwitcher"; }
    @Override public String getDescription() { return "Управление цветовой схемой IDE"; }

    /** Открыть/создать вкладку «Тема». */
    private void openThemePanel(JFrame owner) {
        try {
            var f = owner.getClass().getDeclaredField("tabbedPane");
            f.setAccessible(true);
            JTabbedPane tabs = (JTabbedPane) f.get(owner);

            // если уже есть – переключаем
            for (int i = 0; i < tabs.getTabCount(); i++) {
                if ("Тема".equals(tabs.getTitleAt(i))) {
                    tabs.setSelectedIndex(i);
                    return;
                }
            }

            ThemePanel panel = new ThemePanel(owner);
            tabs.addTab("Тема", panel);
            tabs.setSelectedComponent(panel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(owner,
                    "Не удалось открыть панель тем:\n" + ex.getMessage(),
                    "Ошибка плагина", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Прочитать из Preferences набор сохранённых UI‑цветов и собрать Theme. */
    private static Theme loadSavedTheme() {
        Theme.Builder b = Theme.builder("Saved");
        boolean any = false;
        for (UIKey key : UIKey.values()) {
            Color c = SettingsManager.getUIKeyColor(key, null);
            if (c != null) {
                b.put(key, c);
                any = true;
            }
        }
        return any ? b.build() : null;
    }

    /** Применить тему ко всему UI и сохранить её в Preferences. */
    public static void applyTheme(JFrame owner, Theme th) {
        // 1️⃣ Сохраняем цвета редактора/терминала в SettingsManager
        if (th.get(UIKey.EDITOR_BACKGROUND) != null)
            SettingsManager.setEditorBackground(th.get(UIKey.EDITOR_BACKGROUND));
        if (th.get(UIKey.EDITOR_FOREGROUND) != null)
            SettingsManager.setEditorForeground(
                    th.get(UIKey.EDITOR_FOREGROUND) != null
                    ? th.get(UIKey.EDITOR_FOREGROUND)
                    : sensibleForeground(th.get(UIKey.EDITOR_BACKGROUND)));

        if (th.get(UIKey.TERMINAL_BACKGROUND) != null)
            SettingsManager.setTerminalBackground(th.get(UIKey.TERMINAL_BACKGROUND));
        if (th.get(UIKey.TERMINAL_FOREGROUND) != null)
            SettingsManager.setTerminalForeground(
                    th.get(UIKey.TERMINAL_FOREGROUND) != null
                    ? th.get(UIKey.TERMINAL_FOREGROUND)
                    : sensibleForeground(th.get(UIKey.TERMINAL_BACKGROUND)));

        // 2️⃣ UIManager – фоновые свойства
        for (UIKey key : UIKey.values()) {
            Color bg = th.get(key);
            if (bg != null) UIManager.put(key.key(), bg);
        }

        // 3️⃣ UIManager – свойства текста (foreground)
        FOREGROUND_FOR_BACKGROUND.forEach((bgKey, fgKey) -> {
            Color fg = th.get(fgKey);
            if (fg == null) {                 // пользователь не задал → авто‑контраст
                Color bg = th.get(bgKey);
                if (bg != null) fg = sensibleForeground(bg);
            }
            if (fg != null) UIManager.put(fgKey.key(), fg);
        });

        // 4️⃣ Обновляем UI и фон окна
        SwingUtilities.updateComponentTreeUI(owner);
        Color panelBg = UIManager.getColor(UIKey.PANEL_BACKGROUND.key());
        if (panelBg != null) {
            owner.getContentPane().setBackground(panelBg);
            owner.getRootPane().setBackground(panelBg);
        }

        // 5️⃣ Если Main имеет метод applyAllSettings (чтобы обновить
        //    редактор/терминал) – вызываем его.
        if (owner instanceof main.Main) ((main.Main) owner).applyAllSettings();
    }
}
