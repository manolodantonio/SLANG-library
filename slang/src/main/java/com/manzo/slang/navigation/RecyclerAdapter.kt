package com.manzo.slang.navigation

import android.content.Context
import android.support.annotation.LayoutRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class RecyclerAdapter<T>(dataset: MutableList<T> = mutableListOf()) :
    RecyclerView.Adapter<RecyclerAdapter<T>.ViewHolder>() {
    companion object {
        protected const val CONTENT_VIEW = 1
        protected const val EMPTY_VIEW = 2
    }

    var dataset = dataset
        set(value) {
            field.apply {
                    clear()
                    addAll(value)
                }
            notifyDataSetChanged()
        }


    fun removeItem(item: T) {
        dataset.apply {
            indexOfFirst { element -> element == item }
                .let { index ->
                    if (index != -1) {
                        removeAt(index)
                        notifyItemRemoved(index)
                    } else return@apply
                }
        }
    }

    fun addItem(item: T) {
        dataset.apply {
            add(item)
            notifyItemInserted(lastIndex)
        }
    }


    @get:LayoutRes
    protected abstract val rowLayout: Int

    protected abstract fun onBindContentView(holder: ViewHolder, position: Int, elementData: T)

    @LayoutRes
    protected open val emptyLayout: Int = 0

    protected open fun onBindEmptyView(holder: ViewHolder, position: Int) {}

    protected open fun onItemLayoutClick(viewHolder: ViewHolder, view: View, position: Int, elementData: T) {}

    override fun getItemCount(): Int {
        return if (dataset.isEmpty() && emptyLayout != 0) 1 else dataset.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(getLayoutRes(viewType), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            EMPTY_VIEW -> onBindEmptyView(holder, position)
            else -> onBindContentView(holder, position, dataset[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            dataset.isNotEmpty() -> CONTENT_VIEW
            emptyLayout != 0 -> EMPTY_VIEW
            else -> CONTENT_VIEW
        }
    }

    protected open fun getLayoutRes(viewType: Int): Int {
        return when (viewType) {
            EMPTY_VIEW -> emptyLayout
            CONTENT_VIEW -> rowLayout
            else -> -1
        }
    }

    open inner class ViewHolder(itemView: View) : GenericViewHolder<View>(itemView) {
        private val adapter = this@RecyclerAdapter

        override fun onItemViewClick(v: View) {
            adapter.onItemLayoutClick(this, v, adapterPosition, dataset[adapterPosition])
        }
    }

}


abstract class GenericViewHolder<T : View>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val cache = SparseArray<T>()

    val root: View
        get() = itemView


    init {
        itemView.setOnClickListener { onItemViewClick(it) }
    }


    operator fun get(id: Int): T? {
        var result: T? = cache.get(id)
        val viewById = itemView.findViewById<View>(id)
        if (result == null && viewById != null) {
            result = viewById as T
            cache.put(id, result)
        }
        return result
    }

    fun setValue(id: Int, value: T) {
        cache.put(id, value)
    }

    fun getValue(id: Int): T {
        return cache.get(id)
    }

    protected abstract fun onItemViewClick(v: View)
}

/**
 * Adds layout manager and adapter
 * @receiver RecyclerView
 * @param context Context
 * @param myAdapter RecyclerAdapter<T>
 * @param numberOfColumns Int
 */
fun <T> RecyclerView.init(context: Context, myAdapter: RecyclerAdapter<T>, numberOfColumns: Int = 1) {
    apply {
        layoutManager =
            if (numberOfColumns <= 1) LinearLayoutManager(context)
            else GridLayoutManager(context, numberOfColumns)
        adapter = myAdapter
    }
}

/**
 * Creates simple adapter from list of objects
 * @receiver MutableList<E>
 * @param rowLayout Int
 * @param emptyLayout Int
 * @param onBindEmpty Function1<[@kotlin.ParameterName] ViewHolder<E>, Unit>?
 * @param onBindContent Function2<[@kotlin.ParameterName] ViewHolder<E>, [@kotlin.ParameterName] Int, [@kotlin.ParameterName] E, Unit>
 * @return RecyclerAdapter<E>
 */
fun <E> MutableList<E>.toAdapter(
    rowLayout: Int,
    emptyLayout: Int = 0,
    onBindEmpty: ((emptyHolder: RecyclerAdapter<E>.ViewHolder) -> Unit)? = null,
    onBindContent: (holder: RecyclerAdapter<E>.ViewHolder, position: Int, elementData: E) -> Unit
) = object : RecyclerAdapter<E>(this@toAdapter) {
    override val rowLayout = rowLayout
    override val emptyLayout = emptyLayout

    override fun onBindContentView(holder: ViewHolder, position: Int, elementData: E) {
        onBindContent.invoke(holder, position, elementData)
    }

    override fun onBindEmptyView(holder: ViewHolder, position: Int) {
        onBindEmpty?.invoke(holder)
    }
}