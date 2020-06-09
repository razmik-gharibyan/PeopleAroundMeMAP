package com.studio29.gharibyan.peoplearoundmemap.ui.profile

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.studio29.gharibyan.peoplearoundmemap.R

class AccountItemAdapter(private val context: Context, private val usernameArr: ArrayList<String>
                         , private val picArr: ArrayList<Bitmap>): BaseAdapter() {

    private val inflater: LayoutInflater

    init {
        this.inflater = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view: View?
        val viewholder: AccountItemViewHolder
        if(convertView == null) {
            view = this.inflater.inflate(R.layout.list_menu_item,parent,false)
            viewholder = AccountItemViewHolder(view)
            view.tag = viewholder
        }else{
            view = convertView
            viewholder = view.tag as AccountItemViewHolder
        }

        viewholder.iconView.setImageBitmap(picArr[position])
        viewholder.usernameView.text = usernameArr[position]
        viewholder.disconnectButton.setOnClickListener {
            //TODO("Remove document from firebase")
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return usernameArr[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return usernameArr.size
    }
}

class AccountItemViewHolder(view: View?) {
    val iconView: ImageView
    val usernameView: TextView
    val disconnectButton: Button

    init {
        this.iconView = view!!.findViewById(R.id.menu_item_icon)
        this.usernameView = view.findViewById(R.id.menu_item_title)
        this.disconnectButton = view.findViewById(R.id.disconnect_button)
    }
}