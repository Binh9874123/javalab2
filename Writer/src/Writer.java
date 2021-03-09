import java.util.HashMap;
import java.util.logging.Logger;
import java.io.FileOutputStream;
import java.io.IOException;

import ru.spbstu.pipeline.IExecutable;
import ru.spbstu.pipeline.IWriter;
import ru.spbstu.pipeline.RC;

public class Writer implements IWriter{
    WriterGrammar writerGrammar = new WriterGrammar();
    Integer bufSize;
    FileOutputStream fileOutputStream;
    final Logger logger;
    IExecutable consumer;
    IExecutable producer;

    public Writer(Logger logger){
        this.logger = logger;
    }
    @Override
    public RC setOutputStream(FileOutputStream file){
        if (file == null)
            return RC.CODE_INVALID_OUTPUT_STREAM;
        fileOutputStream = file;
        return RC.CODE_SUCCESS;
    }
    @Override
    public RC setConfig(String cfgPath){
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

    @Override
    public RC setProducer(IExecutable producer){
        if (producer == null)
            return RC.CODE_INVALID_ARGUMENT;
        this.producer = producer;
        return RC.CODE_SUCCESS;
    }
    @Override
    public RC execute(byte[] inputData){
        if (inputData == null)
            return RC.CODE_FAILED_TO_WRITE;

        try{
            fileOutputStream.write(inputData);
            return RC.CODE_SUCCESS;
        } catch (IOException ex) {
            logger.severe(LogMsg.FAILED_TO_WRITE.msg);
        }
        return RC.CODE_FAILED_TO_WRITE;
    }
}
