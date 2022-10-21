package com.lnd.RencontreAfricaine.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.lnd.RencontreAfricaine.R

class SpinnerAdapter(private val context:Context, private val listFilter:MutableList<String>) : BaseAdapter(){
    override fun getCount(): Int = listFilter.size

    override fun getItem(p0: Int): Any = p0

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun getView(position: Int, p1: View?, p2: ViewGroup?): View {
        val rootView = LayoutInflater.from(context).inflate(R.layout.itemspinner, p2, false)

        val img = rootView.findViewById<ImageView>(R.id.imgItemSpinner)
        val txt = rootView.findViewById<TextView>(R.id.txtItemSpinner)
        txt.text = listFilter[position]
        when(listFilter[position]){
            //Sex
            "Homme / Femme"->img.setImageResource(R.drawable.iconsmenwomen)
            "Homme"->img.setImageResource(R.drawable.iconsmen)
            "Femme"->img.setImageResource(R.drawable.iconswomen)
            //Relation
            "Toute relation"->img.setImageResource(R.drawable.allrelation)
            "Marriage"->img.setImageResource(R.drawable.iconsring)
            "Relation Serieuse"->img.setImageResource(R.drawable.iconsflower)
            "Amour"->img.setImageResource(R.drawable.iconsheart)
            "Amitie"->img.setImageResource(R.drawable.iconsfriend)
            "Relation Sexuel"->img.setImageResource(R.drawable.iconssex)
            "Inconnue"->img.setImageResource(R.drawable.iconsunknown)

            //Drapeau des pays
            "Monde / Partout"->img.setImageResource(R.drawable.iconsworld)
            "Pays"->img.setImageResource(R.drawable.iconsworld)
            "Mali"->img.setImageResource(R.drawable.iconsmali)
            "Algerie"->img.setImageResource(R.drawable.iconsalgerie)
            "Senegal"->img.setImageResource(R.drawable.iconssenegale)
            "Burkina Faso"->img.setImageResource(R.drawable.iconsburkiafaso)
            "Niger"->img.setImageResource(R.drawable.iconsniger)
            "Nigeria"->img.setImageResource(R.drawable.iconsnigeria)
            "Togo"->img.setImageResource(R.drawable.iconstogo)
            "Benin"->img.setImageResource(R.drawable.iconsbenin)
            "Cape Vert"->img.setImageResource(R.drawable.iconscapevert)
            "Gambie"->img.setImageResource(R.drawable.iconsgambie)
            "Ghana"->img.setImageResource(R.drawable.iconsghana)
            "Guinee Bissau"->img.setImageResource(R.drawable.iconsguinebissau)


        }


        return rootView
    }

}