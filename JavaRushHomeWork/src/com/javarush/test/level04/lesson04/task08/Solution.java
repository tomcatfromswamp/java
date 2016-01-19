package com.javarush.test.level04.lesson04.task08;

/* Треугольник
Ввести с клавиатуры три числа а, b, c – стороны предполагаемого треугольника.
Определить возможность существования треугольника по сторонам. Результат вывести на экран в следующем виде:
"Треугольник существует." - если треугольник с такими сторонами существует.
"Треугольник не существует." - если треугольник с такими сторонами не существует.
Подсказка: Треугольник существует только тогда, когда сумма любых двух его сторон больше третьей.
Требуется сравнить каждую сторону с суммой двух других.
Если хотя бы в одном случае сторона окажется больше суммы двух других, то треугольника с такими сторонами не существует.
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
        System.out.println(itsTriangle(a,b,c));
    }

    public static String itsTriangle(int x, int y, int z)
    {
        String answer = "";
        if(x<0 | y<0 | z<0){
            answer="Треугольник не существует.";
        } else
        {
            if ((y + z) > x && (x + z) > y && (x + y) > z)
            {
                answer = "Треугольник существует.";
            } else
            {
                answer = "Треугольник не существует.";
            }
        }
        return answer;
    }
}