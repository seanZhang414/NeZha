package cn.com.duiba.nezha.compute.common.model.activityselect;

/**
 * Created by jiali on 2017/9/4.
 */
public class WilsonInterval {

    public static WilsonPair wilsonCalc(long numerator, long denominator){
        if(denominator == 0)
            return new WilsonPair(0, 0);
        double z = 1.6;         //置信度95%
        double phat = (double)(numerator)/denominator;
        double denorm = 1. + (z*z/denominator);
        double enum1 = phat + z*z/(2*denominator);
        double enum2 = z*Math.sqrt(phat*(1-phat)/denominator + z*z/(4*denominator*denominator));
        return new WilsonPair((enum1-enum2)/denorm, (enum1+enum2)/denorm);
    }

    public static WilsonPair wilsonCalc97(long numerator, long denominator){
        if(denominator == 0)
            return new WilsonPair(0, 0);
        double z = 2.2;         //置信度97%
        double phat = (double)(numerator)/denominator;
        double denorm = 1. + (z*z/denominator);
        double enum1 = phat + z*z/(2*denominator);
        double enum2 = z*Math.sqrt(phat*(1-phat)/denominator + z*z/(4*denominator*denominator));
        return new WilsonPair((enum1-enum2)/denorm, (enum1+enum2)/denorm);
    }

    public static void main(String[] args){

        double slotScore = wilsonCalc(0, 0).lowerBound;
        double appScore = wilsonCalc(5000/ 3, 40000*3).lowerBound;
        double globalScore = wilsonCalc(10000/3, 8000*3).lowerBound;

        double coef = 0, matchscore = 0;

        double sconfidence = 1;
        double aconfidence = Math.min(3 / 500, 1);

        matchscore = sconfidence * slotScore + (1 - sconfidence) * aconfidence * appScore * 0.9 + (1 - (1-sconfidence) * aconfidence) * globalScore * 0.8;

        System.out.print(slotScore);
    }
}