package Core.Process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Core.Queue.QueuePool;
import Core.Utilities.*;

//Proses s�n�f� tan�mlan�yor
public class Process {
	public String ProcessId; 				//PID
	public int ArrivalTime;					//Var�� Zaman�
	public int BurstTime;					//�cra edilce�i toplam s�re
	public Priority Priority; 				//�ncelik
	public State State;						//Durum
	private ProcessBuilder processBuilder;  //��letim sistemi proses ba�latmak i�in gerekli s�n�f
	
	//Kurucu s�n�f i�eriside "ProcessBuilder" de�i�kenin i�erisi dolduruluyor.
	public Process() {
		this.processBuilder= new ProcessBuilder();
	}
	
	//Prosesin durumunu de�i�tiren fonksiyon
	public void ChangeState(State state) {
		this.State=state;
		Print();
	}
	
	//Prosesin y�r�t�ld��� fonksiyon
	public void Run(int currentTime) throws IOException {
		switch(this.Priority) 
		{
		case P0: //REALTIME
		{
			//Durum "RUNNING" olarak de�i�tirilip �al��t�r�l�yor.
			ChangeState(Core.Utilities.State.RUNNING);
			StartProcess();
			break;
		}
		case P1: //USER - BASED (PRIORITY 1 )
		{
			this.State=Core.Utilities.State.RUNNING;
			StartProcess();
			if(this.BurstTime!=0)
			{
				//E�er proses noktalanmad�ysa 1 saniye �al��t�r�ld�ktan sonra 
				//ask�ya al�n�yor ve �nceli�i d���r�l�p, i�inde bulundu�u kuyruktan
				//silinmek suretiyle bir alt kuyru�a ekleniyor.
				QueuePool.remove(this.Priority);
				this.Priority=Core.Utilities.Priority.P2;
				QueuePool.add(this);
				ChangeState(Core.Utilities.State.INTERRUPTED);				
				QueuePool.getInterruptedProcesses().add(new ProceessInterruptedModel(this.ProcessId, currentTime, Core.Utilities.Priority.P2));
			}
			
			//S�z konusu proses, �al��ma mesuliyetini yerine getirip ask�ya al�nd��� i�in,
			//halihaz�rda �al��makta olan prosesin tutuldu�u de�i�kene, bir sonraki saniyede
			//�al��ma hakk�na sahip olan prosesin atanmas� i�in a�a��daki fonksiyon �a��r�l�yor.
			SetRunningProcess();
			break;
		}
		case P2:
			ChangeState(Core.Utilities.State.RUNNING);
			StartProcess();
			if(this.BurstTime!=0)
			{
				QueuePool.remove(this.Priority);
				this.Priority=Core.Utilities.Priority.P3;
				QueuePool.add(this);
				ChangeState(Core.Utilities.State.INTERRUPTED);
				if(!IfAlreadyInterrupted(this.ProcessId, currentTime))
					QueuePool.getInterruptedProcesses().add(new ProceessInterruptedModel(this.ProcessId, currentTime, Core.Utilities.Priority.P3));

			}
			SetRunningProcess();
			break;
		case P3:
			ChangeState(Core.Utilities.State.RUNNING);
			StartProcess();
			if(this.BurstTime!=0)
			{
				ChangeState(Core.Utilities.State.INTERRUPTED);
				if(!IfAlreadyInterrupted(this.ProcessId, currentTime))
					QueuePool.getInterruptedProcesses().add(new ProceessInterruptedModel(this.ProcessId, currentTime, Core.Utilities.Priority.P3));
				
				/*
				 ��erisinde bulundu�umuz kuyruktaki proseslerin �ncelik de�erinden daha d���k
				 �nceli�e sahip prosesler bulunmad���ndan �al��t�r�lan prosesler ba�ka bir 
				 kuyru�a eklenmiyor. Onun yerine bu kuyru�un �al��ma prensibinin "Round Rubin" 
				 mahiyeti kazanmas� i�in proses kuyruktan silinip kuyru�un en sonuna ekleniyor.
				 */
				QueuePool.remove(this.Priority);
				QueuePool.add(this);
				SetRunningProcess();
			}
			break;		
		}
	}
	
	//Durumu de�i�en fonksiyonun yeni durumunun ve di�er bilgilerinin ekrana bas�ld��� fonksiyon
	private void Print() 
	{
		String processInformation="(id:"+this.ProcessId+"    oncelik: "+this.Priority.toString()+"  kalan sure: "+this.BurstTime +" sn)";
		switch(this.State) 
		{
		case INTERRUPTED:
		{
			PrintColored(Constants.MESSAGE_INTERRUPTED+processInformation);
			break;
		}
		case READY:
		{
			PrintColored(Constants.MESSAGE_READY+processInformation);
			break;
		}
		case TERMINATED:
		{
			PrintColored(Constants.MESSAGE_TERMINATED+processInformation);
			break;
		}		
		}
	}
	
