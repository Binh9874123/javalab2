import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public class Semantic {
    static final Logger logger = Logger.getLogger(Semantic.class.getName());

    private Semantic() {}


    public static Integer getInteger(HashMap<String, String> variableMap, String token)
    {
        if (variableMap == null || token == null){
            logger.severe(LogMsg.INVALID_ARGUMENTS.msg);
            return null;
        }
        try{
            String strVal = variableMap.get(token);
            Integer value = Integer.parseInt(strVal);
            return value;
        }
        catch (NumberFormatException ex){
            logger.severe(LogMsg.NOT_FOUND_TOKEN.msg + token);
        }
        return null;

    }
    public static Double getDouble(HashMap<String, String> variableMap, String token){
        if (variableMap == null || token == null){
            logger.severe(LogMsg.INVALID_ARGUMENTS.msg);
            return null;
        }
        try{
            String strVal = variableMap.get(token);
            Double value = Double.parseDouble(strVal);
            return value;
        }
        catch (NumberFormatException | NullPointerException ex){
            logger.severe(LogMsg.NOT_FOUND_TOKEN.msg + token);
        }
        return null;
    }
    public static String getString(HashMap<String, String> variableMap, String token){
        if (variableMap == null || token == null){
            logger.severe(LogMsg.INVALID_ARGUMENTS.msg);
            return null;
        }
        try{
            String value = variableMap.get(token);
            if (value == null){
                logger.severe(LogMsg.CONFIG_SEMANTIC_ERROR.msg + "\n" + token);
            }
            return value;
        }
        catch (NumberFormatException ex){
            logger.severe(LogMsg.NOT_FOUND_TOKEN.msg + token);
        }
        return null;
    }

}
