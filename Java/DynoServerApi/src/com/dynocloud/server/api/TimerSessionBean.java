//package com.dynocloud.server.api;
//
//import java.util.Date;
////import java.util.logging.System.out.println;
//import javax.annotation.Resource;
//import javax.ejb.Schedule;
//import javax.ejb.Singleton;
//import javax.ejb.Stateless;
//import javax.ejb.Timeout;
//import javax.ejb.Timer;
//import javax.ejb.TimerService;
//
//@Singleton
//public class TimerSessionBean {
//	
//    @Resource
//    TimerService timerService;
//
//    private Date lastProgrammaticTimeout;
//    private Date lastAutomaticTimeout;
//    
////    private System.out.println System.out.println = System.out.println.getSystem.out.println(
////            "com.sun.tutorial.javaee.ejb.timersession.TimerSessionBean");
//    
//    public void setTimer(long intervalDuration) {
//        System.out.println("Setting a programmatic timeout for "
//                + intervalDuration + " milliseconds from now.");
//        Timer timer = timerService.createTimer(intervalDuration, 
//                "Created new programmatic timer");
//    }
//    
//    @Timeout
//    public void programmaticTimeout(Timer timer) {
//        this.setLastProgrammaticTimeout(new Date());
//        System.out.println("Programmatic timeout occurred.");
//    }
//
//    @Schedule(second="5", minute="*", hour="*")
//    public void automaticTimeout() {
//        this.setLastAutomaticTimeout(new Date());
//        System.out.println("Automatic timeout occured");
//    }
//
//    public String getLastProgrammaticTimeout() {
//        if (lastProgrammaticTimeout != null) {
//            return lastProgrammaticTimeout.toString();
//        } else {
//            return "never";
//        }
//        
//    }
//
//    public void setLastProgrammaticTimeout(Date lastTimeout) {
//        this.lastProgrammaticTimeout = lastTimeout;
//    }
//
//    public String getLastAutomaticTimeout() {
//        if (lastAutomaticTimeout != null) {
//            return lastAutomaticTimeout.toString();
//        } else {
//            return "never";
//        }
//    }
//
//    public void setLastAutomaticTimeout(Date lastAutomaticTimeout) {
//        this.lastAutomaticTimeout = lastAutomaticTimeout;
//    }
//}