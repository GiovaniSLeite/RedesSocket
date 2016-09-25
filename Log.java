import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Log {


    public static void geraLog (String inserir) throws IOException{
        File arquivo = new File( "./Log.txt" );
        
        FileWriter fw = new FileWriter(arquivo, true);
        BufferedWriter bw = new BufferedWriter(fw);
                        
        bw.append(inserir+"\n");
        bw.close();
        fw.close();
        
    }
}
