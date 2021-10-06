package com.example.demo.exam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.*;

public class Poker {
    public static boolean LOG = true;
    public static void main(String[] args) {
        Poker poker = new Poker();
        Deck deck = new Deck();
        List<Card> player_1 = deck.take(5);
        for (Card card : player_1) {
            if(LOG)System.out.println("Player 1 的牌有 : " + card);
        }
        List<Card> player_2 = deck.take(5);
        for (Card card : player_2) {
            if(LOG)System.out.println("Player 2 的牌有 : " + card);
        }
        Level level_player_1 = poker.checkLevel(player_1);
        int p1Kind = level_player_1.getKind();
        Level level_player_2 = poker.checkLevel(player_2);
        if(LOG)System.out.println("player 1 組合為:" + level_player_1);
        if(LOG)System.out.println("player 2 組合為:" + level_player_2);
        if(level_player_1.getKind() < level_player_2.getKind()){
            if(LOG)System.out.println("player 1 勝利");
        }else if(level_player_1.getKind() > level_player_2.getKind()){
            if(LOG)System.out.println("player 2 勝利");
        }else {
            if(level_player_1.getNumber() > level_player_2.getNumber()){
                if(LOG)System.out.println("player 1 勝利");
            }else if(level_player_1.getNumber() < level_player_2.getNumber()){
                if(LOG)System.out.println("player 2 勝利");
            }else {
                if(level_player_1.getSuit() > level_player_2.getSuit()){
                    if(LOG)System.out.println("player 1 勝利");
                }else {
                    if(LOG)System.out.println("player 2 勝利");
                }
            }
        }
        //同花順 0  -> 鐵支 1 -> 葫蘆 2  -> 同花 3 ->順 4  -> 對 5 -> 單 > 6
    }
    public Level checkLevel(List<Card> cards){
        straightSort(cards);
        int[] numberCounts = new int[14];
        Set<Integer> values_set = new HashSet<>();
        Set<Integer> suits_set = new HashSet<>();
        for (Card card : cards) {
            numberCounts[card.getNumber()]++;
            values_set.add(card.getNumber());
            suits_set.add(card.getSuit());
        }
        //
        Level straightflush = straightflush(cards, values_set, suits_set);
        if(straightflush != null)return straightflush;
        Level fourOfaKind = fourOfaKind(cards, values_set, numberCounts);
        if(fourOfaKind != null)return fourOfaKind;
        Level fullHouse = fullHouse(cards, values_set, numberCounts);
        if(fullHouse != null)return fullHouse;
        Level flush = flush(cards, suits_set);
        if(flush != null)return flush;
        Level straight = straight(cards, values_set);
        if(straight !=null)return straight;
        Level pair = pair(cards, values_set, numberCounts);
        if(pair != null)return pair;
        Level single = single(cards);
        if(single != null)return single;
        throw new RuntimeException("此牌不是任何組合");
    }

