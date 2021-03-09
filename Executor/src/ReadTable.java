import ru.spbstu.pipeline.IExecutable;
import ru.spbstu.pipeline.RC;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ReadTable {
    private static List<Integer> s1 = new ArrayList<Integer>();


    private static List<Integer> s2 = new ArrayList<Integer>();

    final Logger logger;
    public ReadTable(Logger logger)
    {

        this.logger = logger;
    }
    public void FindTable(String cfgPath)
    {
        if(cfgPath == null)
        {
            logger.severe(LogMsg.INVALID_TABLE.msg);
        }

        try
        {
            FileInputStream table = new FileInputStream(cfgPath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(table));
            String c;
            while ( ( c = bufferedReader.readLine()) != null )
            {
                s1.add((int)c.charAt(0));
                s2.add((int)c.charAt(2));
            }

        } catch(IOException ex)
        {
            logger.severe(LogMsg.FAILED_TO_READ.msg);
        }

    }
    public static boolean IsTable()
    {
        boolean ktra = true;
        if (s1.size() != s2.size())
        {
            ktra = false;

        }// Test the same amount
        for(int i = 0 ; i < s1.size() ; i++)
        {
            for(int j = 0 ; j < s2.size();j++)
            {
                if(s1.get(i) == s2.get(j))
                {
                    if(s2.get(i) != s1.get(j)) {
                        ktra = false;

                    }
                }
                if(s1.get(i) == s1.get(j))
                {
                    if(s2.get(i) != s2.get(j)) {
                        ktra = false;

                    }
                }
                if(s2.get(i) == s2.get(j))
                {
                    if(s1.get(i) != s1.get(j)) {
                        ktra = false;

                    }
                }
            }
        }
        return ktra;
    }
    public static byte[] Fix(byte[]buf )
    {
        for (int i = 0; i < buf.length; i++) {

            for (int j = 0; j < s1.size(); j++) {

                if (buf[i] == s1.get(j)) {
                    buf[i] = s2.get(j).byteValue();

                }
            }

        }
        return buf;
    }

}
