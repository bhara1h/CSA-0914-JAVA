package parking.app;

import java.util.Arrays;

public class Task03Demo {
    private final boolean[][] occupied = new boolean[2][];

    public Task03Demo() {
        occupied[0] = new boolean[2];
        occupied[1] = new boolean[1];
    }

    private int[] allocate() {
        for (int floor = 0; floor < occupied.length; floor++) {
            for (int slot = 0; slot < occupied[floor].length; slot++) {
                if (!occupied[floor][slot]) {
                    occupied[floor][slot] = true;
                    return new int[] {floor, slot};
                }
            }
        }
        return null;
    }

    private void release(int[] position) {
        occupied[position[0]][position[1]] = false;
    }

    public static void main(String[] args) {
        Task03Demo facility = new Task03Demo();
        int[] first = facility.allocate();
        int[] second = facility.allocate();
        int[] third = facility.allocate();
        int[] none = facility.allocate();

        System.out.println(Arrays.toString(first));
        System.out.println(Arrays.toString(second));
        System.out.println(Arrays.toString(third));
        System.out.println(none);
        facility.release(second);
        facility.release(third);
        System.out.println(facility.occupied[0][0] + " "
                + facility.occupied[0][1] + " " + facility.occupied[1][0]);
        System.out.println(Arrays.toString(facility.allocate()));
    }
}
