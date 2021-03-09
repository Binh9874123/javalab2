import java.util.logging.Logger;
import java.io.*;
import java.util.HashMap;

import ru.spbstu.pipeline.IExecutable;
import ru.spbstu.pipeline.IReader;
import ru.spbstu.pipeline.RC;

import java.util.logging.Logger;

public class Reader implements IReader {
    private Integer Buffer;
    int fileSize;
    int ReadedData;
    FileInputStream inputFile;
    ReaderGrammar readerGrammar = new ReaderGrammar();
    IExecutable consumer;
    IExecutable producer;


    final Logger logger;

    public Reader(Logger logger){
        this.logger = logger;
    }
    private byte[] readFile(){
        if (inputFile == null)
            return  null;

        try{
            byte[] data = null;
            if (Buffer < fileSize - ReadedData){
                data = new byte[Buffer];
                inputFile.read(data);
                ReadedData += Buffer;
            }
            else if (fileSize - ReadedData > 0){
                data = new byte[fileSize - ReadedData];
                ReadedData = fileSize;
                inputFile.read(data);
            }
            return data;
        } catch (IOException ex) {
            logger.severe(LogMsg.FAILED_TO_READ.msg);
        }
        return null;
    }

    @Override
    public RC setInputStream(FileInputStream file){
        if (file == null)
            return RC.CODE_INVALID_INPUT_STREAM;
        inputFile = file;
        try {
            fileSize = (int)file.getChannel().size();
        } catch (IOException e) {
            logger.severe(LogMsg.INVALID_INPUT_STREAM.msg);
            return RC.CODE_INVALID_INPUT_STREAM;
        }
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC setConfig(String cfgPath){
        HashMap<String,String> cfgParam =
                Syntactical.getConfig(cfgPath);

        Buffer = Semantic.getInteger(cfgParam, readerGrammar.token(1));
        if (Buffer == null){
            logger.severe(LogMsg.INVALID_CONFIG_DATA.msg);
            return RC.CODE_CONFIG_SEMANTIC_ERROR;
        }
        logger.info(LogMsg.SUCCESS.msg);
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC setConsumer(IExecutable consumer){
        if (consumer == null)
            return RC.CODE_INVALID_ARGUMENT;
        this.consumer = consumer;
        return RC.CODE_SUCCESS;
    }


    public RC setProducer(IExecutable var1){
        producer = null;
        return RC.CODE_SUCCESS;
    }

    @Override
    public RC execute(byte[] inputData){
        byte[] data;
        RC rc = RC.CODE_SUCCESS;
        do{
            data = readFile();
            if (data == null)
                break;
            rc = consumer.execute(data);
            if (rc != RC.CODE_SUCCESS) {
                break;
            }
        }while (data != null);

        return rc;
    }

}