    private void straightSort(List<Card> cards){
        //排完會以1開頭
        cards.sort((c1,c2)->{
            return c1.getNumber() - c2.getNumber();
        });
        //把1放到最後
        int oneCount = 0;
        for (Card card : cards) {
            if(card.getNumber() == 1) oneCount++;
            else break;
        }
        List<Card> temp = new ArrayList<>();
        for (int i = oneCount; i < cards.size(); i++) {
            temp.add(cards.get(i));
        }
        for (int i = 0; i < oneCount; i++) {
            temp.add(cards.get(i));
        }
        cards = temp;
    }
    private Map<Integer,Integer>scoreTable = new HashMap<>();
    public Poker(){
        scoreTable.put(1,13);
        scoreTable.put(2,1);
        scoreTable.put(3,2);
        scoreTable.put(4,3);
        scoreTable.put(5,4);
        scoreTable.put(6,5);
        scoreTable.put(7,6);
        scoreTable.put(8,7);
        scoreTable.put(9,8);
        scoreTable.put(10,9);
        scoreTable.put(11,10);
        scoreTable.put(12,11);
        scoreTable.put(13,12);
    }
    private Integer getScore(Integer number){
        return scoreTable.get(number);
    }
    private Level straightflush(List<Card> cards,Set<Integer> values_set,Set<Integer> suits_set){
        if(LOG)System.out.println("開始判斷同花順");
        if(values_set.size() != 5){
            if(LOG)System.out.println("非同花順，牌不滿五種");
            return null;
        }
        if(suits_set.size() != 1){
            if(LOG)System.out.println("非同花順，顏色超過一種");
            return null;
        }
        if(isStraight(cards)){
            Card lastCard = cards.get(cards.size() - 1);
            return new Level(0,lastCard.getSuit(),lastCard.getNumber());
        }
        return null;
    }
    private Level straight(List<Card> cards,Set<Integer> values_set){
        if(LOG)System.out.println("開始判斷順子");
        if(values_set.size() != 5){
            if(LOG)System.out.println("非順子，牌不滿五種");
            return null;
        }
        if(isStraight(cards)){
            Card lastCard = cards.get(cards.size() - 1);
            return new Level(4,lastCard.getSuit(),lastCard.getNumber());
        }
        return null;
    }
    /* isStraight
     *
     * @param cards 必須確保card的1是放在最後面
     * @return
     */
    private Boolean isStraight(List<Card> cards){
        if(LOG)System.out.println("判斷是否為順子");
        boolean before4Isstraight = true;
        int cardSize = cards.size();
        for (int i = 1; i < cardSize - 1 ; i++) {
            if(cards.get(i).getNumber() != cards.get(i -1).getNumber() + 1){
                before4Isstraight = false;
                break;
            }
        }
        if(!before4Isstraight){
            return false;
        }
        if(before4Isstraight){
            Integer lastValue = cards.get(cardSize -1).getNumber();
            Integer secondLastValue = cards.get(cardSize -2).getNumber();
            // 10 11 12 13 1
            if(lastValue == 1 && secondLastValue==13){
                return true;
            }else if(lastValue == secondLastValue + 1) {
                return true;
            }
        }
        return false;
    }
    private Level fourOfaKind(List<Card> cards,Set<Integer> values_set,int[] numberCounts){
        if(LOG)System.out.println("開始判斷鐵支");
        if(values_set.size() != 2){
            if(LOG)System.out.println("非鐵支，牌不等於兩種");
            return null;
        }
        if(!isFourKindOrFullHouse(values_set, numberCounts)){
            if(LOG)System.out.println("非鐵支，牌堆必須分為一四");
            return null;
        }
        //占比比較多的牌，如44441就是4比較多
        int more = 0;
        for (Integer value : values_set) {
            if(more == 0){
                more = value;
            }
            else if(numberCounts[value] > numberCounts[more]){
                more = value;
            }
        }
        Card mostValuedCard = null;
        for (Card card : cards) {
            if(card.getNumber() == more){
                mostValuedCard = card;
                break;
            }
        }
        return new Level(1,mostValuedCard.getSuit(),mostValuedCard.getNumber());
    }
    private Level fullHouse(List<Card> cards,Set<Integer> values_set,int[] numberCounts){
        if(LOG)System.out.println("開始判斷葫蘆");
        if(values_set.size() != 2){
            if(LOG)System.out.println("非葫蘆，牌不等於兩種");
            return null;
        }
        if(isFourKindOrFullHouse(values_set, numberCounts)){
            if(LOG)System.out.println("非葫蘆，牌堆必須分為二三");
            return null;
        }
        //計算哪張牌比較多
        int more = 0;
        for (Integer value : values_set) {
            if(more == 0){
                more = value;
            }
            else if(numberCounts[value] > numberCounts[more]){
                more = value;
            }
        }
        Card mostValuedCard = null;
        for (Card card : cards) {
            if(card.getNumber() == more){
                mostValuedCard = card;
                break;
            }
        }
        return new Level(2,mostValuedCard.getSuit(),mostValuedCard.getNumber());
    }
    /**
     * @param values_set 牌種類
     * @param numberCounts 此牌種有多少張
     * @return 如果返回true代表為鐵支
     */
    private boolean isFourKindOrFullHouse(Set<Integer> values_set, int[] numberCounts){
        int more = 0;
        for (Integer value : values_set) {
            if(numberCounts[value] > more){
                more = value;
            }
        }
        if(numberCounts[more] == 2 || numberCounts[more] == 3){
            return false;
        }else {
            return true;
        }
    }
    private Level flush(List<Card> cards,Set<Integer> suits_set){
        if(LOG)System.out.println("開始判斷同花");
        if(suits_set.size() != 1){
            if(LOG)System.out.println("非同花，牌堆有大於一種的花色");
            return null;
        }
        Card mostValuedCard = mostValuedCard(cards);
        return new Level(3,mostValuedCard.getSuit(),mostValuedCard.getNumber());
    }
    private Level pair(List<Card> cards,Set<Integer> values_set,int[] numberCounts){
        if(LOG)System.out.println("開始判斷pair");
        if(values_set.size() == 5){
            if(LOG)System.out.println("非pair，牌堆不能有五種不同的值");
            return null;
        }
        List<Card> pair = new ArrayList<>();
        for (Card card : cards) {
            if(numberCounts[card.getNumber()] >= 2){
                pair.add(card);
            }
        }
        Card mostValuedCard = mostValuedCard(pair);
        return new Level(5,mostValuedCard.getSuit(),mostValuedCard.getNumber());
    }
    private Level single(List<Card> cards){
        if(LOG)System.out.println("開始判斷單張");
        Card mostValuedCard = mostValuedCard(cards);
        return new Level(6,mostValuedCard.getSuit(),mostValuedCard.getNumber());
    }
    private Card mostValuedCard(List<Card> cards){
        Card more_card = null;
        for (Card card : cards) {
            if(more_card == null){
                more_card = card;
            }else if(getScore(card.getNumber()) > getScore(more_card.getNumber())){
                more_card = card;
            }
        }
        return more_card;
    }
    //    public Level straightLevel(List<Card> cards){
//        straightSort(cards);
//        if(LOG)System.out.println("開始判斷順子");
//        int cardSize = cards.size();
//
//        if(cardSize != 5){
//            if(LOG)System.out.println("至少有一張的大小重複");
//            if(LOG)System.out.println("判斷順子結束");
//            return null;
//        }
//        Set<Integer> suitsCount = new HashSet<>();
//        for (Card card : cards) {
//            suitsCount.add(card.getSuit());
//        }
//        if(LOG)System.out.println("有" + suitsCount.size()+"種花色");
//        boolean before4Isstraight = true;
//        for (int i = 1; i < cardSize - 1 ; i++) {
//            if(cards.get(i).getNumber() != cards.get(i -1).getNumber() + 1){
//                before4Isstraight = false;
//                break;
//            }
//        }
//        if(before4Isstraight){
//            Integer lastValue = cards.get(cardSize -1).getNumber();
//            Integer lastSuit = cards.get(cardSize -1).getSuit();
//            Integer secondLastValue = cards.get(cardSize -2).getNumber();
//            if(lastValue == 1 && secondLastValue==13){
//                if(suitsCount.size() == 1){
//                    return new Level(0,lastSuit,lastValue);
//                }else {
//                    return new Level(4,lastSuit,lastValue);
//                }
//            }else if(lastValue == secondLastValue + 1) {
//                if(suitsCount.size() == 1){
//                    return new Level(0,lastSuit,lastValue);
//                }else {
//                    return new Level(4,lastSuit,lastValue);
//                }
//            }
//        }
//        return null;
//    }
//    public Level notStraightLevel(List<Card> cards){
//        if(LOG)System.out.println("開始判斷非順子");
//        Level level = null;
//        int[] numbers = new int[14];
//        //有幾種值
//        List<Integer> numbers_list = new ArrayList<>();
//        //
//        Set<Integer> numbers_set = new HashSet<>();
//        //排組內該數字的最大花色
//        int[] numberMaxSuit = new int[14];
//        for (Card card : cards) {
//            int cardNum = card.getNumber();
//            numbers[cardNum]++;
//            if(numberMaxSuit[cardNum] < card.getSuit()){
//                numberMaxSuit[cardNum] = card.getSuit();
//            }
//
//            numbers_set.add(cardNum);
//        }
//        numbers_list = new ArrayList<>(numbers_set);
//        for (Integer integer : numbers_list) {
//            if(LOG)System.out.println("value:" + integer);
//        }
//        if(numbers_set.size() == 2){
//            //可能是葫蘆或是鐵支
//            Integer cardNumber_0 = numbers_list.get(0);
//            Integer cardNumber_1 = numbers_list.get(1);
//            int number_0_count = numbers[cardNumber_0];
////            int number_1_count = numbers[cardNumber_1];
//
//            switch (number_0_count){
//                case 1:
//                    //
//                    level = new Level(1,numberMaxSuit[cardNumber_1],cardNumber_1);
//                    break;
//                case 2:
//                    //
//                    level = new Level(2,numberMaxSuit[cardNumber_1],cardNumber_1);
//                    break;
//                case 3:
//                    level = new Level(2,numberMaxSuit[cardNumber_0],cardNumber_0);
//                    break;
//                case 4:
//                    level = new Level(2,numberMaxSuit[cardNumber_0],cardNumber_0);
//                    break;
//            }
//        }else if(numbers_set.size() == 3){
//            //找出哪個數字字是對子
//            List<Integer> pair = new ArrayList<>();
//            //兩個對子
//            for (Integer integer : numbers_list) {
//                if(numbers[integer] == 2)pair.add(integer);
//            }
//            //如:5 pair 7 pair
//            int pair_0 = pair.get(0);
//            int pair_1 = pair.get(1);
//            if(getScore( pair_0)> getScore( pair_1)){
//                level = new Level(5,numberMaxSuit[pair_0],pair_0);
//            }else {
//                level = new Level(5,numberMaxSuit[pair_1],pair_1);
//            }
//        }else if(numbers_set.size() == 4){
//            //單個對子
//            for (Integer integer : numbers_list) {
//                if(numbers[integer] == 2){
//                    level = new Level(5,numberMaxSuit[integer],integer);
//                }
//            }
//        }
//        return level;
//    }

}
@Data
@NoArgsConstructor
@AllArgsConstructor
class Level{
    private int kind;
    private int suit;
    private int number;
}
class Deck {
    private Queue<Card> cards;
    public Deck(){
        List<Card> cards = new ArrayList<>();
        for(int i = 1 ; i <= 4;i++){
            for(int x = 1 ; x <= 13;x++ ){
                cards.add(new Card(i*x,i,x));
            }
        }

        for (int i = 0; i < cards.size(); i++) {
            Double random = Math.random() * 52;
            Integer intValue = random.intValue();
            Card card_1 = cards.get(i);
            Card card_2 = cards.get(intValue);
            Card temp = null;
            temp = card_1;
            card_1 = card_2;
            card_2 = temp;
            cards.set(i,card_1);
            cards.set(intValue,card_2);
        }
        this.cards = new LinkedList<>(cards);
    }
    public List<Card> take(int count){
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Card poll = this.cards.poll();
            if(poll == null){
                throw new RuntimeException("沒牌了");
            }
            cards.add(poll);
        }
        return cards;
    }
}
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
class Card {
    private int id;
    private int suit;
    private int number;
}
