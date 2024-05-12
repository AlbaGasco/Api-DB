package com.example.easylearn

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import android.content.Intent
import android.util.Log

import android.database.sqlite.SQLiteDatabase

class MainActivity : AppCompatActivity() {
    private val userList = arrayListOf<User>()
    private lateinit var userAdapter: UserAdapter

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Abrir o crear la base de datos
        dbHelper = DatabaseHelper(this)
        db = dbHelper.writableDatabase

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        userAdapter = UserAdapter(userList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter

        val buttonFavorites = findViewById<Button>(R.id.buttonFavorites)
        buttonFavorites.setOnClickListener {
            startActivity(Intent(this@MainActivity, FavoritesActivity::class.java))
        }

        // Realiza la solicitud GET a la API para obtener la lista de personajes
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = "https://gsi.fly.dev/characters"
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener<JSONObject> { response ->
                val results = response.getJSONArray("results")
                for (i in 0 until results.length()) {
                    val character = results.getJSONObject(i)
                    val id = character.getInt("id")
                    val name = character.getString("name")
                    val weapon = character.getString("weapon")
                    val vision = character.getString("vision")

                    // Aquí puedes agregar logs para depurar
                    Log.d("MainActivity", "Character: $name, Weapon: $weapon, Vision: $vision")

                    // Agrega el personaje a userList
                    userList.add(User(id, name, weapon, vision))
                }
                // Notifica al adaptador sobre el cambio en los datos
                userAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                // Maneja errores de la solicitud
                error.printStackTrace()
            }
        )
        requestQueue.add(jsonObjectRequest)

        // Escucha el clic en el botón de marcar como favorito
        userAdapter.setOnItemClickListener(object : UserAdapter.OnItemClickListener {
            override fun onItemClick(id: Int, name: String) {
                dbHelper.addFavorite(id, name)
                // Actualiza el color o estilo visual del elemento en el RecyclerView
                val position = userList.indexOfFirst { it.id == id }
                if (position != -1) {
                    userList[position].isFavorite = true
                    userAdapter.notifyItemChanged(position)
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cerrar la base de datos cuando ya no se necesite
        db.close()
    }
}
