package cn.com.duiba.nezha.engine.common.utils;

import java.util.Optional;

/**
 * @author ZhouFeng zhoufeng@duiba.com.cn
 * @version $Id: SpecialPair.java , v 0.1 2017/8/28 下午3:13 ZhouFeng Exp $
 */
public class Pair<T> {

    private T left;
    private T right;

    public Pair(T left, T right) {
        this.left = left;
        this.right = right;
    }

    public static <T> Pair<T> of(T left, T right) {
        return new Pair<>(left, right);
    }

    public Optional<T> getLeft() {
        return Optional.ofNullable(left);
    }

    public void setLeft(T left) {
        this.left = left;
    }

    public Optional<T> getRight() {
        return Optional.ofNullable(right);
    }

    public void setRight(T right) {
        this.right = right;
    }
}
