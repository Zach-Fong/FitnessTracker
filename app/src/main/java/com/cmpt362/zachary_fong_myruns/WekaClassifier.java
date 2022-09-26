package com.cmpt362.zachary_fong_myruns;


class WekaClassifier {

    public static double classify(Object[] i)
            throws Exception {

        double p = Double.NaN;
        p = WekaClassifier.N6bbd0b4510(i);
        return p;
    }
    static double N6bbd0b4510(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 644.245443) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 644.245443) {
            p = WekaClassifier.N4644234b11(i);
        }
        return p;
    }
    static double N4644234b11(Object []i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() <= 1200.005319) {
            p = 1;
        } else if (((Double) i[0]).doubleValue() > 1200.005319) {
            p = 2;
        }
        return p;
    }
}


