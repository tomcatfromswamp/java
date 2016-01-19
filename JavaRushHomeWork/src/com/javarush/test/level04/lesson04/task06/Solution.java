package com.javarush.test.level04.lesson04.task06;

/* День недели
Ввести с клавиатуры номер дня недели, в зависимости от номера вывести название «понедельник», «вторник», «среда», «четверг», «пятница», «суббота», «воскресенье»,
если введен номер больше или меньше 7 – вывести «такого дня недели не существует».
Пример для номера 5:
пятница
Пример для номера 10:
такого дня недели не существует
*/

import java.io.*;

public class Solution
{
    public static void main(String[] args) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        int x = Integer.parseInt(line);
        System.out.println(dayOfWeek(x));
    }

    public static String dayOfWeek(int a)
    {
        String answer = "";

        if (a < 1 | a > 7)
        {
            answer = "такого дня недели не существует";
        }
        if(a==1){
            answer = "понедельник";
        }
        if(a==2){
            answer = "вторник";
        }
        if(a==3){
            answer = "среда";
        }
        if(a==4){
            answer = "четверг";
        }
        if(a==5){
            answer = "пятница";
        }
        if(a==6){
            answer = "суббота";
        }
        if(a==7){
            answer = "воскресенье";
        }
        return answer;
    }


}