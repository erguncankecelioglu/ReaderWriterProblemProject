package com.company;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {
    public static void main(String [] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ReadWriteLock RW = new ReadWriteLock();

        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));

        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));

    }
    // static Random instance for run processes
    public static Random rand = new Random();
}


class ReadWriteLock{
    // semaphore for write processes
    private Semaphore S = new Semaphore(1);

    // AtomicInteger for count of reader process
    private AtomicInteger readerCount = new AtomicInteger(0);

    public void readLock() {
        try {
            // firstly wait until write processes finished, and acquire a permit from writer queue
            S.acquire();

            // when all write processes finished, release our permit for get more read process
            S.release();

            // increment the readers count for lock writing processes coming after us
            readerCount.incrementAndGet();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    public void writeLock() {
        try {
            // firstly wait until write processes finished, and acquire a permit from writer queue
            S.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // wait until all read processes finished, that is, until the readerCount is zero
        while(readerCount.intValue() != 0)
        {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void readUnLock() {
        // when any reader is finished, decrease the readerCount
        readerCount.decrementAndGet();
    }
    public void writeUnLock() {
        // when any writer is finished, release the semaphore
        S.release();
    }

}




class Writer implements Runnable
{
    private ReadWriteLock RW_lock;


    public Writer(ReadWriteLock rw) {
        RW_lock = rw;
    }

    public void run() {
        while (true){
            RW_lock.writeLock();
            // sleep while process
            try {
                System.out.println("Writer " +Thread.currentThread().getId()+ " Started");
                Thread.sleep(Main.rand.nextInt(2500));
                System.out.println("Writer " +Thread.currentThread().getId()+ " Ended");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RW_lock.writeUnLock();
            // sleep after process end
            try {
                Thread.sleep(Main.rand.nextInt(2500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


}



class Reader implements Runnable
{
    private ReadWriteLock RW_lock;


    public Reader(ReadWriteLock rw) {
        RW_lock = rw;
    }
    public void run() {
        while (true){
            RW_lock.readLock();
            // sleep while process
            try {
                System.out.println("Reader " +Thread.currentThread().getId()+ " Started");
                Thread.sleep(Main.rand.nextInt(2500));
                System.out.println("Reader " +Thread.currentThread().getId()+ " Ended");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RW_lock.readUnLock();
            // sleep after process end
            try {
                Thread.sleep(Main.rand.nextInt(2500));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


}