
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

public class Syntactical {

    static final Logger logger = Logger.getLogger(Syntactical.class.getName());
    private Syntactical() {}


    static public HashMap<String, String> getConfig(String cfgPath)
    {

        ArrayList<String> config1 = new ArrayList<String>();
        if(cfgPath == null)
        {
            logger.severe(LogMsg.INVALID_ARGUMENTS.msg);
            return null;

        }
        logger.info(LogMsg.START_PARS.msg + "\n" + cfgPath);
        HashMap<String, String> hashMap = new HashMap<>();
        try {
            FileInputStream fileInputStream = new FileInputStream(cfgPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String c;

            while ((c = bufferedReader.readLine()) != null) {
                config1.add(c);


            }
            fileInputStream.close();
            bufferedReader.close();

        } catch (FileNotFoundException ex) {
            logger.severe(LogMsg.NO_CONFIG_FILE.msg);
        } catch (IOException ex) {

            logger.severe(LogMsg.CONFIG_GRAMMAR_ERROR.msg);
        }



        for (String i : config1) {
            String[] S = i.split("\\s");
            //ArrayList<String> config3 = new ArrayList(Arrays.asList(S));
            hashMap.put(S[0] , S[2]);

        }
        return hashMap;
    }


}
