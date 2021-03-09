import ru.spbstu.pipeline.*;
import ru.spbstu.pipeline.IExecutor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.logging.Logger;

public class Manager {
    IPipelineStep Start;
    static final ManagerGrammar managerGrammar = new ManagerGrammar();
    static final Logger logger = Logger.getLogger(Manager.class.getName());
    FileInputStream inputStream;
    FileOutputStream outputStream;
    public Manager(String cfgPath) {
        HashMap<String, String> hashMap = Syntactical.getConfig(cfgPath);

        RC rc = Conveyor(hashMap);
        if (rc != RC.CODE_SUCCESS) {
            Start = null;
            logger.severe(LogMsg.FAILED_PIPELINE_CONSTRUCTION.msg);
        }
        else
            logger.info(LogMsg.SUCCESS.msg);
    }
    private RC Conveyor(HashMap<String, String> hashMap){

        ArrayDeque<String> Names = new ArrayDeque<>();
        ArrayDeque<String> ConfigName = new ArrayDeque<>();
        SetDequeNames(hashMap ,Names ,  ConfigName);

        String input = hashMap.get(managerGrammar.token(5));
        String output = hashMap.get(managerGrammar.token(6));

        try {
            IReader reader = createClass(Names.pollFirst(), ConfigName.pollFirst());
            reader.setProducer(null);
            RC rc = setInputStream(reader, input);
            if (rc != RC.CODE_SUCCESS)
                return rc;

            Start = reader;
            IPipelineStep prevName = reader;
            IPipelineStep nextName = null;
            while (Names.size() > 1 && ConfigName.size() > 1) {
                nextName = createClass(Names.pollFirst(), ConfigName.pollFirst());
                nextName.setProducer(prevName);
                prevName.setConsumer(nextName);
                prevName = nextName;
            }
            IWriter writer = createClass(Names.pollFirst(), ConfigName.pollFirst());
            rc = setOutputStream(writer, output);
            if (rc != RC.CODE_SUCCESS)
                return rc;

            writer.setProducer(prevName);
            prevName.setConsumer(writer);

        } catch(IllegalAccessException |
                ClassNotFoundException |
                InstantiationException |
                NullPointerException |
                NoSuchMethodException |
                InvocationTargetException e) {
            return RC.CODE_CONFIG_SEMANTIC_ERROR;
        }
        return RC.CODE_SUCCESS;
    }
    private void SetDequeNames(HashMap<String, String> hashMap, ArrayDeque<String> Names, ArrayDeque<String> ConfigName){

        String readerName = managerGrammar.token(1);
        String readerConfig = managerGrammar.token(4) + managerGrammar.token(1);
        if (CreateDeque(hashMap, Names, readerName) != RC.CODE_SUCCESS ||
                CreateDeque(hashMap, ConfigName, readerConfig) != RC.CODE_SUCCESS)
            return;

        int i = 1;
        while(true){
            String ExecutorName = managerGrammar.token(2) + Integer.toString(i);
            String ExecutorConfig = managerGrammar.token(4) + managerGrammar.token(2)+ Integer.toString(i);
            if (CreateDeque(hashMap, Names, ExecutorName) != RC.CODE_SUCCESS ||
                    CreateDeque(hashMap, ConfigName, ExecutorConfig) != RC.CODE_SUCCESS)
                break;
            i++;
        }


        String writerName = managerGrammar.token(3);
        String writerConfig = managerGrammar.token(4) + managerGrammar.token(3);
        if (CreateDeque(hashMap, Names, writerName) != RC.CODE_SUCCESS ||
                CreateDeque(hashMap, ConfigName, writerConfig) != RC.CODE_SUCCESS) {
            return;
        }

    }

    private RC CreateDeque(HashMap<String, String> hashMap, ArrayDeque<String> Names, String token){
        String nextName = hashMap.get(token);
        if (nextName == null)
            return RC.CODE_FAILED_PIPELINE_CONSTRUCTION;
        Names.add(nextName);
        return RC.CODE_SUCCESS;
    }
    private RC setInputStream(IReader reader, String input){

        try{
            inputStream = new FileInputStream(input);
            System.out.println();
            return reader.setInputStream(inputStream);
        }
        catch (FileNotFoundException e) {
            inputStream = null;
            logger.severe(LogMsg.INVALID_INPUT_STREAM.msg);
            return RC.CODE_INVALID_INPUT_STREAM;
        }
    }

    private RC setOutputStream(IWriter writer, String output){

        try{
            outputStream = new FileOutputStream(output);
            return writer.setOutputStream(outputStream);
        } catch (FileNotFoundException ex) {
            outputStream = null;
            logger.severe(LogMsg.INVALID_OUTPUT_STREAM.msg);
            return RC.CODE_SUCCESS;
        }
    }
    private <T extends IConfigurable> T createClass(String className, String configClass)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {

        logger.info(LogMsg.TRY_CREATE_WORKER.msg + ":" + className);
        Class<?> workerClass = Class.forName(className);
        T worker = (T)workerClass.getConstructor(Logger.class).newInstance(logger);



        RC rc = worker.setConfig(configClass);

        if (rc != RC.CODE_SUCCESS) {
            logger.info(LogMsg.INVALID_ARGUMENTS.msg);
            return null;
        }

        logger.info(LogMsg.SUCCESS.msg);

        return worker;
    }

    public RC execute(){
        if (Start == null) {
            return RC.CODE_FAILED_PIPELINE_CONSTRUCTION;
        }
        RC rc = Start.execute(null);
        logger.info(rc.toString());
        Start = null;
        try {
            inputStream.close();
        } catch (IOException e) {
            logger.severe(LogMsg.INVALID_INPUT_STREAM.msg);
            rc = RC.CODE_INVALID_INPUT_STREAM;
        }
        try{
            outputStream.close();
        } catch (IOException e) {
            logger.severe(LogMsg.INVALID_OUTPUT_STREAM.msg);
            rc = RC.CODE_INVALID_OUTPUT_STREAM;
        }
        return rc;
    }

}
