package Core.Queue;

import Core.Process.Process;

//Proseslerin içerisinde tutulacaðý "Kuyruk Sýnýfý" tanýmlanýyor.
public class Queue {
	public int first; //Kuyruðun ilk elemaný
    public int last;  //Kuyruðun son elemaný
    int capacity;     //Kuyruðun kapasitesi(içerebileceði maksimum eleman sayýsý)
    Process array[];  //Kuyruðun içerisinde tutlacak olan veri(Prosesler)
    int processCount; //Kuyrupun içerisinde bulunan proses sayýsý
        
    //Yapýcý fonksiyon içerisinde varsayýlan atamalar yapýlýyor.
    public Queue(){
        this.first = 0;
        this.last=-1;
        this.array = new Process[1];
        this.capacity = 0;   
        this.processCount=0;
        expand(5);
    }
     
    //Ýlgili indexdeki prosesi getiren fonksiyon
    public Process getProcess(int index)
    {
    	if(array.length>index)
    		return array[index];
    	return null;
    }
    
    //Kuyruðun içerisindeki proses sayýsýný döndüren fonksiyon
    public int getSize() {
    	return processCount;
    }
    
    //Kuyruða yeni proses ekleme kabiliyeti kazandýran fonksyion
    public void add(Process process){ 
        if(last==capacity-1){ 
        	//Yeni bir proses eklemek için kapasitenin yetersiz olduðu durumda kapasite geniþletiliyor.
        	expand(capacity);
        }
        last++;
        processCount++;
         array[last]=process;
       }   
    
    //Prosesin kapasitesi arttýrýlarak daha fazla proses barýndýrabilme kabiliyeti kazandýrýlýyor.
    private void expand(int size) {
    	
		Process[] newArray =new Process [size+capacity];
		int newIndex=0;
	    for(int i=first;i<=last;i++)
	    //Kuyruðun içerisinde bulunan prosesler yeni kapasite deðeri ile olulturulan diziye atýlýyor.
	    {
	    	newArray[newIndex] = array[i];   
	    	newIndex++;        
	    }
	    last=processCount-1;
		first=0;
		array=newArray;
		capacity+=size;
	}
    
    //Ýlgili indexteki prosesin bilgilerini güncelleyen fonksiyon
	public void change(int index,Process process){ 
 
		array[index]=process;
    }
    
	//Kuyuktan veri çýkartan fonksiyon
	//Kuyruk veri yapýsýnýn yapýsý gereði kuyruðun ilk elemaný çýkartýlýyor.
    public void remove(){ 
        if(processCount!=0){
        	first++;
        	processCount--;
        }
    }
       
}