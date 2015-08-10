package plop.graph;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 *
 */
public class GraphDataControl
{
    private GraphView graph;
    private LineGraphSeries<DataPoint> dataSeries;

    public GraphDataControl(GraphView graph)
    {
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        this.graph = graph;
        dataSeries = new LineGraphSeries<DataPoint>();
        this.graph.addSeries(dataSeries);
        dataSeries.setTitle("Voltage");
    }

    public synchronized void addDataPoint(double x, double y, boolean scrollToEnd)
    {
        dataSeries.appendData(new DataPoint(x, y), scrollToEnd, 500);
    }

    public synchronized void clearGraph()
    {
        graph.getViewport().setScalable(false);
        graph.getViewport().setScrollable(false);
        this.graph.getViewport().setMinX(0);
        this.graph.getViewport().setMaxX(200);
        this.graph.computeScroll();
        dataSeries.resetData(new DataPoint[0]);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
    }
}
