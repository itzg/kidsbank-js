package me.itzg.kidsbank.types;

/**
 * @author Geoff Bourne
 * @since Sep 2017
 */
public class ResponseValue<T> {

    private T value;

    public ResponseValue() {
    }

    public ResponseValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public static <T> ResponseValue<T> of(T value) {
        return new ResponseValue<>(value);
    }
}
