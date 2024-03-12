package simulation.data;

import java.util.ArrayList;
import java.util.List;

public class DataColumn {
    public final String name;
    public final int index;
    public final List<Double> data;

    public DataColumn(int index, int maximumTableSize, String name) {
        this.index = index;
        this.name = name;
        this.data = new ArrayList<>(maximumTableSize);
    }

    public int dataSize() {
        return data.size();
    }

    public void addNewData(double val) {
        data.add(val);
    }

    public double getData(int index) {
        return data.get(index);
    }
}
