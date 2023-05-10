package com.simon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * 1. 并发 4 线程,每个线程生成 10 万组随机数，一组随机数里面有三个数
 * 2. 将这四个线程里面每组都是奇数的总组数，以（count1,count2,count3,count4)形式
 * 输出到文件 test.txt
 */
public class Main {

    static String fileName = "test.txt";

    public static void main(String[] args) {
        Random random = new Random();
        int limit = 100000;
        int innerLimit = 3;

        CompletableFuture<Integer> count1 = CompletableFuture.supplyAsync(new RandomSupplier(random, limit, innerLimit));
        CompletableFuture<Integer> count2 = CompletableFuture.supplyAsync(new RandomSupplier(random, limit, innerLimit));
        CompletableFuture<Integer> count3 = CompletableFuture.supplyAsync(new RandomSupplier(random, limit, innerLimit));
        CompletableFuture<Integer> count4 = CompletableFuture.supplyAsync(new RandomSupplier(random, limit, innerLimit));

        CompletableFuture.allOf(count1, count2, count3, count4);

        String result = "";
        try {
            result = count1.get() + "," + count2.get() + "," + count3.get() + "," + count4.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        File newFile = new File(fileName);
        try {
            if (!newFile.exists()) {
                newFile.createNewFile();
            }else{
                newFile.delete();
                newFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(newFile);
            outputStream.write(result.getBytes());
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

class RandomSupplier implements Supplier<Integer> {
    private Random r;
    private int limit;
    private int innerLimit;

    public RandomSupplier(Random r, Integer limit, Integer innerLimit) {
        this.r = r;
        this.limit = limit;
        this.innerLimit = innerLimit;
    }

    @Override
    public Integer get() {
        int result = 0;
        for (int i = 0; i < limit; i++) {
            boolean isAllOdd = true;
            for (int j = 0; j < innerLimit; j++) {
                int temp = r.nextInt();
                boolean isEven = 0 == temp % 2;
                if (isEven) {
                    isAllOdd = false;
                }
            }
            if (isAllOdd) {
                result++;
            }
        }
        return result;
    }
}