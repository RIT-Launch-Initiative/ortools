package OpenRocketExtensions;

import net.sf.openrocket.util.ArrayList;

class Common {

    public static void mustBeSorted(double[] numbers) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i_num = 0; i_num < numbers.length; i_num++) {
            if (numbers[i_num] <= max) {
                throw new IllegalArgumentException("Interpolation domain must" +
                        " be sorted");
            }
            max = numbers[i_num];
        }
    }

    public static void mustBePositive(double[] numbers) {
        for (int i_num = 0; i_num < numbers.length; i_num++) {
            if (numbers[i_num] <= 0.0) {
                throw new IllegalArgumentException("Model inputs must be " +
                        "strictly positive.");
            }
        }
    }

    public static void mustBeFinite(double[] numbers) {
        for (int i_num = 0; i_num < numbers.length; i_num++) {
            if (!Double.isFinite(numbers[i_num])) {
                throw new IllegalArgumentException("Model inputs must be finite.");
            }
        }
    }

    public static void mustBeNonNegative(double[] numbers) {
        for (int i_num = 0; i_num < numbers.length; i_num++) {
            if (numbers[i_num] < 0.0) {
                throw new IllegalArgumentException("Model inputs must be " +
                        "non-negative.");
            }
        }
    }

    public static ArrayList<Double> asArrayList(double[] numbers) {
        ArrayList<Double> list = new ArrayList<Double>(numbers.length);
        for (int i_num = 0; i_num < numbers.length; i_num++) {
            list.add(numbers[i_num]);
        }
        return list;
    }
}