	//20 saniye boyunca s�ra gelmemesi sebebiyle zaman a��m�na u�rayan fonksiyon i�in
	//ekrana bilgi mesaj� bas�ld��� fonksiyon
	public void PrintTimeout()
	{
		String processInformation="(id:"+this.ProcessId+"    oncelik: "+this.Priority.toString()+"  kalan sure: "+this.BurstTime +" sn)";
		PrintColored(Constants.MESSAGE_TIMEOUT + processInformation);
	}
	
	//Prosesin y�r�t�ld��� fonksiyon
	private void StartProcess() throws IOException
	{	
		//Prosesin an itibariyle y�r�t�ld��� bilgisini ekrana basan ger�ek bir i�letim
		//sistemi prosesi ba�lat�l�yor.
		String processInformation="(id:"+this.ProcessId+"    oncelik: "+this.Priority.toString()+"  kalan sure: "+this.BurstTime +" sn)";
		String message = Constants.MESSAGE_RUNNING+processInformation;
		processBuilder.command("cmd", "/c", "echo "+ message);
		java.lang.Process process = processBuilder.start();
		ReadProcess(process);
		
		//Prosesin sonland�r�lm�� olma ihtimali de�erlendiriliyor.
		this.BurstTime--;
		if(this.BurstTime==0)
		{
			ChangeState(Core.Utilities.State.TERMINATED);
			QueuePool.remove(this.Priority);
			SetRunningProcess();
			return;
		}

		
	}
	
	//"ProcessBuilder.start()" ile ba�lat�lan proses kapsam�nda veri okunup konsola mesaj yazd�r�l�yor.
	private void ReadProcess(java.lang.Process process)
	{
		try (var reader = new BufferedReader(
	            new InputStreamReader(process.getInputStream()))) {

	            String line;

	            while ((line = reader.readLine()) != null) {
	            	PrintColored(line);
	            }

	        }catch(Exception e) {
	        	
	        }
	}
	
	//Konsol ekran�n renkli mesaj yazan fonksiyon
	private void PrintColored(String message) {
        System.out.println(Constants.COLOR_SPACE_PREFIX+ this.ProcessId + 1 + "m "+"    " + " "+message+Constants.COLOR_SPACE_SUFFIX);
	}
	
	//Bir sonraki saniyede hangi prosesin �al��aca��n�n belirlendi�i fonksiyon
	private void SetRunningProcess() {
		
		//Her kuyru�un ilk eleman� getiriliyor.
		Process firstItemOfRealTimeQueue=QueuePool.GetFirstItem(Core.Utilities.Priority.P0);
		Process firstItemOfUserBased1Queue=QueuePool.GetFirstItem(Core.Utilities.Priority.P1);
		Process firstItemOfUserBased2Queue=QueuePool.GetFirstItem(Core.Utilities.Priority.P2);
		Process firstItemOfUserBased3Queue=QueuePool.GetFirstItem(Core.Utilities.Priority.P3);

		
		//�lk eleman bo� de�ilse ve �al��maya haz�r durumda ise ger�ek zamanl� olana �ncelik
		//vererek �al��ma imkan� sunuluyor.
		if(firstItemOfRealTimeQueue!=null&&firstItemOfRealTimeQueue.State==Core.Utilities.State.READY)
			QueuePool.RunningProcess=firstItemOfRealTimeQueue;
		
		else if(firstItemOfUserBased1Queue!=null&&firstItemOfUserBased1Queue.State==Core.Utilities.State.READY)
			QueuePool.RunningProcess=firstItemOfUserBased1Queue;
		
		else if(firstItemOfUserBased2Queue!=null&&
				(firstItemOfUserBased2Queue.State==Core.Utilities.State.READY||
				firstItemOfUserBased2Queue.State==Core.Utilities.State.INTERRUPTED))
			QueuePool.RunningProcess=firstItemOfUserBased2Queue;
		
		else if(firstItemOfUserBased3Queue!=null&&
				(firstItemOfUserBased3Queue.State==Core.Utilities.State.READY||
				firstItemOfUserBased3Queue.State==Core.Utilities.State.INTERRUPTED))
			QueuePool.RunningProcess=firstItemOfUserBased3Queue;
		
		else
			QueuePool.RunningProcess=null;
	}
	
	//E�er ilgili proses zaten daha �nce ask�ya al�nanlar listesine eklenmi�diyse, 
	//tekrar eklenmek yerine ask�ya al�nd��� saniye de�eri g�ncelleniyor.
	private boolean IfAlreadyInterrupted(String processId, int currentTime)
	{
		for (ProceessInterruptedModel item : QueuePool.getInterruptedProcesses()) {
			if(item.ProcessId == processId) {
				item.InterruptedTime = currentTime;
				return true;
			}
		}
		return false;
	}
}
