package net.floodlightcontroller.hand;

public class ClusterNotFoundException extends Exception {
	private static final long serialVersionUID = -8153300818250428652L;

	public ClusterNotFoundException (){
    }

	public ClusterNotFoundException (String message){
		super (message);
	}

	public ClusterNotFoundException (Throwable cause){
		super (cause);
    }

	public ClusterNotFoundException (String message, Throwable cause){
		super (message, cause);
    }

}
