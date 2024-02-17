package up.visulog.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveFile {

    static Logger log = new Logger();

    /**
     *
     * @param json
     * @param fileName
     * @param extension without point
     */
    public static void save(String json, String fileName, String extension, String folderName){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy_HH-mm");

            File file = new File("histoGRAPH/output/"+extension+"/"+folderName);
            if(!file.exists()) {
                file.mkdirs();
            }
            file = new File(file.getPath() + "/" + fileName + "_" + formatter.format(new Date()) + "." + extension);
            if(file.exists()) {
                file.delete();
            }
            FileWriter writer = new FileWriter(file,false);
            writer.write(json);
            writer.close();
            if(file.exists()) {
                log.info("The file was created in: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
