package com.studio29.gharibyan.peoplearoundmemap.ui.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.studio29.gharibyan.peoplearoundmemap.R

class MenuItemAdapter(private val context: Context, private val titleArr: Array<String>
                      , private val iconArr: Array<Int>): BaseAdapter() {

    private val inflater: LayoutInflater

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View?
        val viewholder: ItemViewHolder
        if(convertView == null) {
            view = this.inflater.inflate(R.layout.list_menu_item,parent,false)
            viewholder = ItemViewHolder(view)
            view.tag = viewholder
        }else{
            view = convertView
            viewholder = view.tag as ItemViewHolder
        }

        viewholder.iconView.setBackgroundResource(iconArr[position])
        viewholder.titleView.text = titleArr[position]
        return view
    }

    override fun getItem(position: Int): Any {
        return titleArr[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return titleArr.size
    }
}

class ItemViewHolder(view: View?) {
    val iconView: View
    val titleView: TextView

    init {
        this.iconView = view!!.findViewById(R.id.menu_item_icon)
        this.titleView = view!!.findViewById(R.id.menu_item_title)
    }
}