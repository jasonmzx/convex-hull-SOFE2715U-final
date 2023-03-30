import java.util.Comparator;

public class ConvexComparator implements Comparator<ConvexPoint> {
    @Override
    public int compare(ConvexPoint o1, ConvexPoint o2) {
        return o1.angle.compareTo(o2.angle);
    }
}

