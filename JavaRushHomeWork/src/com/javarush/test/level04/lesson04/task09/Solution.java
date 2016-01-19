package com.javarush.test.level04.lesson04.task09;

/* Светофор
Работа светофора для пешеходов запрограммирована следующим образом: в начале каждого часа в течение трех минут горит зеленый сигнал,
затем в течение одной минуты — желтый, а потом в течение одной минуты — красный, затем опять зеленый горит три минуты и т. д.
Ввести с клавиатуры вещественное число t, означающее время в минутах, прошедшее с начала очередного часа.
Определить, сигнал какого цвета горит для пешеходов в этот момент.
Результат вывести на экран в следующем виде:
"зеленый" - если горит зеленый цвет, "желтый" - если горит желтый цвет, "красный" - если горит красный цвет.
Пример для числа 2.5:
зеленый
Пример для числа 3:
желтый
Пример для числа 4:
красный
Пример для числа 5:
зеленый
*/

import java.io.*;

public class Solution
{
    public static void main(String[] args) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        double x = Double.parseDouble(line);
        System.out.println(whatsColor(x));
    }

    public static String whatsColor(double a)
    {
        String answer = "";
        //System.out.println("Кратность 5м " + a%5);
        if(a>=0 && a<=60)
        {
            if (a % 5 >= 4.0 && a % 5 < 5.0)
            {
                answer = "красный";
            }
            if (a % 5 >= 3.0 && a % 5 < 4.0)
            {
                answer = "желтый";
            }
            if (a % 5 >= 0.0 && a % 5 < 3.0)
            {
                answer = "зеленый";
            }
        }
        return answer;
    }
}