import ru.spbstu.pipeline.BaseGrammar;

public class ManagerGrammar extends BaseGrammar {
    public ManagerGrammar(){
        super(new String[] {";","Reader", "Executor", "Writer", "Config", "input", "output"});
     }
}
