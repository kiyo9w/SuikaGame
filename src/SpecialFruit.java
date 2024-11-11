import java.util.List;
import java.util.Set;

public abstract class SpecialFruit extends Fruit {

    public SpecialFruit(double x, double y, int type) {
        super(x, y, type);
    }

    // Abstract method to be implemented by all special fruits
    public abstract void postUpdate(List<Fruit> allFruits, Set<Fruit> fruitsToRemove);

    @Override
    public boolean canMergeWith(Fruit other) {
        // Special fruits typically can't merge with each other or other types
        return false;
    }
}
