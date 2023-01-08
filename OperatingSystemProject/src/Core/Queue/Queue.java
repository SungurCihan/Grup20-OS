package Core.Queue;

import Core.Process.Process;

//Proseslerin i�erisinde tutulaca�� "Kuyruk S�n�f�" tan�mlan�yor.
public class Queue {
	public int first; //Kuyru�un ilk eleman�
    public int last;  //Kuyru�un son eleman�
    int capacity;     //Kuyru�un kapasitesi(i�erebilece�i maksimum eleman say�s�)
    Process array[];  //Kuyru�un i�erisinde tutlacak olan veri(Prosesler)
    int processCount; //Kuyrupun i�erisinde bulunan proses say�s�
        
    //Yap�c� fonksiyon i�erisinde varsay�lan atamalar yap�l�yor.
    public Queue(){
        this.first = 0;
        this.last=-1;
        this.array = new Process[1];
        this.capacity = 0;   
        this.processCount=0;
        expand(5);
    }
     
    //�lgili indexdeki prosesi getiren fonksiyon
    public Process getProcess(int index)
    {
    	if(array.length>index)
    		return array[index];
    	return null;
    }
    
    //Kuyru�un i�erisindeki proses say�s�n� d�nd�ren fonksiyon
    public int getSize() {
    	return processCount;
    }
    
    //Kuyru�a yeni proses ekleme kabiliyeti kazand�ran fonksyion
    public void add(Process process){ 
        if(last==capacity-1){ 
        	//Yeni bir proses eklemek i�in kapasitenin yetersiz oldu�u durumda kapasite geni�letiliyor.
        	expand(capacity);
        }
        last++;
        processCount++;
         array[last]=process;
       }   
    
    //Prosesin kapasitesi artt�r�larak daha fazla proses bar�nd�rabilme kabiliyeti kazand�r�l�yor.
    private void expand(int size) {
    	
		Process[] newArray =new Process [size+capacity];
		int newIndex=0;
	    for(int i=first;i<=last;i++)
	    //Kuyru�un i�erisinde bulunan prosesler yeni kapasite de�eri ile olulturulan diziye at�l�yor.
	    {
	    	newArray[newIndex] = array[i];   
	    	newIndex++;        
	    }
	    last=processCount-1;
		first=0;
		array=newArray;
		capacity+=size;
	}
    
    //�lgili indexteki prosesin bilgilerini g�ncelleyen fonksiyon
	public void change(int index,Process process){ 
 
		array[index]=process;
    }
    
	//Kuyuktan veri ��kartan fonksiyon
	//Kuyruk veri yap�s�n�n yap�s� gere�i kuyru�un ilk eleman� ��kart�l�yor.
    public void remove(){ 
        if(processCount!=0){
        	first++;
        	processCount--;
        }
    }
       
}