package net.sf.jabref.gui.desktop.os;

import net.sf.jabref.external.ExternalFileType;
import net.sf.jabref.external.ExternalFileTypes;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class OSX implements NativeDesktop {
    @Override
    public void openFile(String filePath, String fileType) throws IOException {
        ExternalFileType type = ExternalFileTypes.getInstance().getExternalFileTypeByExt(fileType);

        if (type != null && !type.getOpenWithApplication().isEmpty()) {
            openFileWithApplication(filePath, type.getOpenWithApplication());
        } else {
            String[] cmd = { "/usr/bin/open", filePath };
            Runtime.getRuntime().exec(cmd);
        }
    }

    @Override
    public void openFileWithApplication(String filePath, String application) throws IOException {
        // Use "-a <application>" if the app is specified, and just "open <filename>" otherwise:
        String[] cmd = (application != null) && !application.isEmpty() ?
                new String[] {"/usr/bin/open", "-a", application, filePath} :
                new String[] {"/usr/bin/open", filePath};
        Runtime.getRuntime().exec(cmd);
    }

    @Override
    public void openFolderAndSelectFile(String filePath) throws IOException {
        File file = new File(filePath);
        Desktop.getDesktop().open(file.getParentFile());
    }

    @Override
    public void openConsole(String absolutePath) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("open -a Terminal " + absolutePath, null, new File(absolutePath));
    }

    @Override
    public String detectProgramPath(String programName, String directoryName) {
        return programName;
    }
}
