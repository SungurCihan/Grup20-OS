package Core.Process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import Core.Queue.QueuePool;
import Core.Utilities.*;

//Proses sýnýfý tanýmlanýyor
public class Process {
	public String ProcessId; 				//PID
	public int ArrivalTime;					//Varýþ Zamaný
	public int BurstTime;					//Ýcra edilceði toplam süre
	public Priority Priority; 				//Öncelik
	public State State;						//Durum
	private ProcessBuilder processBuilder;  //Ýþletim sistemi proses baþlatmak için gerekli sýnýf
	
	//Kurucu sýnýf içeriside "ProcessBuilder" deðiþkenin içerisi dolduruluyor.
	public Process() {
		this.processBuilder= new ProcessBuilder();
	}
	
	//Prosesin durumunu deðiþtiren fonksiyon
	public void ChangeState(State state) {
		this.State=state;
		Print();
	}
	
	//Prosesin yürütüldüðü fonksiyon
	public void Run(int currentTime) throws IOException {
		switch(this.Priority) 
		{
		case P0: //REALTIME
		{
			//Durum "RUNNING" olarak deðiþtirilip çalýþtýrýlýyor.
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
				//Eðer proses noktalanmadýysa 1 saniye çalýþtýrýldýktan sonra 
				//askýya alýnýyor ve önceliði düþürülüp, içinde bulunduðu kuyruktan
				//silinmek suretiyle bir alt kuyruða ekleniyor.
				QueuePool.remove(this.Priority);
				this.Priority=Core.Utilities.Priority.P2;
				QueuePool.add(this);
				ChangeState(Core.Utilities.State.INTERRUPTED);				
				QueuePool.getInterruptedProcesses().add(new ProceessInterruptedModel(this.ProcessId, currentTime, Core.Utilities.Priority.P2));
			}
			
			//Söz konusu proses, çalýþma mesuliyetini yerine getirip askýya alýndýðý için,
			//halihazýrda çalýþmakta olan prosesin tutulduðu deðiþkene, bir sonraki saniyede
			//çalýþma hakkýna sahip olan prosesin atanmasý için aþaðýdaki fonksiyon çaðýrýlýyor.
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
				 Ýçerisinde bulunduðumuz kuyruktaki proseslerin öncelik deðerinden daha düþük
				 önceliðe sahip prosesler bulunmadýðýndan çalýþtýrýlan prosesler baþka bir 
				 kuyruða eklenmiyor. Onun yerine bu kuyruðun çalýþma prensibinin "Round Rubin" 
				 mahiyeti kazanmasý için proses kuyruktan silinip kuyruðun en sonuna ekleniyor.
				 */
				QueuePool.remove(this.Priority);
				QueuePool.add(this);
				SetRunningProcess();
			}
			break;		
		}
	}
	
	//Durumu deðiþen fonksiyonun yeni durumunun ve diðer bilgilerinin ekrana basýldýðý fonksiyon
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
	
	//20 saniye boyunca sýra gelmemesi sebebiyle zaman aþýmýna uðrayan fonksiyon için
	//ekrana bilgi mesajý basýldýðý fonksiyon
	public void PrintTimeout()
	{
		String processInformation="(id:"+this.ProcessId+"    oncelik: "+this.Priority.toString()+"  kalan sure: "+this.BurstTime +" sn)";
		PrintColored(Constants.MESSAGE_TIMEOUT + processInformation);
	}
	
	//Prosesin yürütüldüðü fonksiyon
	private void StartProcess() throws IOException
	{	
		//Prosesin an itibariyle yürütüldüðü bilgisini ekrana basan gerçek bir iþletim
		//sistemi prosesi baþlatýlýyor.
		String processInformation="(id:"+this.ProcessId+"    oncelik: "+this.Priority.toString()+"  kalan sure: "+this.BurstTime +" sn)";
		String message = Constants.MESSAGE_RUNNING+processInformation;
		processBuilder.command("cmd", "/c", "echo "+ message);
		java.lang.Process process = processBuilder.start();
		ReadProcess(process);
		
		//Prosesin sonlandýrýlmýþ olma ihtimali deðerlendiriliyor.
		this.BurstTime--;
		if(this.BurstTime==0)
		{
			ChangeState(Core.Utilities.State.TERMINATED);
			QueuePool.remove(this.Priority);
			SetRunningProcess();
			return;
		}

		
	}
	
	//"ProcessBuilder.start()" ile baþlatýlan proses kapsamýnda veri okunup konsola mesaj yazdýrýlýyor.
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
	
	//Konsol ekranýn renkli mesaj yazan fonksiyon
	private void PrintColored(String message) {
        System.out.println(Constants.COLOR_SPACE_PREFIX+ this.ProcessId + 1 + "m "+"    " + " "+message+Constants.COLOR_SPACE_SUFFIX);
	}
	
	//Bir sonraki saniyede hangi prosesin çalýþacaðýnýn belirlendiði fonksiyon
	private void SetRunningProcess() {
		
		//Her kuyruðun ilk elemaný getiriliyor.
		Process firstItemOfRealTimeQueue=QueuePool.GetFirstItem(Core.Utilities.Priority.P0);
		Process firstItemOfUserBased1Queue=QueuePool.GetFirstItem(Core.Utilities.Priority.P1);
		Process firstItemOfUserBased2Queue=QueuePool.GetFirstItem(Core.Utilities.Priority.P2);
		Process firstItemOfUserBased3Queue=QueuePool.GetFirstItem(Core.Utilities.Priority.P3);

		
		//Ýlk eleman boþ deðilse ve çalýþmaya hazýr durumda ise gerçek zamanlý olana öncelik
		//vererek çalýþma imkaný sunuluyor.
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
	
	//Eðer ilgili proses zaten daha önce askýya alýnanlar listesine eklenmiþdiyse, 
	//tekrar eklenmek yerine askýya alýndýðý saniye deðeri güncelleniyor.
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
