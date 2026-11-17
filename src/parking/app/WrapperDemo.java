package parking.app;

public class WrapperDemo {
    public static void main(String[] args) {
        int primitiveCount = 5;
        Integer boxedCount = primitiveCount;
        int unboxedCount = boxedCount;

        System.out.println("Primitive int: " + primitiveCount);
        System.out.println("Wrapper Integer: " + boxedCount);
        System.out.println("Unboxed int: " + unboxedCount);
        System.out.println("ArrayList-compatible value: " + boxedCount);
    }
}
