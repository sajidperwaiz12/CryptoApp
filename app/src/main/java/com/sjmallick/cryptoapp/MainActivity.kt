package com.sjmallick.cryptoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sjmallick.cryptoapp.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var rvAdapter: RvAdapter
    private lateinit var data:ArrayList<Model>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        data = ArrayList<Model>()
        apiData
        rvAdapter = RvAdapter(this, data)

        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = rvAdapter

        binding.search.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                val filterData = ArrayList<Model>()

                for (item in data) {
                    if (item.name.lowercase(Locale.getDefault()).contains(p0.toString().lowercase(Locale.getDefault()))) {
                        filterData.add(item)
                    }
                }

                if (filterData.isEmpty()) {
                    Toast.makeText(this@MainActivity, "No Data Available", Toast.LENGTH_SHORT).show()
                } else {
                    rvAdapter.changeData(filterData)
                }
            }

        })
    }

    val apiData:Unit
        get() {
            val url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest"

            val queue = Volley.newRequestQueue(this)
            val jsonObjectRequest: JsonObjectRequest =

                object: JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    response ->
                    binding.progressBar.isVisible=false
                    try {
                        val dataArray = response.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)
                            val symbol = dataObject.getString("symbol")
                            val name = dataObject.getString("name")
                            val quote = dataObject.getJSONObject("quote")
                            val USD = quote.getJSONObject("USD")
                            val price = String.format("$" + "%.2f", USD.getDouble("price"))
                            data.add(Model(name, symbol, price))
                        }
                        rvAdapter.notifyDataSetChanged()
                    } catch (e:Exception) {
                        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                })
                {
                    override fun getHeaders(): MutableMap<String, String> {
//                        return super.getHeaders()
                        val headers = HashMap<String, String>()
                        headers["X-CMC_PRO_API_KEY"] = "3962702c-be31-4f8d-80ab-8f7d382b7b16"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        }
}