package net.pushl.whereis;

public class SharedInfo {
    
    public String hostPackageName = null;
    public boolean isResumed = false;
    
    
    
    
    private static SharedInfo instance;
    private SharedInfo(){}
    public static SharedInfo getInstance(){
        if(instance == null) instance = new SharedInfo();
        return instance;
    }
    
    
}
