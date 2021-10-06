package com.example.demo.exam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Visitors {

    public static void main(String[] args) throws ParseException {
        Visitors visitors2 = new Visitors();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date begin = sdf.parse("2021-10-04 08:00");
        Date end = sdf.parse("2021-10-04 22:00");
        List<Visitor> visitors = visitors2.generateVisitor(10);
        visitors = visitors2.excute(visitors,begin,end);
        for (Visitor visitor : visitors) {
            System.out.println("結果為:"+ visitor);
        }
    }
    private List<Visitor> excute(List<Visitor> visitors,Date begin,Date end){
        //取得某區間的Visitor
        List<Visitor> inPeriod = visitors.stream().filter(item -> {
            return inPeriod(item, begin, end);
        }).collect(Collectors.toList());
        inPeriod.sort((item,item2)->{
            if(item.getIn().getTime() > item2.getIn().getTime()){
                return 1;
            }else if(item.getIn().getTime() < item2.getIn().getTime()){
                return -1;
            }
            return 0;
        });
        for (Visitor visitor : inPeriod) {

            System.out.println("排序後:"+ visitor);

        }
        return v1(inPeriod);
    }
    public List<Visitor> v1(List<Visitor> visitors){
        if(visitors.size() == 0){
            System.out.println("這期間沒人光顧");
            return null;
        }else if(visitors.size() == 1){
            System.out.println("只有一人光顧");
            return visitors;
        }
        System.out.println("開始執行V1");
        Visitor[] v_array = new Visitor[visitors.size()];
        visitors.toArray(v_array);
        List<Visitor> current = new ArrayList<>();
        //
        Visitor earlyOut = v_array[0];
        current.add(v_array[0]);
        System.out.println("-+-+-+-+-+-+-+-+-+-");
        System.out.println("成功加入:" +v_array[0]);
        System.out.println("-+-+-+-+-+-+-+-+-+-");
        List<Visitor> max = new ArrayList<>();
        for(int i = 1 ; i < v_array.length;i++){
            Visitor n_v = v_array[i];
            if(!inSomeonePeriod(n_v,earlyOut)){
                System.out.println("遇到分岔點" +i+":" + n_v );
                for (Visitor visitor : current) {
                    System.out.println("找出新路前:" + visitor);
                }

                //
                if(current.size() > max.size()){
                    max = new ArrayList<>(current);
                    for (Visitor visitor : current) {
                        System.out.println("目前最長為:"+visitor);
                    }
                }

                //尋找最早離開，並移除此點，判斷目前最早離開者是否晚於分岔點的進入時間
                current.remove(earlyOut);
                System.out.println("排出:"+earlyOut);
                earlyOut = findLeaveEarly(current);
                //找出最早離開，並判斷他能不能走到分岔點，如果不能走到分岔點就刪除該點，最後如果沒有earlyOut，會跳出
                //會跳出只有兩種情況
                //1.可以到/不能到
                //2.沒有點
                while (!canOverBranch(earlyOut,n_v)){
                    current.remove(earlyOut);
                    System.out.println("排出:"+earlyOut);
                    earlyOut = findLeaveEarly(current);
                }
                //如果沒有能繼續的點，則將n_v設為最早的點
                if(earlyOut == null){
                    earlyOut = n_v;
                    System.out.println("沒有能繼續的點");
                }else {
                    System.out.println("有能繼續的點:"+ earlyOut);
                    for (Visitor visitor : current) {
                        System.out.println("找到新路後:"+visitor);
                    }
                }
            }
            if(n_v.getOut().getTime() < earlyOut.getOut().getTime()){
                earlyOut =n_v;
            }
            System.out.println("-+-+-+-+-+-+-+-+-+-");
            System.out.println("成功加入:" +v_array[i]);
            current.add(v_array[i]);
            System.out.println("-+-+-+-+-+-+-+-+-+-");
            System.out.println("");
        }
        if(current.size() > max.size()){
            return current;
        }
        return max;
    }
    public List<Visitor> generateVisitor(int count) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date begin = sdf.parse("2021-10-04 08:00");
        Date end = sdf.parse("2021-10-04 22:00");
        Long beginTime = begin.getTime();
        Long endTime = end.getTime();
        Long offset = endTime - beginTime;
        List<Visitor> visitors = new ArrayList<>();
        for(int i = 0 ; i < count;i++){
            double r1 = Math.random();
            double r2 = Math.random();
            Double in_full = null;
            Double out_full = null;
            Visitor visitor = null;
            if(r1 > r2){
                in_full =  r2 * offset + beginTime;
                out_full =  r1 * offset + beginTime;

            }else if(r1 == r2){
                do {
                    r1 = Math.random();
                    r2 = Math.random();
                }while ( r1 == r2);
                in_full =  r1 * offset + beginTime;
                out_full =  r2 * offset + beginTime;
            }else {
                in_full =  r1 * offset + beginTime;
                out_full =  r2 * offset + beginTime;
            }
            visitor = new Visitor(new Date(in_full.longValue()),new Date(out_full.longValue()));
            visitors.add(visitor);
        }
        return visitors;
    }

    /**
     * 計算人是否存在過某個區間
     * @param visitor
     * @param limitBegin 區間開始
     * @param limitEnd 區間結束
     * @return 存在則返回True
     */
    private boolean inPeriod(Visitor visitor, Date limitBegin, Date limitEnd){
        return !(visitor.getOut().getTime() < limitBegin.getTime() || visitor.getIn().getTime() > limitEnd.getTime());
    }
    /**
     * 用此方法前要保證someone的進入時間晚於other
     * @param someone
     * @param other
     * @return
     */
    private boolean inSomeonePeriod(Visitor other, Visitor someone){
        return someone.getOut().getTime() >= other.getIn().getTime();
    }

    private boolean canOverBranch(Visitor visitor,Visitor branch){
        if(visitor == null){
            System.out.println("沒點可用，返回CanOverBranch");
            return true;
        }
        return visitor.getOut().getTime() > branch.getIn().getTime();
    }
    private Visitor findLeaveEarly(List<Visitor> visitors){
        Visitor leaveEarly = null;
        for (Visitor visitor : visitors) {
            if(leaveEarly == null){
                leaveEarly = visitor;
            }else {
                if(visitor.getOut().getTime() < leaveEarly.getOut().getTime()){
                    leaveEarly = visitor;
                }
            }
        }
        if(leaveEarly == null){
            System.out.println("找不到最早離開的點");
        }
        return leaveEarly;
    }
}
@Data
@NoArgsConstructor
@AllArgsConstructor
class Visitor {
    private Date in;
    private Date out;

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String in_format = sdf.format(in);
        String out_format = sdf.format(out);
        return "Visitor{" +
                ", in=" + in_format +
                ", out=" + out_format +
                '}';
    }
}