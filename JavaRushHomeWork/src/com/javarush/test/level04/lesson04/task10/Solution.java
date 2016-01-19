package com.javarush.test.level04.lesson04.task10;

/* Три числа
Ввести с клавиатуры три целых числа. Определить, имеется ли среди них хотя бы одна пара равных между собой чисел.
Если такая пара существует, вывести на экран числа через пробел. Если все три числа равны между собой, то вывести все три.
Пример для чисел 1 2 2:
2 2
Пример для чисел 2 2 2:
2 2 2
*/

import java.io.*;

public class Solution
{
    public static void main(String[] args) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        int a = Integer.parseInt(line);
        line = reader.readLine();
        int b = Integer.parseInt(line);
        line = reader.readLine();
        int c = Integer.parseInt(line);
        System.out.println(compare(a,b,c));
    }

    public static String compare(int a, int b, int c)
    {
        String answer = "";
        if ((a == b) && (a == c))
        {
            answer = a + " " + b + " " + c;
        } else
        {
            if (a == b)
            {
                answer = a + "" + b;
            }
            if (b == c)
            {
                answer = b + " " + c;
            }
            if (a == c)
            {
                answer = a + " " + c;
            }
        }
        return answer;
    }
}