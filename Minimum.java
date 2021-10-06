package com.example.demo.exam;

import java.util.ArrayList;
import java.util.List;

public class Minimum {
    public static void main(String[] args) {
        int[] nums = new int[]{ -1, -1, -2, 4, 3 };
        System.out.println(excute(nums));
        nums = new int[]{-1, 0 };
        System.out.println(excute(nums));
        nums = new int[]{  0, 0, 0 };
        System.out.println(excute(nums));
    }
    public static int excute(int[] args){
        List<Integer> negative = new ArrayList<>();
        if(args.length == 1)return args[0];
        Integer lessNagative = args[0];
        Integer temp = args[0];
        if(args[0] < 0){
            negative.add(args[0]);
        }
        for(int i = 1 ; i < args.length;i++){
            if(args[i] < lessNagative){
                lessNagative = args[i];
            }
            if(args[i] != 0) {
                temp *=args[i];
            }
            if(args[i] < 0){
                negative.add(args[i]);
            }
        }
        if(negative.size() %2 == 0){
            return lessNagative;
        }else {
            return temp;
        }
    }
}
