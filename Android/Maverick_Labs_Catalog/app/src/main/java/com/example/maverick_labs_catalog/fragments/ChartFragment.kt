package com.example.maverick_labs_catalog.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.enums.AvailabilityPeriod
import com.anychart.enums.TimeTrackingMode
import com.anychart.scales.calendar.Availability
import com.example.maverick_labs_catalog.R
import com.example.maverick_labs_catalog.models.Projects
import com.example.maverick_labs_catalog.services.ApiService
import com.example.maverick_labs_catalog.services.ServiceBuilder
import com.example.maverick_labs_catalog.storage.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChartFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_chart, container, false)

        val resource = AnyChart.resource()

        val data: MutableList<DataEntry> = ArrayList()

        resource.zoomLevel(1.0)
                .timeTrackingMode(TimeTrackingMode.ACTIVITY_PER_CHART)
                .currentStartDate("2016-09-30")

        resource.resourceListWidth(120)

        resource.calendar().availabilities(arrayOf(
                Availability(AvailabilityPeriod.DAY, null as Double?, 10.0, true, null as Double?, null as Double?, 18.0),
                Availability(AvailabilityPeriod.DAY, null as Double?, 14.0, false, null as Double?, null as Double?, 15.0),
                Availability(AvailabilityPeriod.WEEK, null as Double?, null as Double?, false, 5.0, null as Double?, 18.0),
                Availability(AvailabilityPeriod.WEEK, null as Double?, null as Double?, false, 6.0, null as Double?, 18.0)
        ))

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val sessionmanager = this.context?.let { SessionManager(it.applicationContext) }
        val apiService = ServiceBuilder.buildService(ApiService::class.java)
        val requestCall = apiService.getProjectList(token = "Token ${sessionmanager?.fetchAuthToken()}")
        requestCall.enqueue(object : Callback<List<Projects>> {
            override fun onResponse(call: Call<List<Projects>>, response: Response<List<Projects>>) {
                val info = response.body()
                Log.d("projlist", "Info:${info}")
                for (i in info!!){

                            data.add(ResourceDataEntry(
                i.name,
                i.description, arrayOf(
                Activity(
                        i.name, arrayOf(
                        Interval(sdf.format(i.start_date), sdf.format(i.completion_date))
                ))
        )))


//                    Log.d("chart","Inside loop:,START:${sdf.format(i.start_date)},COMP:${i.completion_date},DATA:${data},INFO:${info},BODY:${response.body()}")
            }
                Log.d("chart","DATA:${data}")
            }

            override fun onFailure(call: Call<List<Projects>>, t: Throwable) {
                Log.d("projlist", "Error!!${t}")
            }

        })






//        data.add(ResourceDataEntry(
//                "Romario",
//                "Developer",
//                "http://cdn.anychart.com/images/resource-chart/developer-romario.png", arrayOf(
//                Activity(
//                        "Gantt timeline", arrayOf(
//                        Interval("2016-10-01", "2016-10-11", 120)
//                ),
//                        "#62BEC1"),
//                Activity(
//                        "Gantt Connectors events/removal + UI customization", arrayOf(
//                        Interval("2016-10-01", "2016-10-04", 180)
//                ),
//                        "#62BEC1"),
//                Activity(
//                        "Chart Facebook sharing", arrayOf(
//                        Interval("2016-10-01", "2016-10-04", 120)
//                ),
//                        "#62BEC1"),
//                Activity(
//                        "Chart animation problems", arrayOf(
//                        Interval("2016-10-05", "2016-10-09", 300)
//                ),
//                        "#62BEC1"),
//                Activity(
//                        "iPad touch problems", arrayOf(
//                        Interval("2016-10-12", "2016-10-16", 300),
//                        Interval("2016-10-17", "2016-10-21", 60)
//                ),
//                        "#62BEC1"),
//                Activity(
//                        "Some improvements for chart labels", arrayOf(
//                        Interval("2016-10-17", "2016-10-22", 240),
//                        Interval("2016-10-22", "2016-10-26", 240)
//                ),
//                        "#62BEC1")
//        )))
//        data.add(ResourceDataEntry(
//                "Antonio",
//                "Developer", arrayOf(
//                Activity(
//                        "Gantt resource list", arrayOf(
//                        Interval("2016-09-25", "2016-10-01")
//                )),
//                Activity(
//                        "Pareto Chart", arrayOf(
//                        Interval("2016-09-25", "2016-10-05")
//                )),
//                Activity(
//                        "Chart bug fixes", arrayOf(
//                        Interval("2016-10-08", "2016-10-25")
//                )),
//                Activity(
//                        "Chart legend", arrayOf(
//                        Interval("2016-10-06", "2016-10-12")
//                ))
//        )))
//        data.add(ResourceDataEntry(
//                "Alejandro",
//                "Developer",
//                "http://cdn.anychart.com/images/resource-chart/developer-alejandro.png", arrayOf(
//                Activity(
//                        "Pie chart improvement", arrayOf(
//                        Interval("2016-09-25", "2016-10-02", 120)
//                ),
//                        "#8789C0"),
//                Activity(
//                        "Pie chart labels problems", arrayOf(
//                        Interval("2016-10-05", "2016-11-01", 120)
//                ),
//                        "#8789C0"),
//                Activity(
//                        "Stock chart minor bugs", arrayOf(
//                        Interval("2016-10-01", "2016-10-10", 120)
//                ),
//                        "#8789C0"),
//                Activity(
//                        "Chart minor bug fixes", arrayOf(
//                        Interval("2016-10-20", "2016-11-05", 120)
//                ),
//                        "#8789C0")
//        )))
//        data.add(ResourceDataEntry(
//                "Sergio",
//                "Developer",
//                "http://cdn.anychart.com/images/resource-chart/developer-sergio.png", arrayOf(
//                Activity(
//                        "Gantt logo", arrayOf(
//                        Interval("2016-09-30", "2016-10-03", 300)
//                ),
//                        "#E06D06"),
//                Activity(
//                        "Tooltip bug fix", arrayOf(
//                        Interval("2016-10-04", "2016-10-10", 300)
//                ),
//                        "#E06D06"),
//                Activity(
//                        "Chart label", arrayOf(
//                        Interval("2016-10-11", "2016-10-15", 300)
//                ),
//                        "#E06D06"),
//                Activity(
//                        "Map series labels improvement", arrayOf(
//                        Interval("2016-10-16", "2016-11-03", 300)
//                ),
//                        "#E06D06")
//        )))

        resource.data(data)

        val anyChartView = view.findViewById(R.id.any_chart_view) as AnyChartView
        anyChartView.setChart(resource)


        return view
    }

    private class ResourceDataEntry internal constructor(name: String?, description: String?, activities: Array<Activity>) : DataEntry() {
        init {
            setValue("name", name)
            setValue("description", description)
            setValue("activities", activities)
        }
    }

    private class Activity internal constructor(name: String?, intervals: Array<Interval>) : DataEntry() {
        init {
            setValue("name", name)
            setValue("intervals", intervals)
        }
    }

    private class Interval internal constructor(start: String?, end: String?) : DataEntry() {
        init {
            setValue("start", start)
            setValue("end", end)
        }
    }
}


