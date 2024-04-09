package com.azeroth.healthapp.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.azeroth.healthapp.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ChartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_glucose, container, false)
        drawLineChart(view)

        return view
    }

    private fun drawLineChart(view: View) {
        val lineChart: LineChart = view.findViewById(R.id.lineChart)

        lineChart.setDrawGridBackground(false);
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM;

        lineChart.setGridBackgroundColor(Color.WHITE);
        lineChart.setTouchEnabled(true);
        lineChart.setScaleEnabled(false)
        lineChart.setPinchZoom(true);
        lineChart.highlightValue(null);
        lineChart.isDoubleTapToZoomEnabled = false;

        lineChart.setPinchZoom(false)
        val lineEntries: List<Entry> = getDataSet()
        val lineDataSet = LineDataSet(lineEntries, "Work")
        lineDataSet.axisDependency = YAxis.AxisDependency.LEFT
        lineDataSet.lineWidth = 3F
        lineDataSet.setDrawValues(false)
        lineDataSet.color = Color.CYAN
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER;
        lineDataSet.setDrawCircles(false)
        lineDataSet.setDrawHighlightIndicators(true)
        lineDataSet.isHighlightEnabled = true
        lineDataSet.highLightColor = Color.CYAN
        lineDataSet.valueTextSize = 12F
        lineDataSet.valueTextColor = Color.DKGRAY
        val lineData = LineData(lineDataSet)

        lineData.setDrawValues(false)

        lineChart.axisLeft.setDrawGridLines(false)
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.xAxis.spaceMax = 6f
        val xAxis: XAxis = lineChart.xAxis

        xAxis.axisLineColor = resources.getColor(R.color.transparent)

        lineChart.description = null // Hide the description

        lineChart.axisLeft.setDrawLabels(true)
        lineChart.axisRight.setDrawLabels(true)
        lineChart.legend.isEnabled = false

        lineData.setValueTextColor(Color.parseColor("#ebecf0"))
        lineChart.xAxis.textColor = R.color.blue


        lineChart.animateY(1000)
        lineChart.data = lineData

        // Setup X Axis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.isGranularityEnabled = true
        xAxis.granularity = 1.0f
        xAxis.xOffset = 1f
        xAxis.labelCount = 25
        xAxis.axisMinimum = 0F
        xAxis.axisMaximum = 24F


        lineChart.axisLeft.setCenterAxisLabels(true)
        var xVals: MutableList<String> = arrayListOf()

        xVals.add("  Mo  ");
        xVals.add("  Tu  ");
        xVals.add("  We  ");
        xVals.add("  Th  ");
        xVals.add("  Fr  ");
        xVals.add("  Sa  ");

        xAxis.setCenterAxisLabels(false);
        xAxis.setDrawGridLines(false);
        xAxis.axisMaximum = 6f;
        xAxis.position = XAxis.XAxisPosition.BOTTOM;
        xAxis.valueFormatter = IndexAxisValueFormatter(xVals);

        lineChart.axisRight.isEnabled = false
        lineChart.invalidate()
    }

    private fun getDataSet(): List<Entry> {
        val lineEntries: MutableList<Entry> = ArrayList()
        lineEntries.add(Entry(0f, 1f))
        lineEntries.add(Entry(1f, 4f))
        lineEntries.add(Entry(2f, 3f))
        lineEntries.add(Entry(3f, 1f))
        lineEntries.add(Entry(4f, 8f))
        lineEntries.add(Entry(5f, 9f))
        lineEntries.add(Entry(6f, 11f))
        return lineEntries
    }
}