package cn.com.duiba.nezha.compute.common.vo;

/**
 * Created by jiali on 2017/12/8.
 */
public class GenericPair<A extends Comparable, B  extends Comparable> implements Comparable {
    public A first;
    public B second;

    public GenericPair(A first, B second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public int compareTo(Object arg0) {
        GenericPair b = (GenericPair) arg0;
        return  b.second.compareTo(second);
    }

    @Override
    public String toString()
    {
        return String.valueOf(first) +"\t" + String.valueOf(second);
    }
}