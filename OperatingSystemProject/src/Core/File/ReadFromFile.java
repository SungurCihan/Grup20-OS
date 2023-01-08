package Core.File;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import Core.Queue.QueuePool;
import Core.Utilities.Helpers;
import Core.Process.Process;
public final class ReadFromFile {
	
	//Dosyadan veri okunan fonksiyon
	public static void ReadFile(String filePath) throws IOException
	{
		QueuePool.InitializeQueue();
		File file = new File(filePath);
		FileReader fileReader = new FileReader(file);
		String line;
		int number=-1;
		BufferedReader br = new BufferedReader(fileReader);
		while ((line = br.readLine()) != null) 
		{	
			line = line.replaceAll("\\s", "");
			number++;
			String[] parsedProcess = line.split(",");
			Process process = new Process();
			process.ArrivalTime = Integer.valueOf(parsedProcess[0]);
			process.Priority = Helpers.GetPriorityEnum(Integer.valueOf(parsedProcess[1]));
			process.BurstTime = Integer.valueOf(parsedProcess[2]);
			process.ProcessId = Helpers.CreatePid(number);
			QueuePool.add(process);
		}
		br.close();
		
		//Dosyadan okunup kuyuklara eklenen prosesler varýþ zamanlarýna göre sýralanýyor.
		QueuePool.SortAllQueues();
	}
			
}
