package net.floodlightcontroller.hand;

public class HANDThreshold {
	
	public long tId;
	public long ruleAssoc;
	
	public MetricThreshold threshold;
	
	public enum MetricThreshold{
		NONE,	// Defaulted Rule, causes PASS
		GT, 	// Greater Than
		LT, 	// Less Than
		ET,		// Equal To
		LTE,    // Less Than or Equals
		GTE,	// Greater Than or Equals
		NE,		// Not Equal to
	}
	
	public double value; // value for metric threshold
	
	public  HANDThreshold(){
		this.tId = 0;
		this.ruleAssoc = 0;
		this.threshold = MetricThreshold.NONE;
		this.value = 0;
		
		//TODO
	}
	
	/**
	 * 		A negative return means the input values is LESS THAN threshold
	 * 		A positive return means the input is GREATER THAN threshold
	 * 		A return of 0 means that they are EQUAL
	 * 
	 * @param inputValue
	 * @return
	 */
	public double checkValue( double inputValue){
		//Compare the values
		double backwash = inputValue - this.value;
		
		return backwash;
	}
	
	
	
	/**
	 * Generate UID
	 * @return
	 */
	public int genUniqueId() {
		int uid = this.hashCode();
		if (uid <= 0 ){
			uid = Math.abs(uid);
			uid = uid * 1553;
		}
		return uid;
	}
	
	/**
	 * Hash Code
	 * @return
	 */
	@Override
	public int hashCode(){
		final int primeNum = 2521;
		int result = super.hashCode();
		
		//TODO
		
		return result;
		
	}

}
