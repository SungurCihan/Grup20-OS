package Core.Dispatcher;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import Core.Queue.QueuePool;
import Core.Utilities.Priority;
import Core.Utilities.State;
import Core.Process.ProceessInterruptedModel;
import Core.Process.Process;

public class Dispatcher extends TimerTask {
	//Zamanlayıcı değişkenleri
	private static Timer Timer;
	private int timerCounter=0;
	@Override
	public void run() {
		System.out.println("Saniye  = "+timerCounter);
		CheckInterrupted(); //Zaman aşımına uğrayan proses olup olmadığı kontrol ediliyor.
		ClearTerminated();	//Zaman aşımına uğradığı için "TERMINATED" durumuna çekilmiş lakin kuyruktan sillinmemiş proses varsa kuyruktan siliniyor.
		LoopingQueues();	//Bütün kuyruklar dönülerek o saniye içerisinde varan proseslerin durumu "READY" olarak değiştiriliyor.
		timerCounter++;
		
		//İçinde bulunduğumuz saniye içerinde çalışmaya hazır durumda olan, 
		//dolayısıyla "Running Process" değişkeni içerisine yerleşmiş olan proses yürütülüyor.
		if(QueuePool.RunningProcess!=null) 
		{
			try {
				QueuePool.RunningProcess.Run(timerCounter);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Yürütülecek herhangi bir proses kalmadıysa görevlendirici sonlandırılıyor.
		if(QueuePool.CheckIfSchedulerEnded())
			{
			System.out.println("Gorevlendirici vazifesini laigiyla ifa etmek hususunda kiymet-i harbiyesi ala bir muvaffakiyet sagladi");
			Timer.cancel();
			}
	}
	
	//Zamanlayıcı başlatan fonksiyon
	public static void StartTimer() {
		System.out.println("Sayac Baslatildi.\n");

		Timer = new Timer();
		TimerTask task = new Dispatcher();
		Timer.schedule(task,0,1000);
	}
	
	//Bütün kuyrukları dönüp o an varanları hazır durumuna çeken fonskyion
	private void LoopingQueues() {
		
		if(QueuePool.getRealTimeQueue().getSize() != 0) //Kuyruk boş ise içeri girilmeyecek
		{
			for(int i = QueuePool.getRealTimeQueue().first;i <= QueuePool.getRealTimeQueue().last;i++)
			//Kuyruk dönülüyor
			{	
				Process currentProcess=QueuePool.getRealTimeQueue().getProcess(i); //Şu an üzerinde bulunulan proses
				Process nextProcess=QueuePool.getRealTimeQueue().getProcess(i+1);  //Bir sonraki proses
				
				//Proses bu saniye içerinde varmışsa durumu "READY" yapılıyor
				if(currentProcess.ArrivalTime == timerCounter)
				{
					currentProcess.ChangeState(State.READY);
					
					//Çalışmakta olan başka bir proses yoksa veya daha düşük öncelikli bir proses
					//çalışmaktaysa şu an varmış olan proses "RunningProcess" değişkenine atanarak
					//çalışmaya hazır olduğu ilan edilir.
					if(QueuePool.RunningProcess==null||QueuePool.RunningProcess.Priority!=Priority.P0) QueuePool.RunningProcess=currentProcess;
					
					//Bir sonraki proses, içerisinde bulunduğumuz saniye varmıyorsa döngü kırılır
					//ve kuyuruğun geri kalanı boşuna dönülmez. Bu iddia da bulanilmemizin sebebi
					//kuyruktaki prosesleri daha önce varış zamanlarına göre sıralamış olmamız.
					if(nextProcess!=null&&nextProcess.ArrivalTime!=timerCounter) 
						break;
				}
			}
		}
		if(QueuePool.getUserBasedQueue1().getSize() != 0) //Kuyruk boş ise içeri girilmeyecek
		{
			for(int i = QueuePool.getUserBasedQueue1().first;i <= QueuePool.getUserBasedQueue1().last;i++)
			//Kuyruk dönülüyor
			{	
				Process currentProcess=QueuePool.getUserBasedQueue1().getProcess(i); //Şu an üzerinde bulunulan proses
				Process nextProcess=QueuePool.getUserBasedQueue1().getProcess(i+1);	 //Bir sonraki proses
				
				//Proses bu saniye içerinde varmışsa durumu "READY" yapılıyor
				if(currentProcess.ArrivalTime == timerCounter)
				{
					currentProcess.ChangeState(State.READY);
					
					//Çalışmakta olan başka bir proses yoksa veya daha düşük öncelikli bir proses
					//çalışmaktaysa şu an varmış olan proses "RunningProcess" değişkenine atanarak
					//çalışmaya hazır olduğu ilan edilir.
					if(QueuePool.RunningProcess==null||QueuePool.RunningProcess.Priority==Priority.P2
							||QueuePool.RunningProcess.Priority==Priority.P3) QueuePool.RunningProcess=currentProcess;
					
					//Bir sonraki proses, içerisinde bulunduğumuz saniye varmıyorsa döngü kırılır
					//ve kuyuruğun geri kalanı boşuna dönülmez. Bu iddia da bulanilmemizin sebebi
					//kuyruktaki prosesleri daha önce varış zamanlarına göre sıralamış olmamız.
					if(nextProcess!=null&&nextProcess.ArrivalTime!=timerCounter)
						break;
				}
			}
		} 
		if(QueuePool.getUserBasedQueue2().getSize() != 0) //Kuyruk boş ise içeri girilmeyecek
		{
			for(int i = QueuePool.getUserBasedQueue2().first;i <= QueuePool.getUserBasedQueue2().last;i++)
			//Kuyruk dönülüyor
			{	
				Process currentProcess=QueuePool.getUserBasedQueue2().getProcess(i); //Şu an üzerinde bulunulan proses
				Process nextProcess=QueuePool.getUserBasedQueue2().getProcess(i+1);	 //Bir sonraki proses
				
				//Proses bu saniye içerinde varmışsa durumu "READY" yapılıyor
				if(currentProcess.ArrivalTime == timerCounter)
				{
					currentProcess.ChangeState(State.READY);
					
					//Çalışmakta olan başka bir proses yoksa veya daha düşük öncelikli bir proses
					//çalışmaktaysa şu an varmış olan proses "RunningProcess" değişkenine atanarak
					//çalışmaya hazır olduğu ilan edilir.
					if(QueuePool.RunningProcess==null||QueuePool.RunningProcess.Priority==Priority.P3) QueuePool.RunningProcess=currentProcess;
					
					//Bir sonraki proses, içerisinde bulunduğumuz saniye varmıyorsa döngü kırılır
					//ve kuyuruğun geri kalanı boşuna dönülmez. Bu iddia da bulanilmemizin sebebi
					//kuyruktaki prosesleri daha önce varış zamanlarına göre sıralamış olmamız.
					if(nextProcess!=null&&nextProcess.ArrivalTime!=timerCounter)
						break;
				}
			}
		}
		if(QueuePool.getUserBasedQueue3().getSize() != 0) //Kuyruk boş ise içeri girilmeyecek
		{
			for(int i = QueuePool.getUserBasedQueue3().first;i <= QueuePool.getUserBasedQueue3().last;i++)
			//Kuyruk dönülüyor
			{	
				Process currentProcess=QueuePool.getUserBasedQueue3().getProcess(i); //Şu an üzerinde bulunulan proses
				Process nextProcess=QueuePool.getUserBasedQueue3().getProcess(i+1);	 //Bir sonraki proses
				
				//Proses bu saniye içerinde varmışsa durumu "READY" yapılıyor
				if(currentProcess.ArrivalTime == timerCounter)
				{
					currentProcess.ChangeState(State.READY);
					
					//Çalışmakta olan başka bir proses yoksa, 
					// şu an varmış olan proses "RunningProcess" değişkenine atanarak
					//çalışmaya hazır olduğu ilan edilir.
					if(QueuePool.RunningProcess==null) QueuePool.RunningProcess=currentProcess;
					
					//Bir sonraki proses, içerisinde bulunduğumuz saniye varmıyorsa döngü kırılır
					//ve kuyuruğun geri kalanı boşuna dönülmez. Bu iddia da bulanilmemizin sebebi
					//kuyruktaki prosesleri daha önce varış zamanlarına göre sıralamış olmamız.
					if(nextProcess!=null&&nextProcess.ArrivalTime!=timerCounter)
						break;
				}
			}
		}

	}
	
	//Zaman aşımına uğrayan proses varsa tespit edip sonlandıran fonksiyon
	private void CheckInterrupted() {
		for (ProceessInterruptedModel item : QueuePool.getInterruptedProcesses()) {
		//Askıda olan proseslerin bulundğu dizi dönülüyor
			if(timerCounter - item.InterruptedTime == 20) //Zaman aşımına uğrama durumu tespit ediliypr
			{
				switch(item.Priority)
				//Zaman aşımına uğrayan proses öncelik durumuna göre ilgili kuyuruktan siliniyor.
				
				//Bir proses askıya alınmışsa, bu onun daha kullanıcı menşeli bir proses olduğuna
				//ve daha önce çalışmış olduğuna delalet eder. Dolayısıyla bu proses ya "2" ya da "3"
				//önceliğindedir. Bu iki ihtimal ayrı birer durum telakki edilerek ele alınıp 
				//sonlandırılma işlemi gerçekleştiriliyor.
				{
				case P2: //Prosesin "2" önceliğinde olduğu durum
				{
					for(int i = 0; i < QueuePool.getUserBasedQueue2().getSize(); i++)
					//"2" önceliğinde olan proseslerin tutulduğu kuyruk dönülüyor.
					{
						if(item.ProcessId == QueuePool.getUserBasedQueue2().getProcess(i).ProcessId)
						//İlgili proses tespit edilince durumu "TERMINATED" olarak değşitirilip
						//ekrana gerekli mesaj basılıyor.
							
						//Kuyuk veri yapısı işleyiş mantığı olarak kuyruğa eklenen ilk elemanı siler.
						//Burada zaman aşımına uğramış olan prosesin kuyruğun ilk elemanı olduğunu vaat 
						//edemediğimizden kuyruktan silme işlemi burada gerçekleştirilmiyor. Bu proses
						//kuyruğun ilk elemanı olduğu zaman bu durum tespit edilerek proses kuryuktan çıkarılacak.
						//220. satırdaki "ClearTerminated" fonksiyonu bu amaçla yazıldı.
						{
							Process newProcess = QueuePool.getUserBasedQueue2().getProcess(i);
							newProcess.State = State.TERMINATED;
							QueuePool.getUserBasedQueue2().change(i, newProcess);
							newProcess.PrintTimeout();
						}
					}
				}
					break;
				case P3: //Prosesin "3" önceliğinde olduğu durum (Yukarıdaki işlemler aynen uygulanıyor)
				{
					for(int i = 0; i < QueuePool.getUserBasedQueue3().getSize(); i++)
					{
						if(item.ProcessId == QueuePool.getUserBasedQueue3().getProcess(i).ProcessId)
						{
							Process newProcess = QueuePool.getUserBasedQueue3().getProcess(i);
							newProcess.State = State.TERMINATED;
							QueuePool.getUserBasedQueue3().change(i, newProcess);
							newProcess.PrintTimeout();
						}
					}
				}
					break;
				
				}
			}
		}
	}
	
	private void ClearTerminated() {
		//Kuyrupun ilk elemanının durumunun "TERMINATED" olması halinde 
		//kuyruktan silinme işlemi gerçekleştiriliyor. Bunun sebebi yukarıda(186. satır)
		//detaylı olarak açıklandı
		
		if(QueuePool.getUserBasedQueue2().getProcess(QueuePool.getUserBasedQueue2().first) != null && QueuePool.getUserBasedQueue2().getProcess(QueuePool.getUserBasedQueue2().first).State == State.TERMINATED)
			QueuePool.getUserBasedQueue2().remove();
		
		if(QueuePool.getUserBasedQueue3().getProcess(QueuePool.getUserBasedQueue3().first) != null && QueuePool.getUserBasedQueue3().getProcess(QueuePool.getUserBasedQueue3().first).State == State.TERMINATED)
			QueuePool.getUserBasedQueue3().remove();
	}
}
