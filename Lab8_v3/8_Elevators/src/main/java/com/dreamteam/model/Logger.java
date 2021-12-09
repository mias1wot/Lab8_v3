package com.dreamteam.model;

import java.io.File;

public class Logger{
    static private Logger loggerInstance;
    File file;

    private Logger(){}
    public static Logger getInstance(){
        if(loggerInstance == null) {
            loggerInstance = new Logger();
        }

        return loggerInstance;
    }
    public void log(int liftNumber, int floor, String text){
//        console.log(text);
    }
    public void close(){

    }
}