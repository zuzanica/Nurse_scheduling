package algorithm;

import java.util.ArrayList;

public class AllocationVector {
	
	ArrayList<Allocation> x ;
	int fx;
	
	public class Allocation {	
		int n,d; // nurse, date
		String s; // shift type
		double weight = 0.0;
		
		public Allocation(int _n, int _d, String _s ){
			n = _n;
			d = _d;
			s = _s;
		}
		 
		public void setWeight(double _w){
			weight = _w;
		}
		
		@Override
	    public String toString(){
			return "("+this.n+", "+this.d+ ", "+ this.s + " | "+ this.weight +")";
		}
	}
	
	public AllocationVector(){
		x = new ArrayList<Allocation>();
		fx = 0;
	}
	
	public void addAllocation(int _n, int _d, String _s){
		x.add(new Allocation(_n, _d,_s));
	}
	
	public void addAllocation(Allocation a){
		x.add(a);
	}
	
	public void setFxWeight(int _fx){
		fx = _fx;
	}
	
	public ArrayList<Allocation> getX(){
		return x;
	}
	
	public int getFxWeight(){
		return fx;
	}
	
	public String toString(){
		String out = "[ ";
		for(int j = 0; j < x.size(); j++){
			out += x.get(j).toString() + ", ";
		}
		
		out += " | " + fx + " ]";
		return out;
	}
}
