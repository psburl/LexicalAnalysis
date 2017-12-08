package io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class SingletonInput {

    private static SingletonInput instance = null;
    
    private SingletonInput(){}
    
    private static String path = "input.txt";
    
    private List<String> content = new ArrayList<String>();
    
    public static SingletonInput getInstance(){
        
    	if(instance == null){
        	
			instance = new SingletonInput();
			instance.content = readFile(path);
        }
        
        return instance;
    }
      
    public static List<String> readFile(String path){
    	
    	try{
    		return Files.readAllLines(Paths.get(path));
    	}
    	catch(IOException e){
    		System.out.println("Exception opening file. Path: " + e.getMessage());
    		return new ArrayList<String>();
    	}
    }
    
    public List<String> getContentLines(){
    	return getInstance().content;
    }
}