package Core.Utilities;

//Priority deðerlerinin bulunduðu enum
public enum Priority {
	P0(0), //Real Time Process
	P1(1), //User Base Process(Priority = 1)
	P2(2), //User Base Process(Priority = 2)
	P3(3); //User Base Process(Priority = 3)
	
	public final int value;
	private Priority(int value) {
		this.value=value;
	}
}
