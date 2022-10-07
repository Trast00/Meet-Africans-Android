package com.lnd.RencontreAfricaine.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.lnd.RencontreAfricaine.R
import java.util.Objects


class DiscoverFragment : Fragment() {


    private lateinit var spinSex: Spinner
    private lateinit var spinLocalisation: Spinner
    private lateinit var spinRelation: Spinner
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_discover, container, false)

        spinSex = root.findViewById(R.id.spinnerSex)
        spinSex.adapter = FilterAdapter(mutableListOf("Homme / Femme", "Homme", "Femme"))

        spinLocalisation = root.findViewById(R.id.spinnerLocalisation)
        spinLocalisation.adapter = FilterAdapter(mutableListOf("Monde / Partout"))

        spinRelation = root.findViewById(R.id.spinnerRelation)
        spinRelation.adapter = FilterAdapter(mutableListOf("Toute Relations", "Marriage",
            "Relation Serieuse", "Amour", "Amitie", "Relation Sexuel", "Inconnue"))

        spinSex.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val filter = p1?.findViewById<TextView>(R.id.txtItemFilter)?.text.toString()
                if(filter!="null"){
                    filterSex = if (filter!="Homme / Femme"){
                        filter
                    } else{
                        ""
                    }
                    filterChanged()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        spinLocalisation.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val filter = p1?.findViewById<TextView>(R.id.txtItemFilter)?.text.toString()
                if(filter!="null"){
                    filterLocalisation = if (filter!="Monde / Partout"){
                        filter
                    } else{
                        ""
                    }
                    filterChanged()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        spinRelation.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val filter = p1?.findViewById<TextView>(R.id.txtItemFilter)?.text.toString()
                if(filter!="null"){
                    filterRelation = if (filter!="Toute Relations"){
                        filter
                    } else{
                        ""
                    }
                    filterChanged()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }


        return root
    }

    private var filterSex = ""
    private var filterLocalisation =""
    private var filterRelation = ""
    fun filterChanged(){
        Toast.makeText(context, "Filtre: $filterSex $filterLocalisation $filterRelation", Toast.LENGTH_LONG).show()
    }

    inner class FilterAdapter(private val listFilter:MutableList<String>) : BaseAdapter(){
        override fun getCount(): Int = listFilter.size

        override fun getItem(p0: Int): Any = p0

        override fun getItemId(p0: Int): Long = p0.toLong()

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val rootView = LayoutInflater.from(context).inflate(R.layout.itemfilter, p2, false)

            val img = rootView.findViewById<ImageView>(R.id.imgItemFilter)
            val txt = rootView.findViewById<TextView>(R.id.txtItemFilter)
            txt.text = listFilter[p0]
            when(listFilter[p0]){
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

                "Monde / Partout"->img.setImageResource(R.drawable.iconsworld)
            }

            return rootView
        }

    }

}