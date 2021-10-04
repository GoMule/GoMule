package gomule.gui;

import com.google.common.io.Closeables;
import gomule.util.D2Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FileManagerProperties {
    public static File getFileManagerPropertiesFile() throws IOException {
        File lProjectsDir = new File(D2Project.PROJECTS_DIR);
        if (!lProjectsDir.exists()) {
            lProjectsDir.mkdir();
        }

        File lProps = new File(D2Project.PROJECTS_DIR + File.separator + "projects.properties");
        if (!lProps.exists()) {
            lProps.createNewFile();
        }
        return lProps;
    }

    @SuppressWarnings("UnstableApiUsage")
    public static Properties loadFileManagerProperties() throws IOException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(getFileManagerPropertiesFile());
            Properties properties = new Properties();
            properties.load(fileInputStream);
            return properties;
        } finally {
            Closeables.closeQuietly(fileInputStream);
        }
    }

    public static void saveFileManagerProperties(Properties properties) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(getFileManagerPropertiesFile());
            properties.store(out, null);
        } catch (IOException ex) {
            D2FileManager.displayErrorDialog(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
