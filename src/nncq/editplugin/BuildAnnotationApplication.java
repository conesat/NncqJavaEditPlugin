package nncq.editplugin;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import nncq.editplugin.tool.AnnotationBuilder;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildAnnotationApplication extends AnAction {
    public static Project mProject;
    public static String fileName="";

    int start=0,end=0;
    @Override
    public void actionPerformed(AnActionEvent event) {
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if (null == editor) {
            return;
        }
        mProject = event.getData(PlatformDataKeys.PROJECT);
        DataContext dataContext = event.getDataContext();
        if ("java".equals(getFileExtension(dataContext))) {
            //获取选中的文件
            VirtualFile file = DataKeys.VIRTUAL_FILE.getData(event.getDataContext());
            if (file != null) {
                SelectionModel selectionModel = editor.getSelectionModel();

                String selectedText = selectionModel.getSelectedText();
                if (StringUtils.isBlank(selectedText)) {
                    selectedText=editor.getDocument().getText();
                    start=0;
                    end=selectedText.length();
                }else {
                    start=selectionModel.getSelectionStart();
                    end=selectionModel.getSelectionEnd();
                }

                if (SERVICE_NAME.equals("")||!fileName.equals(file.getName())){
                    fileName=file.getName();
                   if ((SERVICE_NAME=getServiceName(editor.getDocument())).equals("")){
                       Messages.showMessageDialog(mProject, "未找到 @FeignClient value内容或者value为空", "异常", Messages.getInformationIcon());
                       return;
                   }
                }


                String finalSelectedText = selectedText;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        //先对空格、制表符进行换掉，然后再重新进行分隔处理。其中分行不处理，因为在写文章的时候复制的一些内容分行是有意义的。
                        editor.getDocument().replaceString(start, end, AnnotationBuilder.builderStart(finalSelectedText,SERVICE_NAME));
                        if (!finalImp.equals("")&&finalNum!=0){
                            editor.getDocument().insertString(finalNum,"\n\n"+ finalImp);
                            finalNum = 0;
                            finalImp="";
                        }

                    }
                };
                WriteCommandAction.runWriteCommandAction(event.getData(PlatformDataKeys.PROJECT), runnable);

              //  Messages.showMessageDialog(mProject, file.getName(), "select file", Messages.getInformationIcon());
            }
        }
    }


    @Override
    public void update(AnActionEvent event) {
        boolean show=true;
        //在Action显示之前,根据选中文件扩展名判定是否显示此Action
        String extension = getFileExtension(event.getDataContext());
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        if (null == editor) {
            show=false;
        }
        this.getTemplatePresentation().setEnabled(extension != null && "java".equals(extension)&&show);
    }

    public static String getFileExtension(DataContext dataContext) {
        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(dataContext);
        return file == null ? null : file.getExtension();
    }

    private boolean ZHUSHI = false;
    public static String SERVICE_NAME = "";
    private int finalNum = 0;
    private String finalImp="";

    /**
     * 提取服务名
     *
     * @param document
     */
    public String getServiceName(Document document) {
        String string=document.getText();
        StringBuilder input=new StringBuilder(string);
        String scan="";
        String imp="";
        if (string.indexOf("import org.springframework.cloud.openfeign.FeignClient;")==-1){
            imp+="import org.springframework.cloud.openfeign.FeignClient;\n";
        }
        if (string.indexOf("import org.springframework.security.access.prepost.PreAuthorize;")==-1){
            imp+="import org.springframework.security.access.prepost.PreAuthorize;\n";
        }
        if (string.indexOf("import org.springframework.web.bind.annotation.RequestMapping;")==-1){
            imp+="import org.springframework.web.bind.annotation.RequestMapping;\n";
        }

        if (!imp.equals("")){
            int num=string.indexOf("package");
            if (num!=-1){
                for (;num<string.length();num++){
                    if (string.charAt(num)=='\n'){
                        finalNum = num;
                        finalImp = imp;
                        break;
                    }
                }
            }else {
                finalNum = 0;
                finalImp = imp;
            }
        }

        for (int i=0;i<input.length()-1;i++){
            if ((input.charAt(i)=='/'&&input.charAt(i+1)=='/')||(input.charAt(i)=='/'&&input.charAt(i+1)=='*')&&!ZHUSHI){
                if (i>6&&!(input.substring(i-6,i).equals("https:"))){
                    ZHUSHI=true;
                }
            }else if ((input.charAt(i)=='/'&&input.charAt(i+1)=='/')||(input.charAt(i)=='*'&&input.charAt(i+1)=='/')&&ZHUSHI){
                if (i>6&&!(input.substring(i-6,i).equals("https:"))){
                    ZHUSHI=false;
                }
            }
            if (ZHUSHI){
                continue;
            }else {

                if (i<input.length()-12&&input.substring(i,i+12).equals("@FeignClient")){
                    scan="";
                    i+=12;
                    for (;i<input.length()&&input.charAt(i)!='\n';i++){
                        scan+=input.charAt(i);
                    }
                    Pattern pattern1 = Pattern.compile("\"(.*?)\"");
                    Matcher matcherTime = pattern1.matcher(scan);
                    if (matcherTime.find()) {
                        return matcherTime.group(1);
                    }
                }
            }
        }
        return "";
    }
}
