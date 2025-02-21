package pers.rockygu.plugin.datetime;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;

import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DatetimeAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        CaretModel caretModel = editor.getCaretModel();

        String text;
        boolean isSelection = false;
        if(caretModel.getCurrentCaret().hasSelection()) {
            text = caretModel.getCurrentCaret().getSelectedText();
            isSelection = true;
        } else {
            text = getClipboardText();
        }

        String msg = convert(isSelection, text);
        showTip(msg, editor);
    }

    private static String convert(boolean isSelection, String text) {
        String msg = "";
        long time = 0;
        boolean isConvert = false;
        try {
            time = Long.parseLong(text);
            isConvert = true;
        } catch (NumberFormatException e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                // 解析日期字符串为LocalDateTime对象
                LocalDateTime localDateTime = LocalDateTime.parse(text, formatter);

                // 将LocalDateTime对象转换为Timestamp对象
                Timestamp timestamp = Timestamp.valueOf(localDateTime);

                // 输出Timestamp对象
                time = timestamp.getTime();
                isConvert = true;
            } catch (Exception e1) {

            }
        }

        if(isConvert)
        {
            Date date = new Date(time);
            msg = isSelection ? "---Text Datetime---\n" : "---Clipboard Datetime---\n";
            msg = msg + time + "\n" + date + "\n";
        }

        Date date = new Date();
        msg = msg + "---Current Datetime---\n" + date.getTime() + "\n" + date;
        return msg;
    }

    private void showTip(String msg, Editor editor) {
        ApplicationManager.getApplication().invokeLater(() -> JBPopupFactory.getInstance()
            .createHtmlTextBalloonBuilder(msg, null, Color.DARK_GRAY, null)
            .setHideOnAction(true)
            .createBalloon()
            .show(JBPopupFactory.getInstance().guessBestPopupLocation(editor), Balloon.Position.below));
    }

    public static String getClipboardText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);

        if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
