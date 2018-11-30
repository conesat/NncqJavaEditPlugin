package nncq.editplugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class ResetServiceNameApplication extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        BuildAnnotationApplication.SERVICE_NAME="";
    }
}
