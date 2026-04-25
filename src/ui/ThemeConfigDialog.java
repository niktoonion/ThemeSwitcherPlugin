package ui;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.*;

import extra.SettingsManager;
import plugin.ThemePlugin;

/**
 * Диалог «Настройка темы». Пользователь задаёт цвета для всех UIKey,
 * а также для редактора и терминала (см. Items ниже).
 * После «OK»/«Применить» выбранные значения сохраняются в Preferences
 * и сразу применяются.
 */
public final class ThemeConfigDialog extends JDialog {

    /** Описание одного элемента – подпись + UIKey. */
    private static final class Item {
        final String label;
        final UIKey key;
        Item(String l, UIKey k) { label = l; key = k; }
    }

    /** Какие элементы показывать. Добавляете/убираете строки здесь. */
    private static final Item[] ITEMS = {
            // редактор
            new Item("Фон редактора",       UIKey.EDITOR_BACKGROUND),
            new Item("Текст редактора",     UIKey.EDITOR_FOREGROUND),
            // терминал
            new Item("Фон терминала",       UIKey.TERMINAL_BACKGROUND),
            new Item("Текст терминала",     UIKey.TERMINAL_FOREGROUND),

            // остальные UI‑компоненты
            new Item("Фон панелей",         UIKey.PANEL_BACKGROUND),
            new Item("Текст меток",         UIKey.LABEL_FOREGROUND),
            new Item("Фон кнопок",          UIKey.BUTTON_BACKGROUND),
            new Item("Текст кнопок",        UIKey.BUTTON_FOREGROUND),
            new Item("Фон меню‑баров",      UIKey.MENU_BAR_BACKGROUND),
            new Item("Текст меню‑баров",    UIKey.MENU_BAR_FOREGROUND),
            new Item("Фон меню",            UIKey.MENU_BACKGROUND),
            new Item("Текст меню",          UIKey.MENU_FOREGROUND),
            new Item("Фон пунктов меню",    UIKey.MENU_ITEM_BACKGROUND),
            new Item("Текст пунктов меню",   UIKey.MENU_ITEM_FOREGROUND),
            new Item("Фон подсказок",        UIKey.TOOL_TIP_BACKGROUND),
            new Item("Текст подсказок",     UIKey.TOOL_TIP_FOREGROUND),
            new Item("Фон тулбара",         UIKey.TOOL_BAR_BACKGROUND),
            new Item("Текст тулбара",       UIKey.TOOL_BAR_FOREGROUND)
    };

    /** Карта UIKey → кнопка‑цвет. */
    private final Map<UIKey, JButton> btnMap = new EnumMap<>(UIKey.class);
    private final JFrame owner;

    public ThemeConfigDialog(JFrame owner) {
        super(owner, "Настройка темы", true);
        this.owner = owner;
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel grid = new JPanel(new GridLayout(0, 2, 5, 5));

        // формируем строки «подпись – кнопка‑цвет»
        for (Item it : ITEMS) {
            grid.add(new JLabel(it.label + ":"));
            JButton btn = createColorButton(it.label);
            // текущий цвет берём из SettingsManager (или UIManager, если нет)
            Color cur = SettingsManager.getUIKeyColor(it.key,
                    UIManager.getColor(it.key.key()));
            btn.setBackground(cur);
            btnMap.put(it.key, btn);
            grid.add(btn);
        }

        add(new JScrollPane(grid), BorderLayout.CENTER);

        /* ---------- Нижняя панель ---------- */
        JButton btnApply = new JButton("Применить");
        btnApply.addActionListener(e -> applyAndStay());

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(e -> { applyAndClose(); dispose(); });

        JButton btnCancel = new JButton("Отмена");
        btnCancel.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(btnApply);
        bottom.add(btnOk);
        bottom.add(btnCancel);
        add(bottom, BorderLayout.SOUTH);
    }

    /** Кнопка‑выбор цвета. */
    private static JButton createColorButton(String tip) {
        JButton b = new JButton();
        b.setPreferredSize(new Dimension(30, 20));
        b.setToolTipText(tip);
        b.addActionListener(e -> {
            Color cur = b.getBackground();
            Color chosen = JColorChooser.showDialog(b, "Выбор цвета", cur);
            if (chosen != null) b.setBackground(chosen);
        });
        return b;
    }

    /** Сборка Theme из выбранных пользователем цветов. */
    private Theme buildThemeFromControls() {
        Theme.Builder b = Theme.builder("Custom");
        btnMap.forEach((key, btn) -> b.put(key, btn.getBackground()));
        return b.build();
    }

    /** Сохранить набор цветов в Preferences (чтобы они выжили после рестарта). */
    private void persistTheme(Theme th) {
        // Сохраняем все UIKey‑цвета.
        for (UIKey key : UIKey.values()) {
            Color c = th.get(key);
            if (c != null) SettingsManager.setUIKeyColor(key, c);
        }
    }

    /** Применить тему и оставить диалог открытым. */
    private void applyAndStay() {
        Theme custom = buildThemeFromControls();
        persistTheme(custom);                // <-- сохраняем
        ThemePlugin.applyTheme(owner, custom);
        SwingUtilities.updateComponentTreeUI(owner);
        JOptionPane.showMessageDialog(this,
                "Тема применена.", "Готово",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /** Применить тему и закрыть диалог (OK). */
    private void applyAndClose() {
        Theme custom = buildThemeFromControls();
        persistTheme(custom);
        ThemePlugin.applyTheme(owner, custom);
    }
}
