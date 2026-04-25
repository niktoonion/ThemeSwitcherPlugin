package ui;

import java.awt.*;
import javax.swing.*;

/**
 * Вкладка‑панель, добавляемая в IDE.
 * Содержит одну кнопку «Настроить тему…», открывающую диалог.
 */
public final class ThemePanel extends JPanel {

    private final JFrame owner;

    public ThemePanel(JFrame owner) {
        this.owner = owner;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Управление темой IDE");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton open = new JButton("Настроить тему…");
        open.addActionListener(e -> new ThemeConfigDialog(owner).setVisible(true));
        center.add(open);
        add(center, BorderLayout.CENTER);
    }
}
