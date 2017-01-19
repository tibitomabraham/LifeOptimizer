
package tu_chemnitz.tibitometit.optimizer.chart_components;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.GregorianCalendar;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import tu_chemnitz.tibitometit.optimizer.DBManager;
import tu_chemnitz.tibitometit.optimizer.DayTaskNumQuadType;
import tu_chemnitz.tibitometit.optimizer.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class XYMarkerView extends MarkerView {

    private TextView tvContent;
    private IAxisValueFormatter xAxisValueFormatter;
    public DBManager dbManager;
    private DecimalFormat format;

    public XYMarkerView(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.custom_marker_view);

        this.xAxisValueFormatter = xAxisValueFormatter;
        tvContent = (TextView) findViewById(R.id.tvContent);
        format = new DecimalFormat("###.0");
        dbManager = new DBManager(this.getContext(),null,null,1);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        //GregorianCalendar  cal = new GregorianCalendar(2017, 1, 1);
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2017,0,1);
        cal.add(Calendar.DATE,(int)e.getX());
        Date date = cal.getTime();
        SimpleDateFormat  datetimeFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            DayTaskNumQuadType dayTaskNumQuadType = dbManager.getCompletedITaskOnDates(dateFormat.format(datetimeFormat.parse(date.toString())));
            int percentIUTasks,percentITasks,percentUTasks,percentNTasks;
            if(dayTaskNumQuadType!=null) {
                tvContent.setText("x: " + xAxisValueFormatter.getFormattedValue(e.getX(), null) + ", y: " + format.format(e.getY()) + " Q1:" + (dayTaskNumQuadType.getCompIUTasks() * 100 / (0 == dayTaskNumQuadType.getIuTasks() ? 1 : dayTaskNumQuadType.getIuTasks()))
                + "Q2: " + (dayTaskNumQuadType.getCompITasks() * 100 / (0 == dayTaskNumQuadType.getiTasks() ? 1 : dayTaskNumQuadType.getiTasks()))
                + "Q3: " + (dayTaskNumQuadType.getCompUTasks() * 100 / (0 == dayTaskNumQuadType.getuTasks() ? 1 : dayTaskNumQuadType.getuTasks()))
                + "Q4: " + (dayTaskNumQuadType.getCompNTasks() * 100 / (0 == dayTaskNumQuadType.getnTasks() ? 1 : dayTaskNumQuadType.getnTasks())));
}
            super.refreshContent(e, highlight);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
