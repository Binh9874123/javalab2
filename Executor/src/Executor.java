import ru.spbstu.pipeline.IExecutable;
import ru.spbstu.pipeline.IExecutor;
import ru.spbstu.pipeline.RC;

import java.util.HashMap;
import java.util.logging.Logger;

public class Executor implements IExecutor {
    String table = null;
    final ReadTable readTable;

    IExecutable consumer;
    IExecutable producer;
    final Logger logger;
    ExecutorGrammar executorGrammar = new ExecutorGrammar();

    public Executor(Logger logger){
        readTable = new ReadTable(logger);
        this.logger = logger;

    }



    @Override
    public RC setConfig(String cfgPath){

        HashMap<String,String> cfgParam = Syntactical.getConfig(cfgPath);
        if(cfgParam == null)
            return RC.CODE_CONFIG_GRAMMAR_ERROR;


        if (cfgParam.get(executorGrammar.token(1)) == null)
            return RC.CODE_CONFIG_SEMANTIC_ERROR;


        table = Semantic.getString(cfgParam, executorGrammar.token(1));
        readTable.FindTable(table);


        System.out.println(table);

        return RC.CODE_SUCCESS;

    }
    //readTable.FindTable(table);

    @Override
    public RC setConsumer(IExecutable consumer) {
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
        byte[] outputData = null;

         if(readTable.IsTable()) {
             outputData = readTable.Fix(inputData);
        }


        if (inputData == null)
            return consumer.execute(null);
        else {

            return consumer.execute(outputData);
        }

    }

}
