package algorithm;

public class Allocation  implements Comparable<Object> {	
	int n,d; // nurse, date
	String s; // shift type
	double weight = 0.0;
	
	public Allocation(int _n, int _d, String _s ){
		n = _n;
		d = _d;
		s = _s;
	}
	 
	public Allocation clone(){
		return new Allocation(this.n, this.d, this.s);
	}
	
	public void setWeight(double _w){
		weight = _w;
	}
	
	
	@Override
	public int compareTo(Object o) {
		if (o instanceof Allocation) {
			Allocation a = ((Allocation)o);
			if (this.d == a.d)
			{
			    return 0;
			}
			else if (this.d > a.d)
			{
			    return 1;
			}
			else
			{
			    return -1;
			}
                }
		return 0;
	}
	
	@Override
    public String toString(){
		return "("+this.n+", "+this.d+ ", "+ this.s + " | "+ this.weight +")";
	}
}