package com.lnd.RencontreAfricaine.ui.main

import android.os.Bundle
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
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lnd.RencontreAfricaine.R
import com.lnd.RencontreAfricaine.UserData
import com.lnd.RencontreAfricaine.utils.ProfileAdapter
import com.lnd.RencontreAfricaine.utils.SpinnerAdapter


class DiscoverFragment : Fragment() {
    companion object{
        val listUser: MutableList<UserData> = mutableListOf()
        val listUserFiltered: MutableList<UserData> = mutableListOf()
    }


    private lateinit var spinSex: Spinner
    private lateinit var spinLocalisation: Spinner
    private lateinit var spinRelation: Spinner
    private lateinit var recyclerProfile: RecyclerView
    private lateinit var btnShowMore : AppCompatButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_discover, container, false)
        btnShowMore = root.findViewById(R.id.btnShowMore)

        recyclerProfile = root.findViewById(R.id.recyclerProfile)
        recyclerProfile.adapter = ProfileAdapter(context, listUserFiltered)
        recyclerProfile.layoutManager = GridLayoutManager(context, 2)

        spinSex = root.findViewById(R.id.spinnerSex)
        spinSex.adapter = SpinnerAdapter(requireContext(), mutableListOf("Homme / Femme", "Homme", "Femme"))

        spinLocalisation = root.findViewById(R.id.spinnerLocalisation)
        spinLocalisation.adapter = SpinnerAdapter(requireContext(), mutableListOf("Monde / Partout", "Mali", "Algerie",
        "Senegal", "Burkina Faso", "Niger", "Nigeria", "Togo", "Benin", "Cape Vert", "Gambie",
        "Ghana", "Guinee Bissau"))

        spinRelation = root.findViewById(R.id.spinnerRelation)
        spinRelation.adapter = SpinnerAdapter(requireContext(), mutableListOf("Toute Relations", "Marriage",
            "Relation Serieuse", "Amour", "Amitie", "Relation Sexuel", "Inconnue"))

        spinSex.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val filter = p1?.findViewById<TextView>(R.id.txtItemSpinner)?.text.toString()
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
                val filter = p1?.findViewById<TextView>(R.id.txtItemSpinner)?.text.toString()
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
                val filter = p1?.findViewById<TextView>(R.id.txtItemSpinner)?.text.toString()
                if(filter!="null"){
                    filterRelation = if (filter!="Toute Relations"){
                        filter
                    }else{
                        ""
                    }
                    filterChanged()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

        addTestProfile()

        loadUsers(10)

        btnShowMore.isVisible = false
        btnShowMore.isEnabled = false
        btnShowMore.setOnClickListener {
            btnShowMore.isVisible = false
            btnShowMore.isEnabled = false
            loadUsers(10)
        }

        return root
    }

    private fun addTestProfile() {
        val user1  = UserData("id1", "phone1",
            "nom1", "prenom1", 99, "Homme", "", "",
            "Marriage", "Mali", "Francais")

        val user2  = UserData("id2", "phone2",
            "nom2", "prenom2", 99, "Femme", "", "",
            "Amour", "Algerie", "English")

        listUser.add(user1)
        listUser.add(user2)
        listUserFiltered.add(user1)
        listUserFiltered.add(user2)
    }


    private fun loadUsers(nbr:Int) {
        FirebaseDatabase.getInstance().reference.child("Users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        var i=0
                        for (h0 in snapshot.children){
                            if (h0.child("userData").child("nom").exists()){

                                //The user is already loaded
                                var isNewUser = true
                                for (user in listUser){
                                    val newUserID = h0.child("userData").child("id").value.toString()
                                    if (user.id==newUserID){
                                         isNewUser = false
                                    }
                                }
                                if (!isNewUser){ continue }

                                //The user respect the filter
                                if(filterLocalisation.isNotEmpty() || filterSex.isNotEmpty()
                                    || filterRelation.isNotEmpty()){
                                    if(filterSex!=h0.child("userData").child("sexe").value.toString()){continue}
                                    if(filterLocalisation!=h0.child("userData").child("localisation").value.toString()){continue}
                                    if(filterRelation!=h0.child("userData").child("relation").value.toString()){continue}
                                }

                                //load the user data
                                val h1 = h0.child("userData")
                                val id = h0.key.toString()
                                val phone = h1.child("phone").value.toString()
                                val nom = h1.child("nom").value.toString()
                                val prenom = h1.child("prenom").value.toString()
                                val age = h1.child("age").value.toString().toInt()
                                val sexe = h1.child("sexe").value.toString()
                                val mdp = h1.child("mdp").value.toString()
                                val imgProfileUrl = h1.child("imgProfileUrl").value.toString()

                                val relation = h1.child("relation").value.toString()
                                val localisation = h1.child("localisation").value.toString()
                                val language = h1.child("language").value.toString()

                                val userData = UserData(id, phone, nom, prenom, age, sexe, mdp, imgProfileUrl, relation, localisation, language)
                                listUser.add(userData)
                                listUserFiltered.add(userData)
                                i++
                                if (i==nbr){
                                    break
                                }
                            }
                        }
                        recyclerProfile.adapter?.notifyDataSetChanged()
                        btnShowMore.isVisible = true
                        btnShowMore.isEnabled = true

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private var filterSex = ""
    private var filterLocalisation =""
    private var filterRelation = ""
    fun filterChanged(){
        Toast.makeText(context, "Filtre: $filterSex $filterLocalisation $filterRelation", Toast.LENGTH_LONG).show()
        listUserFiltered.clear()
        for ((i, userData) in listUser.withIndex()){
            if (filterSex.isNotEmpty() && userData.sexe!= filterSex){continue}
            if (filterLocalisation.isNotEmpty() && userData.localisation!= filterLocalisation){continue}
            if (filterRelation.isNotEmpty() && userData.relation!= filterRelation){continue}
            listUserFiltered.add(userData)
        }
        recyclerProfile.adapter?.notifyDataSetChanged()
    }

}