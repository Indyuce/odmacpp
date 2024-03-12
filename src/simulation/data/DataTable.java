package simulation.data;

import simulation.Simulation;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataTable {
    private int columnCounter = 0;
    private final int maximumTableSize;
    @Deprecated
    public final List<DataColumn> data = new ArrayList<>();

    public DataTable(Simulation simulation) {
        this.maximumTableSize = simulation.tEnd - simulation.tStart;
    }

    public DataColumn newColumn(String id) {
        final DataColumn col = new DataColumn(columnCounter++, maximumTableSize, id);
        data.add(col);
        return col;
    }

    @Deprecated
    public DataColumn byIndex(int col) {
        return data.get(col);
    }

    private String fetchColumn(int index) {
        StringBuilder b = new StringBuilder();
        boolean empty = true;
        for (DataColumn col : data) {
            if (empty) empty = false;
            else b.append(';');

            if (index == -1) b.append(col.name);
            else b.append(col.getData(index));
        }

        return b.toString();
    }

    public void export(File targetFile) {
        try {
            final PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8));
            // pw.println(title) ;
            // pw.println("0;1;2;3") ;
            final int dataRowCount = data.stream().findAny().get().dataSize();

            // TODO add TIME (days) // ------------- // ------------------ // -------------
            // TODO pw.println("Time (days);Energy Profile (W/m2);Battery Level (J);Throughput (Packets/s)");

            for (int i = -1; i < dataRowCount; i++)
                pw.println(fetchColumn(i));

            pw.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }
}
