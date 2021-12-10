package com.dreamteam.model;

import java.io.*;

public class Logger{
    static private Logger loggerInstance;
    PrintWriter file;

    private Logger(){
        final String path = ".\\logs\\logger.txt";
        try {
            file = new PrintWriter(new BufferedWriter(new FileWriter(path,false)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Logger getInstance(){
        if(loggerInstance == null) {
            loggerInstance = new Logger();
        }

        return loggerInstance;
    }
    public void log(int liftNumber, int floor, String text){
//        String message = "Lift #" + liftNumber + " at floor # " + floor + ": " + text + "\n";
//        file.write(message,0,message.length());
    }
    public void close(){
        if(file != null)
            file.close();
    }
}