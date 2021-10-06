package com.example.demo.exam;

public class Fibonacci {
    public static void main(String[] args) {
        Fibonacci fibonacci = new Fibonacci();
        System.out.println(fibonacci.excute(10));
        System.out.println(fibonacci.excute(15));
        System.out.println(fibonacci.excute(20));
        System.out.println(fibonacci.excute(25));
        System.out.println(fibonacci.excute(30));
        System.out.println(fibonacci.excute(35));
    }
    public int excute(int index){
        if(index == 0 )return 0;
        else if(index == 1)return 1;
        int[] numbers = new int[index + 1];
        numbers[0] = 0;
        numbers[1] = 1;
        for(int i = 2 ; i <= index;i++){
            numbers[i] = numbers[i - 1 ] + numbers[i - 2];
        }
        return numbers[index];
    }
}
