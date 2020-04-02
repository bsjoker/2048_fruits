package ru.skillsoft.a20fruits48

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import kotlin.math.log2

class ImageAdapter internal constructor(context: Context, private val resource: Int, private val itemList: ArrayList<Int>?) : ArrayAdapter<ImageAdapter.ItemHolder>(context, resource) {
    val listOfPic = arrayListOf(
        R.drawable.iempty_green,
        R.drawable.i2_green,
        R.drawable.i4_green,
        R.drawable.i8_green,
        R.drawable.i16_green,
        R.drawable.i32_green,
        R.drawable.i64_green,
        R.drawable.i128_green,
        R.drawable.i256_green,
        R.drawable.i512_green,
        R.drawable.i1024_green,
        R.drawable.i2048_green
    )
    override fun getCount(): Int {
        return if (this.itemList != null) this.itemList.size else 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder: ItemHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null)
            holder = ItemHolder()
            holder.icon = convertView!!.findViewById(R.id.pic)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ItemHolder
        }

        var pos = if (itemList!![position] == 0) 1 else itemList!![position]
        holder.icon!!.setImageResource(this.listOfPic!![log2(pos.toFloat()).toInt()])

        return convertView
    }

    class ItemHolder {
        var icon: ImageView? = null
    }
}
