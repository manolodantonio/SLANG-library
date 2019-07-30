package com.manzo.slang.extensions

import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView

/**
 * Created by Manolo D'Antonio on 19/07/2019
 */


// SPINNER

/**
 * List constructor for setSimpleAdapter
 */
fun Spinner.setSimpleAdapter(
    list: List<String>,
    textSize: Float = 22f,
    positionSelectedListener: (selectedPosition: Int, selectedValue: String) -> Unit
) = setSimpleAdapter(list.toTypedArray(), textSize, positionSelectedListener)


/**
 * Create a simple adapter and load data into the provided Spinner
 */
fun Spinner.setSimpleAdapter(
    array: Array<String>,
    textSize: Float = 22f,
    positionSelectedListener: (selectedPosition: Int, selectedValue: String) -> Unit
): ArrayAdapter<String> {
    if (array.isEmpty()) Log.e("setSimpleAdapter", "dataset is empty")

    val arrayAdapter = object : ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, array) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            view.findViewById<TextView>(android.R.id.text1).apply {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
            }
//            textView.setTextColor(context.color(android.R.color.transparent))
            return view
        }

//        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
//            return super.getDropDownView(position, convertView, parent)
//        }
    }

    adapter = arrayAdapter
    setSelection(0)

    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {
            // not used
        }

        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            itemPosition: Int,
            id: Long
        ) {
            positionSelectedListener.invoke(itemPosition, array[itemPosition])
        }
    }

    return arrayAdapter
}