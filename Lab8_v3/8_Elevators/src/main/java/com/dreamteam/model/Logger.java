package com.dreamteam.model;

import java.io.*;

public class Logger{
    static private Logger loggerInstance;
    PrintWriter file;

    private Logger(){
        String path = "F:\\university\\3 course 1 semester\\java\\8_v3_ours\\Lab8_v3\\8_Elevators\\src\\main\\java\\com\\dreamteam\\logger.txt";
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
//        console.log(text);
//        String message = "Lift #" + liftNumber + " at floor # " + floor + ": " + text + "\n";
//        file.write(message,0,message.length());
    }
    public void close(){
        file.close();
    }
}