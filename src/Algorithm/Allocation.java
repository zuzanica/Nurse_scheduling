package Algorithm;

public class Allocation {	
	int n,d;
	String s;
	
	public Allocation(int _n, int _d, String _s ){
		n = _n;
		d = _d;
		s = _s;
	}
	 
	@Override
    public String toString(){
		return "("+this.n+", "+this.d+ ", "+ this.s +")";
	}
}
