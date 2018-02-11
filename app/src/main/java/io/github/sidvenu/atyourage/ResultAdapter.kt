package io.github.sidvenu.atyourage

import android.content.Context
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class Result(val number_of_days: String, val desc: Spanned)

class ResultAdapter(context: Context, results: ArrayList<Result>) : ArrayAdapter<Result>(context, R.layout.result_item, results) {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view: View? = convertView
        if (view == null)
            view = inflater.inflate(R.layout.result_item, parent, false)
        view?.findViewById<TextView>(R.id.number_of_days_text_view)?.text = getItem(position).number_of_days
        view?.findViewById<TextView>(R.id.desc_text_view)?.text = getItem(position).desc
        return view
    }

}
