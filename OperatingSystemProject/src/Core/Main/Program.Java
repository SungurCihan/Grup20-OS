package Core.Main;

import java.io.IOException;

import Core.Dispatcher.Dispatcher;
import Core.File.ReadFromFile;
import Core.Queue.QueuePool;
public class Program {

	public static void main(String[] args) throws IOException {
		//Veriler dosyadan okunuyor
		ReadFromFile.ReadFile(args[0].toString());
		
		//Zamanlayıcı ve dispatcher başlatılıyor.
		Dispatcher a = new Dispatcher();
		a.StartTimer();
	}
		
}

