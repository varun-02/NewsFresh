package com.example.newsfresh

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.newsfresh.R.id.recyclerview


class MainActivity : AppCompatActivity(), NewsItemClicked {
    private lateinit var madapter: NewsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<androidx.recyclerview.widget.RecyclerView>(recyclerview).layoutManager=LinearLayoutManager(this)
        val items=fetchData()
        madapter = NewsListAdapter(this)
        findViewById<androidx.recyclerview.widget.RecyclerView>(recyclerview).adapter=madapter
    }
    private fun fetchData(){
        val url ="https://newsapi.org/v2/top-headlines?country=in&apiKey=3cf89d0c29cb46578fefd38748aa3dae"
        val jsonObjectRequest:JsonObjectRequest=object:JsonObjectRequest(
            Request.Method.GET,url,null, {
                val newsJsonArray=it.getJSONArray("articles")
                val newsArray=ArrayList<News>()
                for(i in 0 until newsJsonArray.length())
                {
                    val newsJSONObject=newsJsonArray.getJSONObject(i)
                    val news = News(
                        newsJSONObject.getString("title"),
                        newsJSONObject.getString("author"),
                        newsJSONObject.getString("url"),
                        newsJSONObject.getString("urlToImage")
                    )
                    newsArray.add(news)
                }
                madapter.updateNews(newsArray)
            },
            {
                Toast.makeText(this, "null", Toast.LENGTH_LONG).show()
                //fetchData()
            }
        )
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["User-Agent"] = "Mozilla/5.0"
                return headers
            }
        }

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {

        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(this@MainActivity, Uri.parse(item.url))
    }

}
