package io.github.sidvenu.atyourage

import android.app.DatePickerDialog
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.DatePicker
import android.widget.ListView
import android.widget.TextView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = ResultAdapter(this, ArrayList())
        (findViewById<ListView>(R.id.results_list_view)).adapter = adapter

        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val cal: Calendar = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            (findViewById<TextView>(R.id.born_on_text_view)).text = getFormattedDate(cal.time, "MMMM d, yyyy")
            AgeGeekParser(findViewById(R.id.root_layout), adapter).execute(getFormattedDate(cal.time, "MMMM-d-yyyy"))
        }
        dateListener.onDateSet(DatePicker(this), 2000, 8, 15)
        findViewById<TextView>(R.id.change_text_view).setOnClickListener {
            DatePickerDialog(this, dateListener, 2000, 8, 15).show()
        }

    }

    private fun getFormattedDate(date: Date, pattern: String): String {
        val newDateFormat = SimpleDateFormat("", Locale.US)
        newDateFormat.applyPattern(pattern)
        return newDateFormat.format(date)
    }

    private class AgeGeekParser(root: View, val adapter: ResultAdapter) : AsyncTask<String, Unit, ArrayList<String>>() {
        private val textViewRef: WeakReference<TextView>? = WeakReference(root.findViewById(R.id.born_on_text_view))

        override fun doInBackground(vararg params: String?): ArrayList<String> {
            val url: String = "https://www.agegeek.com/" + params[0]!!
            val doc: Document = Jsoup.connect(url).get()
            val list: ArrayList<String> = ArrayList()
            list.add(
                    doc.getElementById("top").html()
                            .substringAfterLast("<strong>")
                            .substringBeforeLast("</strong>")
            )
            val elements: Elements = doc.getElementsByClass("mobile-event")
            elements.mapTo(list) { it.html() }
            return list
        }

        override fun onPostExecute(results: ArrayList<String>) {
            textViewRef?.get()?.text = results[0]
            results.removeAt(0)
            adapter.clear()
            for (result: String in results) {
                var number_of_days = "X"
                val desc = StringBuilder()

                val text = result.substringAfter("<strong>").substringBefore("</strong>")
                if (!text.equals("Today", true)) {
                    number_of_days = text.substringAfter("In ").substringBefore(" days")
                } else desc.append("<strong>Today</strong>")

                desc.append(
                        result.substringAfter("</strong>").substringBefore("<a")
                                .replace("<span class=\"person\">", "<strong>")
                                .replace("</span>", "</strong>")
                )
                adapter.add(Result(number_of_days, Html.fromHtml(desc.toString())))
            }
        }
    }
}
